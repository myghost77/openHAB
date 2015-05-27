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

import org.openhab.binding.lcn_2.internal.definition.IVirtualActuatorBindingBridge;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;

/*----------------------------------------------------------------------------*/

public class IntegerVirtualActuatorBridge extends IntegerActuatorBridge implements IVirtualActuatorBindingBridge {

    public static synchronized IntegerVirtualActuatorBridge getInstance(final LCNValueConverter.Entity entity) {
        if (!instances.containsKey(entity)) {
            instances.put(entity, new IntegerVirtualActuatorBridge(LCNValueConverter.get(entity)));
        }
        return instances.get(entity);
    }

    @Override
    public boolean isGroupAllowed() {
        return true;
    }

    protected IntegerVirtualActuatorBridge(final LCNValueConverter.IConverter valueConverter) {
        super(valueConverter);
    }

    private static final Map<LCNValueConverter.Entity, IntegerVirtualActuatorBridge> instances = new HashMap<LCNValueConverter.Entity, IntegerVirtualActuatorBridge>();
}

/*----------------------------------------------------------------------------*/
