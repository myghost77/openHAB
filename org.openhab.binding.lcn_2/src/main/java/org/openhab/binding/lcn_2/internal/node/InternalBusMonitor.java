/******************************************************************************/
/*               OpenHAB binding for LCN (Local Control Network)              */
/*----------------------------------------------------------------------------*/
/*                                                                            */
/*               Copyright (c) 2010-2015, openHAB.org and others.             */
/*                                                                            */
/*                         Author: Bernd Roffmann                             */
/*                                                                            */
/* All rights reserved. This program and the accompanying materials           */
/* are made available under the terms of the Eclipse Public License v1.0      */
/* which accompanies this distribution, and is available at                   */
/* http://www.eclipse.org/legal/epl-v10.html                                  */
/*                                                                            */
/******************************************************************************/

package org.openhab.binding.lcn_2.internal.node;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openhab.binding.lcn_2.internal.binding.LCNConfiguration;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.helper.IReceivedDataNotifier;
import org.openhab.binding.lcn_2.internal.helper.socket.SocketServerThread;

/*----------------------------------------------------------------------------*/

public class InternalBusMonitor implements INode, IReceivedDataNotifier {

    @Override
    public void register(final ISystem system) {
        if (null == socketServerThread) {
            final int internalBusMonitorPort = LCNConfiguration.getInstance().getInternalBusMonitorPort();
            if (internalBusMonitorPort > 0) {
                socketServerThread = new SocketServerThread(system, this, internalBusMonitorPort);
            }
        }

        system.register(this);
    }

    @Override
    public void start() {
        if (null != socketServerThread) {
            socketServerThread.start();
        }
    }

    @Override
    public void stop() {
        if (null != socketServerThread) {
            socketServerThread.interruptAndCloseConnection();
        }
    }

    @Override
    public void join() throws InterruptedException {
        if (null != socketServerThread) {
            socketServerThread.join();
        }
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        if (null != socketServerThread) {
            final long nextCounter = getNextCounter();
            final List<IAddress> clientsToBeRemoved = new ArrayList<IAddress>();
            final String messageString = createMessageString(message, nextCounter);
            synchronized (clientAddressesWithEOL) {
                for (final IAddress clientAddress : clientAddressesWithEOL.keySet()) {
                    final String EOL = clientAddressesWithEOL.get(clientAddress);
                    if (null != EOL) {
                        final String line = messageString + EOL;
                        if (!socketServerThread.send(clientAddress, line.toCharArray(), line.length())) {
                            clientsToBeRemoved.add(clientAddress);
                        }
                    } else {
                        clientsToBeRemoved.add(clientAddress);
                    }
                }
            }

            // remove clients which are not connected anymore
            if (!clientsToBeRemoved.isEmpty()) {
                synchronized (clientsToBeRemoved) {
                    for (final IAddress clientAddress : clientsToBeRemoved) {
                        clientAddressesWithEOL.remove(clientAddress);
                    }
                }
            }
        }
    }

    @Override
    public void onReceivedData(final ISystem system, final IAddress sourceAddress, final char[] buffer, final int count)
            throws InterruptedException {
        final String receivedData = new String(buffer, 0, count);
        final String EOL;
        if (receivedData.indexOf(windowsEOL) >= 0) {
            EOL = windowsEOL;
        } else if (receivedData.indexOf(unixEOL) >= 0) {
            EOL = unixEOL;
        } else {
            EOL = null;
        }
        if (EOL != null) {
            synchronized (clientAddressesWithEOL) {
                clientAddressesWithEOL.put(sourceAddress, EOL);
            }
        }
    }

    private String createMessageString(final IMessage message, final long nextCounter) {
        final StringBuilder line = new StringBuilder();
        line.append(nextCounter);
        line.append("|");
        line.append(message.getKey().getMessageType().asString());
        line.append("|");
        line.append(message.getKey().getAddress().getName());
        line.append("|");
        line.append(message.getKey().getValueType().asString());
        line.append("|");
        line.append(message.asText());
        line.append("|");
        return line.toString();
    }

    private long getNextCounter() {
        if (++counter < 0L) { // overflow?
            counter = 1L;
        }
        return counter;
    }

    private static final String windowsEOL = "\r\n";

    private static final String unixEOL = "\n";

    private final SortedMap<IAddress, String> clientAddressesWithEOL = new TreeMap<IAddress, String>();

    private long counter = 0L;

    private SocketServerThread socketServerThread = null;
}

/*----------------------------------------------------------------------------*/
