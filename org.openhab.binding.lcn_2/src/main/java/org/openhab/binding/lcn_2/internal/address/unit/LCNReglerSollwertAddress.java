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
import org.openhab.binding.lcn_2.internal.binding.bridge.TempActuatorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNReglerSollwert2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNReglerSollwertAddress extends BaseLCNUnitAddress {

    public LCNReglerSollwertAddress(final BaseLCNTargetAddress targetAddress, final int unitNr) {
        super(targetAddress, unitNr);
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return TempActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNReglerSollwert2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "regulatorDesiredVal";
    }

    @Override
    public int getMaxNrOfUnits() {
        return LCNTemperaturVariableAddress.getMaxNrOfTempVars();
    }
}

/*----------------------------------------------------------------------------*/
