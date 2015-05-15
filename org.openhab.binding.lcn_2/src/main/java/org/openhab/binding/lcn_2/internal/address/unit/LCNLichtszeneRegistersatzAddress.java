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

package org.openhab.binding.lcn_2.internal.address.unit;

import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.binding.bridge.IntegerVirtualActuatorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNLichtszeneRegistersatz2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNLichtszeneRegistersatzAddress extends BaseLCNUnitAddress {

    public LCNLichtszeneRegistersatzAddress(final BaseLCNTargetAddress targetAddress) {
        super(targetAddress);
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return IntegerVirtualActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNLichtszeneRegistersatz2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "cueStateRegSet";
    }

    @Override
    public int getMaxNrOfUnits() {
        return 1;
    }
}

/*----------------------------------------------------------------------------*/
