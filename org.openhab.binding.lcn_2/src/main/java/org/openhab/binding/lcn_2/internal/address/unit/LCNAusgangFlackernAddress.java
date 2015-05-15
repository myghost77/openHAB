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
import org.openhab.binding.lcn_2.internal.node._2pck.LCNAusgangFlackern2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNAusgangFlackernAddress implements ILCNUnitAddress {

    public static int getMaxFlickerCount() {
        return 15;
    }

    public enum Type implements IEnum {
        SLIGHT, MEDIUM, STRONG, TERMINATE;

        public static Type[] asList() {
            return new Type[] { SLIGHT, MEDIUM, STRONG, TERMINATE };
        }

        @Override
        public String asString() {
            switch (this) {
            case SLIGHT:
                return "slight";
            case MEDIUM:
                return "medium";
            case STRONG:
                return "strong";
            case TERMINATE:
                return "terminate";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case SLIGHT:
                return 1;
            case MEDIUM:
                return 2;
            case STRONG:
                return 3;
            case TERMINATE:
                return 4;
            default:
                throw new RuntimeException();
            }
        }
    }

    public enum Speed implements IEnum {
        SLOW, MEDIUM, FAST;

        public static Speed[] asList() {
            return new Speed[] { SLOW, MEDIUM, FAST };
        }

        @Override
        public String asString() {
            switch (this) {
            case SLOW:
                return "slow";
            case MEDIUM:
                return "medium";
            case FAST:
                return "fast";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case SLOW:
                return 1;
            case MEDIUM:
                return 2;
            case FAST:
                return 3;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNAusgangFlackernAddress(final LCNAusgangFlackernParentAddress parent, final Type type, final Speed speed, final int count) {
        this.parent = parent;
        this.type = type;
        this.speed = speed;
        if (count < 1) {
            this.count = 1;
        } else if (count > getMaxFlickerCount()) {
            this.count = getMaxFlickerCount();
        } else {
            this.count = count;
        }
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + type.asString() + "_" + speed.asString() + "_" + count;
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
                final LCNAusgangFlackernAddress other2 = (LCNAusgangFlackernAddress) other;

                if (type.asNumber() < other2.type.asNumber())
                    return -1;
                if (type.asNumber() > other2.type.asNumber())
                    return +1;

                if (speed.asNumber() < other2.speed.asNumber())
                    return -1;
                if (speed.asNumber() > other2.speed.asNumber())
                    return +1;

                if (count < other2.count)
                    return -1;
                if (count > other2.count)
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
        return LCNAusgangFlackern2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNAusgangFlackernParentAddress getParent() {
        return parent;
    }

    public Type getType() {
        return type;
    }

    public Speed getSpeed() {
        return speed;
    }

    public int getCount() {
        return count;
    }

    private final LCNAusgangFlackernParentAddress parent;

    private final Type type;

    private final Speed speed;

    private final int count;
}

/*----------------------------------------------------------------------------*/
