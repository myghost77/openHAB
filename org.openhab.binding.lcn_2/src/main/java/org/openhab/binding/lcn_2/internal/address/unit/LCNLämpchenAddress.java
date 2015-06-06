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
import org.openhab.binding.lcn_2.internal.binding.bridge.IntegerSensorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.IEnum;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
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

    private LCNLämpchenAddress(final LCNLämpchenParentAddress parent, final Type type, final boolean shadow) {
        this.parent = parent;
        this.type = type;
        this.shadow = shadow;
    }

    public LCNLämpchenAddress(final LCNLämpchenParentAddress parent, final Type type) {
        this(parent, type, false);
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        if (null != type) {
            return parent.getName() + ":" + type.asString();
        } else {
            return parent.getName();
        }
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

                if (null != type && null != other2.type) {
                    if (type.asNumber() < other2.type.asNumber())
                        return -1;
                    if (type.asNumber() > other2.type.asNumber())
                        return +1;
                } else {
                    if (null != type && null == other2.type)
                        return -1;
                    if (null == type && null != other2.type)
                        return +1;
                }

                return 0;
            }
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        if (isMaster()) {
            return BooleanVirtualActuatorBridge.getInstance();
        } else {
            return IntegerSensorBridge.getInstance(null);
        }
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        if (isMaster()) {
            return LCNLämpchen2PCKCommand.getInstance();
        } else {
            return null;
        }
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        // re-send stuff as status
        if (MessageType.COMMAND == parentMessage.getKey().getMessageType()) {
            final IAddress address = parentMessage.getKey().getAddress();
            if (address instanceof LCNLämpchenAddress) {
                final LCNLämpchenAddress unitAddress = (LCNLämpchenAddress) address;
                if (!unitAddress.isShadow()) {
                    final IMessageKey newKey = new MessageKeyImpl(MessageType.STATUS, new LCNLämpchenAddress(parent, type, true),
                            parentMessage.getKey().getValueType());
                    return parentMessage.getCopy(newKey);
                }
            }
        }
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

    public boolean isMaster() {
        return null != type;
    }

    public boolean isShadow() {
        return shadow;
    }

    private final LCNLämpchenParentAddress parent;

    private final Type type; // could be 'null'

    private final boolean shadow;
}

/*----------------------------------------------------------------------------*/
