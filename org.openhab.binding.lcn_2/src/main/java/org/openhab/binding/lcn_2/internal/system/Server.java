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

package org.openhab.binding.lcn_2.internal.system;

import java.util.ArrayList;

import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.PrioritizedObject;
import org.openhab.binding.lcn_2.internal.message.StopServerCommand;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;

/*----------------------------------------------------------------------------*/

public class Server implements ISystem {

    public Server() {
        data = new ServerExchangeData(this);
        serverThread = new ServerThread(data);
    }

    @Override
    public void register(final ISystem system) {
        system.register(this);
    }

    @Override
    public void start() {
        serverThread.start();
    }

    @Override
    public void stop() {
        try {
            send(Priority.HIGHEST, StopServerCommand.getInstance());
        } catch (InterruptedException e) {
            serverThread.interrupt();
        }
    }

    @Override
    public void join() throws InterruptedException {
        serverThread.join();
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        send(priority, message);
    }

    @Override
    public void register(final INode node, final MessageType messageType, final IAddress address) {
        final IMessageKey messageKey = new MessageKeyImpl(messageType, address, ValueType.UNKNOWN);
        if (!data.observers2.containsKey(messageKey)) {
            data.observers2.put(messageKey, new ArrayList<INode>());
        }
        data.observers2.get(messageKey).add(node);
    }

    @Override
    public void register(final INode node, final IMessageKey messageKey) {
        if (!data.observers3.containsKey(messageKey)) {
            data.observers3.put(messageKey, new ArrayList<INode>());
        }
        data.observers3.get(messageKey).add(node);
    }

    @Override
    public void register(final INode node) {
        data.observers1.add(node);
    }

    @Override
    public void send(final Priority priority, final IMessage message) throws InterruptedException {
        data.queue.put(new PrioritizedObject<IMessage>(priority, message));
    }

    @Override
    public IMessage getLast(final IMessageKey messageKey) {
        if (data.messages.containsKey(messageKey)) {
            return data.messages.get(messageKey);
        } else {
            return null;
        }
    }

    private final ServerExchangeData data;

    private final Thread serverThread;
}

/*----------------------------------------------------------------------------*/
