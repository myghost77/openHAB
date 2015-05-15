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
import org.openhab.binding.lcn_2.internal.node._2pck.LCNSendeTaste2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNSendeTasteAddress extends BaseLCNUnitAddress {

    public static int getMaxNrOfButtons() {
        return 8;
    }

    public enum Type implements IEnum {
        SHORT, LONG, LOOSE;

        public static Type[] asList() {
            return new Type[] { SHORT, LONG, LOOSE };
        }

        @Override
        public String asString() {
            switch (this) {
            case SHORT:
                return "short";
            case LONG:
                return "long";
            case LOOSE:
                return "loose";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case SHORT:
                return 1;
            case LONG:
                return 2;
            case LOOSE:
                return 3;
            default:
                throw new RuntimeException();
            }
        }
    }

    public enum Bank implements IEnum {
        A, B, C, D;

        public static Bank[] asList() {
            return new Bank[] { A, B, C, D };
        }

        @Override
        public String asString() {
            switch (this) {
            case A:
                return "A";
            case B:
                return "B";
            case C:
                return "C";
            case D:
                return "D";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case A:
                return 1;
            case B:
                return 2;
            case C:
                return 3;
            case D:
                return 4;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNSendeTasteAddress(final BaseLCNTargetAddress targetAddress, final int unitNr, final Type type, final Bank bank) {
        super(targetAddress, unitNr);

        this.type = type;
        this.bank = bank;
    }

    @Override
    public int compareTo(IAddress other) {
        final int cmp = super.compareTo(other);
        if (0 != cmp) {
            return cmp;
        } else {
            final LCNSendeTasteAddress other2 = (LCNSendeTasteAddress) other;

            if (type.asNumber() < other2.type.asNumber())
                return -1;
            if (type.asNumber() > other2.type.asNumber())
                return +1;

            if (bank.asNumber() < other2.bank.asNumber())
                return -1;
            if (bank.asNumber() > other2.bank.asNumber())
                return +1;

            return 0;
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return Command2LCNBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNSendeTaste2PCKCommand.getInstance();
    }

    @Override
    public String getUnitPrefix() {
        return "buttonPress" + bank.asString();
    }

    @Override
    public String getUnitSuffix() {
        return "_" + type.asString();
    }

    @Override
    public int getMaxNrOfUnits() {
        return getMaxNrOfButtons();
    }

    public Type getType() {
        return type;
    }

    public Bank getBank() {
        return bank;
    }

    private final Type type;

    private final Bank bank;
}

/*----------------------------------------------------------------------------*/
