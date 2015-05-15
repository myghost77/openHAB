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
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.IEnum;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNPieper2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNPieperAddress implements ILCNUnitAddress {

    public static int getMaxNrOfBeeps() {
        return 15;
    }

    public enum Mode implements IEnum {
        NORMAL, SPECIAL;

        public static Mode[] asList() {
            return new Mode[] { NORMAL, SPECIAL };
        }

        @Override
        public String asString() {
            switch (this) {
            case NORMAL:
                return "normal";
            case SPECIAL:
                return "special";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case NORMAL:
                return 1;
            case SPECIAL:
                return 2;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNPieperAddress(final LCNPieperParentAddress parent, final Mode mode, final int beeps) {
        this.parent = parent;
        this.mode = mode;
        if (beeps < 1) {
            this.beeps = 1;
        } else if (beeps > getMaxNrOfBeeps()) {
            this.beeps = getMaxNrOfBeeps();
        } else {
            this.beeps = beeps;
        }
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + mode.asString() + "_" + beeps;
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
                final LCNPieperAddress other2 = (LCNPieperAddress) other;

                if (mode.asNumber() < other2.mode.asNumber())
                    return -1;
                if (mode.asNumber() > other2.mode.asNumber())
                    return +1;

                if (beeps < other2.beeps)
                    return -1;
                if (beeps > other2.beeps)
                    return +1;

                return 0;
            }
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return Command2LCNBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNPieper2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNPieperParentAddress getParent() {
        return parent;
    }

    public Mode getMode() {
        return mode;
    }

    public int getBeeps() {
        return beeps;
    }

    private final LCNPieperParentAddress parent;

    private final Mode mode;

    private final int beeps;
}

/*----------------------------------------------------------------------------*/
