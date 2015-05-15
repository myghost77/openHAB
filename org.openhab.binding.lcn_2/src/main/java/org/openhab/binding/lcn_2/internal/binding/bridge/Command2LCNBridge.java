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

package org.openhab.binding.lcn_2.internal.binding.bridge;

import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BaseMessage;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class Command2LCNBridge implements IAddressBindingBridge {

    public static Command2LCNBridge getInstance() {
        return instance;
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends Command> commandType : item.getAcceptedCommandTypes()) {
            if (commandType.equals(OnOffType.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAllowedState(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
            if (dataType.equals(OnOffType.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGroupAllowed() {
        return true;
    }

    @Override
    public IMessage createMessage(final ILCNUnitAddress unitAddress, final Command command) {
        if (command instanceof OnOffType) {
            if (((OnOffType) command).equals(OnOffType.ON)) {
                return new BaseMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.COMMAND)) {
                    @Override
                    public boolean hasSameValue(IMessage other) {
                        return true; // has always the same value
                    }

                    @Override
                    public String asText() {
                        return ""; // no text
                    }

                    @Override
                    public IMessage getCopy(final IMessageKey key) {
                        return null;
                    }
                };
            }
        }
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.ACKNOWLEDGE == message.getKey().getValueType()
                && message instanceof BooleanMessage) {
            final boolean positiveAck = ((BooleanMessage) message).getValue();
            if (!positiveAck) {
                logger.error("Negative acknowledgement for item '" + item.getName() + "'.");
            }
            return OnOffType.OFF; // reset command item to OFF in any case
        }
        return null;
    }

    private Command2LCNBridge() {
        // due to singleton
    }

    private static final Logger logger = LoggerFactory.getLogger(Command2LCNBridge.class);

    private static final Command2LCNBridge instance = new Command2LCNBridge();
}

/*----------------------------------------------------------------------------*/
