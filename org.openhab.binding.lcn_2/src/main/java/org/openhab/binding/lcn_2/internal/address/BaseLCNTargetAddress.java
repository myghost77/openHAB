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

package org.openhab.binding.lcn_2.internal.address;

import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.helper.Comparator;

/*----------------------------------------------------------------------------*/

public abstract class BaseLCNTargetAddress implements IAddress {

    public BaseLCNTargetAddress(final int segmentAddress, final int nr) {
        this.segmentAddress = segmentAddress;
        this.nr = nr;
    }

    public BaseLCNTargetAddress(final int nr) {
        this.segmentAddress = 0; // local segment
        this.nr = nr;
    }

    @Override
    public int compareTo(final IAddress other) {
        final int cmp = Comparator.compareClasses(this, other);
        if (0 != cmp) {
            return cmp;
        } else {
            final int parAddrCmp = getParentAddress().compareTo(other.getParentAddress());
            if (0 != parAddrCmp) {
                return parAddrCmp;
            } else {
                final BaseLCNTargetAddress other2 = (BaseLCNTargetAddress) other;

                if (segmentAddress < other2.segmentAddress)
                    return -1;
                if (segmentAddress > other2.segmentAddress)
                    return +1;

                if (nr < other2.nr)
                    return -1;
                if (nr > other2.nr)
                    return +1;

                return 0;
            }
        }
    }

    @Override
    public IAddress getParentAddress() {
        return LCNAddress.getInstance();
    }

    @Override
    public String getName() {
        return LCNAddress.getInstance().getName() + ":S" + segmentAddress + "." + getTargetPrefix() + nr;
    }

    public int getSegmentAddress() {
        return segmentAddress;
    }

    public int getNr() {
        return nr;
    }

    protected abstract char getTargetPrefix();

    private final int segmentAddress;

    private final int nr;
}

/*----------------------------------------------------------------------------*/
