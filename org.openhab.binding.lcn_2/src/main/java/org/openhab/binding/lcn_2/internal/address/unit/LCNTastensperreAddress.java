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
import org.openhab.binding.lcn_2.internal.binding.bridge.BooleanVirtualActuatorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNTastensperre2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNTastensperreAddress extends BaseLCNUnitAddress {

    public LCNTastensperreAddress(final BaseLCNTargetAddress targetAddress, final int unitNr, final LCNSendeTasteAddress.Bank bank) {
        super(targetAddress, unitNr);

        this.bank = bank;
    }

    @Override
    public int compareTo(IAddress other) {
        final int cmp = super.compareTo(other);
        if (0 != cmp) {
            return cmp;
        } else {
            final LCNTastensperreAddress other2 = (LCNTastensperreAddress) other;

            if (bank.asNumber() < other2.bank.asNumber())
                return -1;
            if (bank.asNumber() > other2.bank.asNumber())
                return +1;

            return 0;
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return BooleanVirtualActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNTastensperre2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "buttonLock" + bank.asString();
    }

    @Override
    public int getMaxNrOfUnits() {
        return LCNSendeTasteAddress.getMaxNrOfButtons();
    }

    public LCNSendeTasteAddress.Bank getBank() {
        return bank;
    }

    private final LCNSendeTasteAddress.Bank bank;
}

/*----------------------------------------------------------------------------*/
