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

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class IntegerSensorBridge implements IAddressBindingBridge {

    public static synchronized IntegerSensorBridge getInstance(final LCNValueConverter.Entity entity) {
        if (!instances.containsKey(entity)) {
            instances.put(entity, new IntegerSensorBridge(LCNValueConverter.get(entity)));
        }
        return instances.get(entity);
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        return true;
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
        return null;
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.LCN_INTEGER == message.getKey().getValueType()
                && message instanceof NumberMessage) {
            if (null != valueConverter) {
                return new DecimalType(valueConverter.fromLcn(((NumberMessage) message).getValue()));
            } else {
                return new DecimalType(((NumberMessage) message).getValue());
            }
        } else {
            return null;
        }
    }

    protected IntegerSensorBridge(final LCNValueConverter.IConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    protected LCNValueConverter.IConverter getValueConverter() {
        return valueConverter;
    }

    private static final Map<LCNValueConverter.Entity, IntegerSensorBridge> instances = new HashMap<LCNValueConverter.Entity, IntegerSensorBridge>();

    private final LCNValueConverter.IConverter valueConverter;
}

/*----------------------------------------------------------------------------*/
