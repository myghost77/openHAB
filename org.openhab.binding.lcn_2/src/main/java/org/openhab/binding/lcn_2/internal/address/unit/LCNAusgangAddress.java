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
import org.openhab.binding.lcn_2.internal.binding.bridge.DimActuatorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNAusgang2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNAusgangAddress extends BaseLCNUnitAddress {

    public static int getMaxNrOfOutputs() {
        return 4;
    }

    public LCNAusgangAddress(final BaseLCNTargetAddress targetAddress, final int unitNr) {
        super(targetAddress, unitNr);
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return DimActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNAusgang2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "output";
    }

    @Override
    public int getMaxNrOfUnits() {
        return getMaxNrOfOutputs();
    }
}

/*----------------------------------------------------------------------------*/
