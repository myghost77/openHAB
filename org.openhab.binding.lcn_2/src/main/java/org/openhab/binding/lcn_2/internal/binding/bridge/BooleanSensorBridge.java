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
import org.openhab.core.items.Item;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class BooleanSensorBridge implements IAddressBindingBridge {

    public static BooleanSensorBridge getInstance() {
        return instance;
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        return true;
    }

    @Override
    public boolean checkAllowedState(final ILCNUnitAddress unitAddress, final Item item) {
        for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
            if (dataType.equals(OpenClosedType.class) || dataType.equals(OnOffType.class)) {
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
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.BOOLEAN == message.getKey().getValueType()
                && message instanceof BooleanMessage) {
            for (final Class<? extends State> dataType : item.getAcceptedDataTypes()) {
                if (((BooleanMessage) message).getValue().booleanValue()) {
                    if (dataType.equals(OpenClosedType.class)) {
                        return OpenClosedType.CLOSED;
                    }
                    if (dataType.equals(OnOffType.class)) {
                        return OnOffType.ON;
                    }
                } else {
                    if (dataType.equals(OpenClosedType.class)) {
                        return OpenClosedType.OPEN;
                    }
                    if (dataType.equals(OnOffType.class)) {
                        return OnOffType.OFF;
                    }
                }
            }
        }
        return null;
    }

    protected BooleanSensorBridge() {
        // supposed to be a singleton
    }

    private static final BooleanSensorBridge instance = new BooleanSensorBridge();
}

/*----------------------------------------------------------------------------*/
