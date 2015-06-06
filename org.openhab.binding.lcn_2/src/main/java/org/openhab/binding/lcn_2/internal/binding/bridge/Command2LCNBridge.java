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
import org.openhab.binding.lcn_2.internal.definition.IEnum;
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

    public enum CommandResetType implements IEnum {
        NEVER, POSITIVE_ACKN, NEGATIVE_ACKN, ANY_ACKN, ALWAYS;

        public static CommandResetType[] asList() {
            return new CommandResetType[] { NEVER, POSITIVE_ACKN, NEGATIVE_ACKN, ANY_ACKN, ALWAYS };
        }

        @Override
        public String asString() {
            switch (this) {
            case NEVER:
                return "never";
            case POSITIVE_ACKN:
                return "positiveAckn";
            case NEGATIVE_ACKN:
                return "negativeAckn";
            case ANY_ACKN:
                return "anyAckn";
            case ALWAYS:
                return "always";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case NEVER:
                return 0;
            case POSITIVE_ACKN:
                return 1;
            case NEGATIVE_ACKN:
                return 2;
            case ANY_ACKN:
                return 3;
            case ALWAYS:
                return 4;
            default:
                throw new RuntimeException();
            }
        }
    }

    public static Command2LCNBridge getInstance(final CommandResetType resetType) {
        switch (resetType) {
        case NEVER:
            return neverInstance;
        case POSITIVE_ACKN:
            return positiveAcknInstance;
        case NEGATIVE_ACKN:
            return negativeAcknInstance;
        case ANY_ACKN:
            return anyAcknInstance;
        case ALWAYS:
            return alwaysInstance;
        default:
            return null;
        }
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
    public State createShadowState(final ILCNUnitAddress unitAddress, final Command command) {
        switch (resetType) {
        case ALWAYS:
            return OnOffType.OFF; // reset command item to OFF
        default:
            return null;
        }
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.ACKNOWLEDGE == message.getKey().getValueType()
                && message instanceof BooleanMessage) {
            final boolean positiveAck = ((BooleanMessage) message).getValue();
            if (!positiveAck) {
                logger.error("Negative acknowledgement for item '" + item.getName() + "'.");
            }

            final boolean reset;
            switch (resetType) {
            case POSITIVE_ACKN:
                reset = positiveAck;
                break;
            case NEGATIVE_ACKN:
                reset = !positiveAck;
                break;
            case ANY_ACKN:
                reset = true;
                break;
            default:
                reset = false;
                break;
            }

            if (reset) {
                return OnOffType.OFF; // reset command item to OFF in any case
            }
        }
        return null;
    }

    private Command2LCNBridge(final CommandResetType resetType) {
        this.resetType = resetType;
    }

    private static final Logger logger = LoggerFactory.getLogger(Command2LCNBridge.class);

    private static final Command2LCNBridge neverInstance = new Command2LCNBridge(CommandResetType.NEVER);

    private static final Command2LCNBridge positiveAcknInstance = new Command2LCNBridge(CommandResetType.POSITIVE_ACKN);

    private static final Command2LCNBridge negativeAcknInstance = new Command2LCNBridge(CommandResetType.NEGATIVE_ACKN);

    private static final Command2LCNBridge anyAcknInstance = new Command2LCNBridge(CommandResetType.ANY_ACKN);

    private static final Command2LCNBridge alwaysInstance = new Command2LCNBridge(CommandResetType.ALWAYS);

    private final CommandResetType resetType;
}

/*----------------------------------------------------------------------------*/
