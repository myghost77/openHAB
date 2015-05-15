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

import java.util.HashSet;
import java.util.Set;

import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.PrioritizedObject;
import org.openhab.binding.lcn_2.internal.message.StopServerCommand;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class ServerThread extends Thread {

    public ServerThread(final ServerExchangeData data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // read next message group and do some checks
                final PrioritizedObject<IMessage> nextMessage;
                nextMessage = data.queue.take();
                if (this.isInterrupted()) {
                    return; // leave thread
                }
                if (!nextMessage.hasObject()) {
                    continue;
                }
                if (nextMessage.getObject() == StopServerCommand.getInstance()) {
                    data.system.stop();
                    return; // leave thread
                }

                // notify nodes
                for (final INode node : getNodesToBeNotified(nextMessage)) {
                    try {
                        node.notify(data.system, nextMessage.getObject(), nextMessage.getPriority());
                    } catch (final Exception e) {
                        logger.error(e.toString()); // log exception
                    }
                }
            }
        } catch (final InterruptedException e) {
            data.system.stop();
        }
    }

    private Set<INode> getNodesToBeNotified(final PrioritizedObject<IMessage> message) {
        final Set<INode> result = new HashSet<INode>();
        if (message.hasObject()) {
            final IMessageKey messageKey = message.getObject().getKey();

            // check 1st observers
            for (final INode node : data.observers1) {
                result.add(node);
            }

            // check 2nd observers
            IAddress tempAddress = messageKey.getAddress();
            while (null != tempAddress) {
                final IMessageKey tempMessageKey = new MessageKeyImpl(messageKey.getMessageType(), tempAddress, ValueType.UNKNOWN);
                if (data.observers2.containsKey(tempMessageKey)) {
                    result.addAll(data.observers2.get(tempMessageKey));
                }
                tempAddress = tempAddress.getParentAddress();
            }

            // check 3rd observers
            if (data.observers3.containsKey(messageKey)) {
                result.addAll(data.observers3.get(messageKey));
            }
        }
        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    private final ServerExchangeData data;
}

/*----------------------------------------------------------------------------*/
