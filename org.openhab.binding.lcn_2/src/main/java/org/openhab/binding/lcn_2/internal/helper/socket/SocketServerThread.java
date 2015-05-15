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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openhab.binding.lcn_2.internal.address.SocketServerAddress;
import org.openhab.binding.lcn_2.internal.address.SocketServerClientAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.helper.IReceivedDataNotifier;
import org.openhab.binding.lcn_2.internal.helper.IServerClientNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class SocketServerThread extends Thread implements IHasAddress {

    private class ClientThread extends Thread {

        public ClientThread(final IAddress clientAddress, final Socket clientSocket) {
            this.clientAddress = clientAddress;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            logger.info("A client has been connected to server: " + clientAddress.getName());

            try {
                clientNotifier.onStartedServerClient(clientAddress);

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
                // do nothing
            } finally {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (final Exception e) {
                    // do nothing
                }

                clientNotifier.onClosedServerClient(clientAddress);
            }

            logger.info("A client was disconnected from server: " + clientAddress.getName());
        }

        public void send(final char[] buffer, final int count) {
            if (!clientSocket.isClosed()) {
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

        public void interruptAndCloseConnection() {
            interrupt(); // try to interrupt thread first

            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (final Exception e) {
                // do nothing
            }
        }

        public boolean isClosed() {
            return clientSocket.isClosed();
        }

        private final IAddress clientAddress;

        private final Socket clientSocket;
    }

    public SocketServerThread(final ISystem system, final IServerClientNotifier clientNotifier, final IReceivedDataNotifier dataNotifer,
            final int port) {
        this.system = system;
        this.clientNotifier = clientNotifier;
        this.dataNotifer = dataNotifer;
        this.port = port;
        this.baseAddress = new SocketServerAddress(port);
    }

    public SocketServerThread(final ISystem system, final IReceivedDataNotifier dataNotifer, final int port) {
        this(system, new IServerClientNotifier() {

            @Override
            public boolean checkAcceptServerClient(final IAddress clientAddress) {
                return true;
            }

            @Override
            public void onStartedServerClient(final IAddress clientAddress) {
            }

            @Override
            public void onClosedServerClient(final IAddress clientAddress) {
            }
        }, dataNotifer, port);
    }

    @Override
    public void run() {
        try {
            // create server socket first
            while (true) {
                try {
                    serverSocket = new ServerSocket(port);
                    break;
                } catch (final IOException e1) {
                    logger.error("Could not create server socket on port " + port + ".");
                    try {
                        Thread.sleep(millisToWaitAfterError);
                    } catch (final InterruptedException e2) {
                        // do nothing here
                    }

                    serverSocket = null; // reset socket
                }

                if (isInterrupted()) {
                    return; // stop thread
                }
            }

            // accept clients
            try {
                while (true) {
                    try {
                        if (isInterrupted() || serverSocket.isClosed()) {
                            break;
                        }

                        cleanUpClientThreads();

                        final Socket clientSocket = serverSocket.accept();
                        if (isInterrupted()) {
                            clientSocket.close();
                            break;
                        }

                        final IAddress clientAddress = new SocketServerClientAddress(baseAddress);
                        if (clientNotifier.checkAcceptServerClient(clientAddress)) {
                            final ClientThread clientThread = new ClientThread(clientAddress, clientSocket);
                            clientThread.start();
                            synchronized (clientThreads) {
                                clientThreads.put(clientAddress, clientThread);
                            }
                        } else {
                            clientSocket.close();
                        }
                    } catch (final IOException e1) {
                        logger.error("IO-error in server on port " + port + ".");
                        try {
                            Thread.sleep(millisToWaitAfterError);
                        } catch (final InterruptedException e2) {
                            // do nothing here
                        }
                    }
                }
            } finally {
                closeClientConnections();
            }
        } finally {
            try {
                if (null != serverSocket && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (final IOException e) {
                // do nothing
            }
        }
    }

    public boolean send(final IAddress address, final char[] buffer, final int count) {
        synchronized (clientThreads) {
            if (null != address) {
                if (clientThreads.containsKey(address)) {
                    clientThreads.get(address).send(buffer, count);
                    return true;
                } else {
                    return false;
                }
            } else {
                // send stuff to all clients
                for (final ClientThread clientThread : clientThreads.values()) {
                    clientThread.send(buffer, count);
                }
                return true;
            }
        }
    }

    public void interruptAndCloseConnection() {
        interrupt(); // try to interrupt thread first

        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (final Exception e) {
            // do nothing
        }
    }

    public void closeClientConnections() {
        synchronized (clientThreads) {
            for (final ClientThread clientThread : clientThreads.values()) {
                clientThread.interruptAndCloseConnection();
                try {
                    clientThread.join();
                } catch (final InterruptedException e) {
                    // do nothing
                }
            }
            clientThreads.clear();
        }
    }

    @Override
    public SocketServerAddress getAddress() {
        return baseAddress;
    }

    private void cleanUpClientThreads() {
        synchronized (clientThreads) {
            final List<IAddress> itemsToBeRemoved = new ArrayList<IAddress>();
            for (final IAddress clientAddress : clientThreads.keySet()) {
                final ClientThread clientThread = clientThreads.get(clientAddress);
                if (clientThread.isClosed()) {
                    try {
                        clientThread.join();
                    } catch (final InterruptedException e) {
                        // do nothing
                    }

                    itemsToBeRemoved.add(clientAddress);
                }
            }

            for (final IAddress clientAddress : itemsToBeRemoved) {
                clientThreads.remove(clientAddress);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SocketServerThread.class);

    private static final long millisToWaitAfterError = 5000;

    private final SortedMap<IAddress, ClientThread> clientThreads = new TreeMap<IAddress, ClientThread>();

    private final ISystem system;

    private final IServerClientNotifier clientNotifier;

    private final IReceivedDataNotifier dataNotifer;

    private final int port;

    private final SocketServerAddress baseAddress;

    private ServerSocket serverSocket = null;
}

/*----------------------------------------------------------------------------*/
