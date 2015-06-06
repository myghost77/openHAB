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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.openhab.binding.lcn_2.internal.binding.LCNConfiguration;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.IReceivedDataNotifier;
import org.openhab.binding.lcn_2.internal.helper.PrioritizedObject;
import org.openhab.binding.lcn_2.internal.helper.TimeThread;
import org.openhab.binding.lcn_2.internal.helper.socket.SocketClientThread;
import org.openhab.binding.lcn_2.internal.message.TextMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;

/*----------------------------------------------------------------------------*/

public class PCHKCommunicator implements INode, IReceivedDataNotifier, IHasAddress {

    @Override
    public void register(final ISystem system) {
        if (null == socketClientThread) {
            socketClientThread = new SocketClientThread(system, this, LCNConfiguration.getInstance());
        }

        system.register(this, MessageType.COMMAND, getAddress());
    }

    @Override
    public void start() {
        // socket client thread
        if (null != socketClientThread) {
            socketClientThread.start();
        }

        // sender thread
        senderThread.start();
    }

    @Override
    public void stop() {
        // socket client thread
        if (null != socketClientThread) {
            socketClientThread.interruptAndCloseConnection();
        }

        // sender thread
        senderThread.interrupt();
        try {
            senderQueue.put(new PrioritizedObject<String>(Priority.HIGHEST, "")); // put any text
        } catch (final InterruptedException e) {
            // do nothing
        }
    }

    @Override
    public void join() throws InterruptedException {
        // socket client thread
        if (null != socketClientThread) {
            socketClientThread.join();
        }

        // sender thread
        senderThread.join();
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        if (null != socketClientThread) {
            if (message instanceof TextMessage) {
                final String sendText = ((TextMessage) message).getValue() + endOfCommandCharacter;
                senderQueue.put(new PrioritizedObject<String>(priority, sendText));
            }
        }
    }

    @Override
    public void onReceivedData(final ISystem system, final IAddress sourceAddress, final char[] buffer, final int count)
            throws InterruptedException {
        receiverBuffer += new String(buffer, 0, count);

        while (true) {
            final int index = receiverBuffer.indexOf(endOfCommandCharacter);
            if (index >= 0) {
                final String receivedText = receiverBuffer.substring(0, index).trim();
                if (!receivedText.isEmpty()) {
                    if (receivedText.equals(userNameRequest)) {
                        final String userName = LCNConfiguration.getInstance().getPCHKUsername() + endOfCommandCharacter;
                        socketClientThread.send(userName.toCharArray(), userName.length());
                    } else if (receivedText.equals(passwordRequest)) {
                        final String password = LCNConfiguration.getInstance().getPCHKPassword() + endOfCommandCharacter;
                        socketClientThread.send(password.toCharArray(), password.length());
                    } else if (receivedText.equals(authentificationErrorStr) || receivedText.equals(licenseErrorStr)) {
                        socketClientThread.disconnect();
                    } else {
                        system.send(Priority.HIGH, new TextMessage(new MessageKeyImpl(MessageType.STATUS, sourceAddress, ValueType.TEXT),
                                receivedText));
                    }
                }
                receiverBuffer = receiverBuffer.substring(index + 1);
            } else {
                break;
            }
        }
    }

    @Override
    public IAddress getAddress() {
        if (null != socketClientThread) {
            return socketClientThread.getAddress();
        } else {
            return null;
        }
    }

    private class SenderThread extends Thread {

        @Override
        public void run() {
            if (null != socketClientThread) {
                while (true) {
                    // read next text to send
                    final String sendText;
                    try {
                        sendText = senderQueue.take().getObject();
                    } catch (final InterruptedException e) {
                        break; // stop thread
                    }
                    if (isInterrupted()) {
                        break; // stop thread
                    }

                    // check timing
                    if (timings.size() >= maxCommandsPerPeriod) {
                        final long lastTime = timings.remove(0);
                        final long currTime = TimeThread.getCurrentMillis();
                        final long waitTime = durationOfPeriodInMS - (currTime - lastTime);
                        if (waitTime > 0L) {
                            try {
                                Thread.sleep(waitTime);
                            } catch (final InterruptedException e) {
                                break; // stop thread
                            }
                        }
                    }

                    // send text and update timing
                    socketClientThread.send(sendText.toCharArray(), sendText.length());
                    if (isInterrupted()) {
                        break; // stop thread
                    }
                    timings.add(TimeThread.getCurrentMillis());
                }
            }
        }

        private static final int maxCommandsPerPeriod = 5;

        private static final long durationOfPeriodInMS = 1001L;

        private List<Long> timings = new ArrayList<Long>();
    }

    private static final char endOfCommandCharacter = '\n';

    private static final String userNameRequest = "Username:";

    private static final String passwordRequest = "Password:";

    private static final String authentificationErrorStr = "Authentification failed.";

    private static final String licenseErrorStr = "$err:(license?)";

    private final SenderThread senderThread = new SenderThread();

    private final BlockingQueue<PrioritizedObject<String>> senderQueue = new PriorityBlockingQueue<PrioritizedObject<String>>(256);

    private SocketClientThread socketClientThread = null;

    private String receiverBuffer = "";
}

/*----------------------------------------------------------------------------*/
