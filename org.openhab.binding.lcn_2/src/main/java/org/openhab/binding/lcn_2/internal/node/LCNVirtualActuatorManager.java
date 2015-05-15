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

import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openhab.binding.lcn_2.internal.address.LCNAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.IVirtualActuatorBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.TimeStatus;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNVirtualActuatorManager implements INode {

    @Override
    public void register(final ISystem system) {
        system.register(this, MessageType.COMMAND, LCNAddress.getInstance());
        system.register(this, TimeStatus.getMessageKey());
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void join() throws InterruptedException {
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        if (ValueType.TIME == message.getKey().getValueType()) {
            if (message instanceof TimeStatus) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(((TimeStatus) message).getDateTime());
                final int seconds = calendar.get(Calendar.SECOND);
                if (seconds % 10 == 3) {
                    // fill list of items to resend
                    if (commandsToResend.isEmpty()) {
                        commandsToResend.putAll(commandDictionary);
                    }

                    // resend next item
                    if (!commandsToResend.isEmpty()) {
                        final ILCNUnitAddress firstKey = commandsToResend.firstKey();
                        final IMessage messageToResend = commandsToResend.get(firstKey);
                        commandsToResend.remove(firstKey);
                        send(system, messageToResend);
                    }
                }
            }
        } else {
            final IAddress address = message.getKey().getAddress();
            if (address instanceof ILCNUnitAddress) {
                final ILCNUnitAddress unitAddress = (ILCNUnitAddress) address;
                if (unitAddress.getBindingBridge() instanceof IVirtualActuatorBindingBridge) {
                    // check if it is a new command
                    boolean isNew = true;
                    if (commandDictionary.containsKey(unitAddress)) {
                        final IMessage storedMessage = commandDictionary.get(unitAddress);
                        if (storedMessage == message) { // same reference?
                            isNew = false;
                        }
                    }

                    // update dictionaries
                    if (isNew) {
                        logger.debug("Store item in virtual actuator manager: " + message.getKey().getAddress().getName() + " => "
                                + message.asText());

                        if (commandsToResend.containsKey(unitAddress)) {
                            commandsToResend.put(unitAddress, message);
                        }
                        commandDictionary.put(unitAddress, message);
                    }
                }
            }
        }
    }

    private void send(final ISystem system, final IMessage commandMessage) throws InterruptedException {
        if (MessageType.COMMAND == commandMessage.getKey().getMessageType()) {
            final IMessage statusMessage = commandMessage.getCopy(new MessageKeyImpl(MessageType.STATUS, commandMessage.getKey()
                    .getAddress(), commandMessage.getKey().getValueType()));

            // resend command and status (HIGHEST priority to avoid interruption)
            system.send(Priority.HIGHEST, commandMessage);
            system.send(Priority.HIGHEST, statusMessage);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNVirtualActuatorManager.class);

    private final SortedMap<ILCNUnitAddress, IMessage> commandDictionary = new TreeMap<ILCNUnitAddress, IMessage>();

    private final SortedMap<ILCNUnitAddress, IMessage> commandsToResend = new TreeMap<ILCNUnitAddress, IMessage>();
}

/*----------------------------------------------------------------------------*/
