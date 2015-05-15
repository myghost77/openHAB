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
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class IntegerActuatorBridge implements IAddressBindingBridge {

    public static IntegerActuatorBridge getInstance() {
        return instance;
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends Command> commandType : item.getAcceptedCommandTypes()) {
            if (commandType.equals(DecimalType.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkAllowedState(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
            if (dataType.equals(DecimalType.class)) {
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
        if (command instanceof DecimalType) {
            return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.INTEGER),
                    ((DecimalType) command).intValue());
        }
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.INTEGER == message.getKey().getValueType()
                && message instanceof NumberMessage) {
            return new DecimalType(((NumberMessage) message).getValue().asInt());
        }
        return null;
    }

    private IntegerActuatorBridge() {
        // due to singleton
    }

    private static final IntegerActuatorBridge instance = new IntegerActuatorBridge();
}

/*----------------------------------------------------------------------------*/
