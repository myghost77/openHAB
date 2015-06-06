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
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class DimActuatorBridge implements IAddressBindingBridge {

    public static DimActuatorBridge getInstance() {
        return instance;
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends Command> commandType : item.getAcceptedCommandTypes()) {
            if (commandType.equals(OnOffType.class) || commandType.equals(IncreaseDecreaseType.class)
                    || commandType.equals(PercentType.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAllowedState(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
            if (dataType.equals(OnOffType.class) || dataType.equals(PercentType.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isGroupAllowed() {
        return false;
    }

    @Override
    public IMessage createMessage(final ILCNUnitAddress unitAddress, final Command command) {
        if (command instanceof OnOffType) {
            switch ((OnOffType) command) {
            case ON:
                return new BooleanMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.PERCENT), true);
            case OFF:
                return new BooleanMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.PERCENT), false);
            }
        }
        if (command instanceof IncreaseDecreaseType) {
            switch ((IncreaseDecreaseType) command) {
            case INCREASE:
                return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.SUMMAND), +2);
            case DECREASE:
                return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.SUMMAND), -2);
            }
        }
        if (command instanceof PercentType) {
            return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.PERCENT),
                    ((PercentType) command).intValue());
        }
        return null;
    }

    @Override
    public State createShadowState(final ILCNUnitAddress unitAddress, final Command command) {
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.PERCENT == message.getKey().getValueType()
                && message instanceof NumberMessage) {
            Boolean useDimmer = null;
            for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
                if (dataType.equals(PercentType.class)) {
                    useDimmer = true;
                    break; // preferred => leave immediately
                }

                if (dataType.equals(OnOffType.class)) {
                    useDimmer = false;
                }
            }
            if (null == useDimmer) {
                return null;
            }

            final int percent = ((NumberMessage) message).getValue();
            if (useDimmer.booleanValue()) {
                return new PercentType(percent);
            } else {
                if (percent > 0) {
                    return OnOffType.ON;
                } else {
                    return OnOffType.OFF;
                }
            }
        } else {
            return null;
        }
    }

    private DimActuatorBridge() {
        // due to singleton
    }

    private static final DimActuatorBridge instance = new DimActuatorBridge();
}

/*----------------------------------------------------------------------------*/
