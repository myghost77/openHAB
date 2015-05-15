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
import org.openhab.binding.lcn_2.internal.definition.IVirtualActuatorBindingBridge;
import org.openhab.core.items.Item;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public class IntegerVirtualActuatorBridge implements IAddressBindingBridge, IVirtualActuatorBindingBridge {

    public static IntegerVirtualActuatorBridge getInstance() {
        return instance;
    }

    @Override
    public boolean checkAllowedCommand(final ILCNUnitAddress unitAddress, final Item item) {
        return IntegerActuatorBridge.getInstance().checkAllowedCommand(unitAddress, item);
    }

    @Override
    public boolean checkAllowedState(final ILCNUnitAddress unitAddress, final Item item) {
        return IntegerActuatorBridge.getInstance().checkAllowedState(unitAddress, item);
    }

    @Override
    public boolean isGroupAllowed() {
        return true;
    }

    @Override
    public IMessage createMessage(final ILCNUnitAddress unitAddress, final Command command) {
        return IntegerActuatorBridge.getInstance().createMessage(unitAddress, command);
    }

    @Override
    public State createState(final IMessage message, final Item item) {
        return IntegerActuatorBridge.getInstance().createState(message, item);
    }

    private IntegerVirtualActuatorBridge() {
        // due to singleton
    }

    private static final IntegerVirtualActuatorBridge instance = new IntegerVirtualActuatorBridge();
}

/*----------------------------------------------------------------------------*/
