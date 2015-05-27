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

import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;

/*----------------------------------------------------------------------------*/

public class IntegerActuatorBridge extends IntegerSensorBridge {

    public static synchronized IntegerActuatorBridge getInstance(final LCNValueConverter.Entity entity) {
        if (!instances.containsKey(entity)) {
            instances.put(entity, new IntegerActuatorBridge(LCNValueConverter.get(entity)));
        }
        return instances.get(entity);
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
    public IMessage createMessage(final ILCNUnitAddress unitAddress, final Command command) {
        if (command instanceof DecimalType) {
            if (null != getValueConverter()) {
                return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.LCN_INTEGER), getValueConverter()
                        .toLCN(((DecimalType) command).floatValue()));
            } else {
                return new NumberMessage(new MessageKeyImpl(MessageType.COMMAND, unitAddress, ValueType.LCN_INTEGER),
                        ((DecimalType) command).intValue());
            }
        } else {
            return null;
        }
    }

    protected IntegerActuatorBridge(final LCNValueConverter.IConverter valueConverter) {
        super(valueConverter);
    }

    private static final Map<LCNValueConverter.Entity, IntegerActuatorBridge> instances = new HashMap<LCNValueConverter.Entity, IntegerActuatorBridge>();
}

/*----------------------------------------------------------------------------*/
