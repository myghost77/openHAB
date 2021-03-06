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
import org.openhab.binding.lcn_2.internal.binding.bridge.Command2LCNBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNAusgangRampeStopp2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNAusgangRampeStoppAddress extends BaseLCNUnitAddress {

    public LCNAusgangRampeStoppAddress(final BaseLCNTargetAddress targetAddress, final int unitNr,
            final Command2LCNBridge.CommandResetType resetType) {
        super(targetAddress, unitNr);
        this.resetType = resetType;
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return Command2LCNBridge.getInstance(resetType);
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNAusgangRampeStopp2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "outputRampStop";
    }

    @Override
    public int getMaxNrOfUnits() {
        return LCNAusgangAddress.getMaxNrOfOutputs();
    }

    private final Command2LCNBridge.CommandResetType resetType;
}

/*----------------------------------------------------------------------------*/
