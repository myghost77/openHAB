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
import org.openhab.binding.lcn_2.internal.node._2pck.LCNRelaisGroup2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNRelaisGroupAddress implements ILCNUnitAddress {

    public enum SwitchType implements IEnum {
        TOGGLE, ON, OFF;

        public static SwitchType[] asList() {
            return new SwitchType[] { TOGGLE, ON, OFF };
        }

        @Override
        public String asString() {
            switch (this) {
            case TOGGLE:
                return "toggle";
            case ON:
                return "on";
            case OFF:
                return "off";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case TOGGLE:
                return 2;
            case ON:
                return 1;
            case OFF:
                return 0;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNRelaisGroupAddress(final LCNRelaisGroupParentAddress parent, final SwitchType[] relais,
            final Command2LCNBridge.CommandResetType resetType) {
        this.parent = parent;
        this.relais = new SwitchType[LCNRelaisAddress.getMaxNrOfRelais()];

        for (int i = 0; i < LCNRelaisAddress.getMaxNrOfRelais(); i++) {
            if (null != relais && i < relais.length) {
                this.relais[i] = relais[i];
            } else {
                this.relais[i] = null;
            }
        }

        this.resetType = resetType;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        final StringBuilder relaisBuilder = new StringBuilder();
        for (int i = 0; i < relais.length; i++) {
            if (i > 0) {
                relaisBuilder.append("_");
            }

            final SwitchType st = relais[i];
            if (null == st) {
                relaisBuilder.append("X");
            } else {
                relaisBuilder.append(st.asString());
            }
        }

        return parent.getName() + ":" + relaisBuilder.toString();
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
                final LCNRelaisGroupAddress other2 = (LCNRelaisGroupAddress) other;

                if (relais.length < other2.relais.length)
                    return -1;
                if (relais.length > other2.relais.length)
                    return +1;

                for (int i = 0; i < relais.length; i++) {
                    final SwitchType st1 = relais[i];
                    final SwitchType st2 = other2.relais[i];

                    if (st1 == st2)
                        continue;

                    if (st1 != null && st2 == null)
                        return -1;
                    if (st1 == null && st2 != null)
                        return +1;

                    if (st1.asNumber() < st2.asNumber())
                        return -1;
                    if (st1.asNumber() > st2.asNumber())
                        return +1;
                }

                return 0;
            }
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return Command2LCNBridge.getInstance(resetType);
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNRelaisGroup2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNRelaisGroupParentAddress getParent() {
        return parent;
    }

    public SwitchType[] getRelais() {
        return relais;
    }

    private final LCNRelaisGroupParentAddress parent;

    private final SwitchType[] relais;

    private final Command2LCNBridge.CommandResetType resetType;
}

/*----------------------------------------------------------------------------*/
