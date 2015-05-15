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
import org.openhab.binding.lcn_2.internal.binding.bridge.BooleanSensorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.IEnum;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.helper.Comparator;

/*----------------------------------------------------------------------------*/

public class LCNSummeAddress implements ILCNUnitAddress {

    public enum Logic implements IEnum {
        SOME, FULL;

        public static Logic[] asList() {
            return new Logic[] { SOME, FULL };
        }

        @Override
        public String asString() {
            switch (this) {
            case SOME:
                return "some";
            case FULL:
                return "full";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case SOME:
                return 1;
            case FULL:
                return 2;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNSummeAddress(final LCNSummeParentAddress parent, final Logic logic) {
        this.parent = parent;
        this.logic = logic;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + logic.asString();
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
                final LCNSummeAddress other2 = (LCNSummeAddress) other;

                if (logic.asNumber() < other2.logic.asNumber())
                    return -1;
                if (logic.asNumber() > other2.logic.asNumber())
                    return +1;

                return 0;
            }
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return BooleanSensorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return null;
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNSummeParentAddress getParent() {
        return parent;
    }

    public Logic getLogic() {
        return logic;
    }

    private final LCNSummeParentAddress parent;

    private final Logic logic;
}

/*----------------------------------------------------------------------------*/
