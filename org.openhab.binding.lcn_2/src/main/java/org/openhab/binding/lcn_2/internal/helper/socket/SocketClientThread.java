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

package org.openhab.binding.lcn_2.internal.helper.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.openhab.binding.lcn_2.internal.address.SocketClientAddress;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.helper.IClientNotifier;
import org.openhab.binding.lcn_2.internal.helper.IReceivedDataNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class SocketClientThread extends Thread implements IHasAddress {

    public SocketClientThread(final ISystem system, final IClientNotifier clientNotifier, final IReceivedDataNotifier dataNotifer,
            final ISocketHostAddress hostAddress) {
        this.system = system;
        this.clientNotifier = clientNotifier;
        this.dataNotifer = dataNotifer;
        this.hostAddress = hostAddress;
        this.clientAddress = new SocketClientAddress(hostAddress);
    }

    public SocketClientThread(final ISystem system, final IReceivedDataNotifier dataNotifer, final ISocketHostAddress hostAddress) {
        this(system, new IClientNotifier() {

            @Override
            public void onStartedClient() {
            }

            @Override
            public void onClosedClient() {
            }
        }, dataNotifer, hostAddress);
    }

    @Override
    public void run() {
        while (true) {
            // create connection first
            if (isInterrupted()) {
                break; // stop thread
            }
            try {
                createClientSocket();
            } catch (final IOException e1) {
                try {
                    logger.error("Could not create connection to '" + clientAddress.getName() + "'.");
                    Thread.sleep(millisToWaitAfterError);
                } catch (final InterruptedException e2) {
                    // do nothing here
                }

                continue; // re-connect
            }
            if (isInterrupted()) {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (final Exception e) {
                    // do nothing
                }

                break; // stop thread
            }

            // read data
            logger.info("Connection to '" + clientAddress.getName() + "' has been started ...");
            try {
                clientNotifier.onStartedClient();

                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                final char[] buffer = new char[1024];
                while (true) {
                    final int count = bufferedReader.read(buffer, 0, buffer.length);
                    if (isInterrupted() || clientSocket.isClosed()) {
                        break;
                    }
                    if (count > 0) {
                        dataNotifer.onReceivedData(system, clientAddress, buffer, count);
                    }
                }
            } catch (final Exception e) {
                try {
                    Thread.sleep(millisToWaitAfterError);
                } catch (final InterruptedException e2) {
                    // do nothing here
                }
            } finally {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (final Exception e) {
                    // do nothing
                }

                clientNotifier.onClosedClient();
            }
            logger.info("Connection to '" + clientAddress.getName() + "' has been ended.");
        }
    }

    public synchronized void send(final char[] buffer, final int count) {
        if (null != clientSocket && !clientSocket.isClosed()) {
            try {
                if (buffer.length > 0 && count > 0) {
                    final OutputStreamWriter streamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
                    streamWriter.write(buffer, 0, count);
                    streamWriter.flush();
                }
            } catch (final IOException e) {
                interruptAndCloseConnection();
            }
        }
    }

    public synchronized void interruptAndCloseConnection() {
        interrupt(); // try to interrupt thread first

        try {
            if (null != clientSocket && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (final Exception e) {
            // do nothing
        }
    }

    public synchronized void disconnect() {
        if (null != clientSocket && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (final IOException e) {
                // do nothing
            }
        }
    }

    @Override
    public SocketClientAddress getAddress() {
        return clientAddress;
    }

    private synchronized void createClientSocket() throws IOException {
        if (null != clientSocket && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        clientSocket = new Socket(hostAddress.getHost(), hostAddress.getPort());
    }

    private static final Logger logger = LoggerFactory.getLogger(SocketClientThread.class);

    private static final long millisToWaitAfterError = 2000;

    private final ISystem system;

    private final IClientNotifier clientNotifier;

    private final IReceivedDataNotifier dataNotifer;

    private final ISocketHostAddress hostAddress;

    private final SocketClientAddress clientAddress;

    private Socket clientSocket = null;
}

/*----------------------------------------------------------------------------*/
