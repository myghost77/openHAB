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
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class BooleanActuatorBridge implements IAddressBindingBridge {

    public static BooleanActuatorBridge getInstance() {
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
        return false;
    }

    @Override
    public IMessage createMessage(final ILCNUnitAddress unitAddress, final Command command) {
        if (command instanceof OnOffType) {
            final IMessageKey messageKey = new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.BOOLEAN);
            switch ((OnOffType) command) {
            case ON:
                return new BooleanMessage(messageKey, true);
            case OFF:
                return new BooleanMessage(messageKey, false);
            }
        }
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.BOOLEAN == message.getKey().getValueType()
                && message instanceof BooleanMessage) {
            if (((BooleanMessage) message).getValue().booleanValue()) {
                return OnOffType.ON;
            } else {
                return OnOffType.OFF;
            }
        }
        return null;
    }

    private BooleanActuatorBridge() {
        // due to singleton
    }

    private static final BooleanActuatorBridge instance = new BooleanActuatorBridge();
}

/*----------------------------------------------------------------------------*/
