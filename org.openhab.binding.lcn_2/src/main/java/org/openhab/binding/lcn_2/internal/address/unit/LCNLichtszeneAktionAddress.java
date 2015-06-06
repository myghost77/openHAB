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
import org.openhab.binding.lcn_2.internal.node._2pck.LCNLichtszeneAktion2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNLichtszeneAktionAddress implements ILCNUnitAddress {

    public enum Action implements IEnum {
        CALL, SAVE;

        public static Action[] asList() {
            return new Action[] { CALL, SAVE };
        }

        @Override
        public String asString() {
            switch (this) {
            case CALL:
                return "call";
            case SAVE:
                return "save";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case CALL:
                return 1;
            case SAVE:
                return 2;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNLichtszeneAktionAddress(final LCNLichtszeneAktionParentAddress parent, final Action action, final int register,
            final Command2LCNBridge.CommandResetType resetType) {
        this.parent = parent;
        this.action = action;
        this.register = register;
        this.resetType = resetType;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + action.asString() + "_" + register;
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
                final LCNLichtszeneAktionAddress other2 = (LCNLichtszeneAktionAddress) other;

                if (action.asNumber() < other2.action.asNumber())
                    return -1;
                if (action.asNumber() > other2.action.asNumber())
                    return +1;

                if (register < other2.register)
                    return -1;
                if (register > other2.register)
                    return +1;

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
        return LCNLichtszeneAktion2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNLichtszeneAktionParentAddress getParent() {
        return parent;
    }

    public Action getAction() {
        return action;
    }

    public int getRegister() {
        return register;
    }

    private final LCNLichtszeneAktionParentAddress parent;

    private final Action action;

    private final int register;

    private final Command2LCNBridge.CommandResetType resetType;
}

/*----------------------------------------------------------------------------*/
