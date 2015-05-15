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
import org.openhab.binding.lcn_2.internal.binding.bridge.BooleanActuatorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.IEnum;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNLämpchen2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNLämpchenAddress implements ILCNUnitAddress {

    public enum Type implements IEnum {
        ON, FLICKER, BLINK;

        public static Type[] asList() {
            return new Type[] { ON, FLICKER, BLINK };
        }

        @Override
        public String asString() {
            switch (this) {
            case BLINK:
                return "blink";
            case FLICKER:
                return "flicker";
            case ON:
                return "on";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case BLINK:
                return 1;
            case FLICKER:
                return 2;
            case ON:
                return 3;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNLämpchenAddress(final LCNLämpchenParentAddress parent, final Type type) {
        this.parent = parent;
        this.type = type;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + type.asString();
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
                final LCNLämpchenAddress other2 = (LCNLämpchenAddress) other;

                if (type.asNumber() < other2.type.asNumber())
                    return -1;
                if (type.asNumber() > other2.type.asNumber())
                    return +1;

                return 0;
            }
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return BooleanActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNLämpchen2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNLämpchenParentAddress getParent() {
        return parent;
    }

    public Type getType() {
        return type;
    }

    private final LCNLämpchenParentAddress parent;

    private final Type type;
}

/*----------------------------------------------------------------------------*/
