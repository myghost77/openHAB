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
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.helper.Comparator;

/*----------------------------------------------------------------------------*/

public abstract class BaseLCNUnitAddress implements ILCNUnitAddress {

    public BaseLCNUnitAddress(final BaseLCNTargetAddress targetAddress, final int unitNr) {
        this.targetAddress = targetAddress;
        this.unitNr = unitNr;
    }

    public BaseLCNUnitAddress(final BaseLCNTargetAddress targetAddress) {
        this.targetAddress = targetAddress;
        this.unitNr = 1;
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return targetAddress;
    }

    @Override
    public int compareTo(final IAddress other) {
        final int cmp = Comparator.compareClasses(this, other);
        if (0 != cmp) {
            return cmp;
        } else {
            final BaseLCNUnitAddress other2 = (BaseLCNUnitAddress) other;
            final int targetAddrCmp = targetAddress.compareTo(other2.targetAddress);
            if (0 != targetAddrCmp) {
                return targetAddrCmp;
            }

            if (unitNr < other2.unitNr)
                return -1;
            if (unitNr > other2.unitNr)
                return +1;

            return 0;
        }
    }

    @Override
    public IAddress getParentAddress() {
        return targetAddress;
    }

    @Override
    public String getName() {
        return targetAddress.getName() + ":" + getUnitName();
    }

    public String getUnitName() {
        if (getMaxNrOfUnits() >= 2 || unitNr != 1) {
            return getUnitPrefix() + unitNr + getUnitSuffix();
        } else {
            return getUnitPrefix();
        }
    }

    public abstract String getUnitPrefix();

    public String getUnitSuffix() {
        return "";
    }

    public abstract int getMaxNrOfUnits();

    public int getUnitNr() {
        return unitNr;
    }

    private final BaseLCNTargetAddress targetAddress;

    private final int unitNr;
}

/*----------------------------------------------------------------------------*/
