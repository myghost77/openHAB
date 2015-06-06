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
import org.openhab.binding.lcn_2.internal.node._2pck.LCNSendeTasteVerzögert2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNSendeTasteVerzögertAddress implements ILCNUnitAddress {

    public enum Entity implements IEnum {
        SECONDS, MINUTES, HOURS, DAYS;

        public static Entity[] asList() {
            return new Entity[] { SECONDS, MINUTES, HOURS, DAYS };
        }

        @Override
        public String asString() {
            switch (this) {
            case SECONDS:
                return "seconds";
            case MINUTES:
                return "minutes";
            case HOURS:
                return "hours";
            case DAYS:
                return "days";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case SECONDS:
                return 1;
            case MINUTES:
                return 2;
            case HOURS:
                return 3;
            case DAYS:
                return 4;
            default:
                throw new RuntimeException();
            }
        }
    }

    public LCNSendeTasteVerzögertAddress(final LCNSendeTasteVerzögertParentAddress parent, final Entity entity, final int delay,
            final Command2LCNBridge.CommandResetType resetType) {
        this.parent = parent;
        this.entity = entity;
        this.delay = delay;
        this.resetType = resetType;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        return parent.getName() + ":" + delay + "_" + entity.asString();
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
                final LCNSendeTasteVerzögertAddress other2 = (LCNSendeTasteVerzögertAddress) other;

                if (entity.asNumber() < other2.entity.asNumber())
                    return -1;
                if (entity.asNumber() > other2.entity.asNumber())
                    return +1;

                if (delay < other2.delay)
                    return -1;
                if (delay > other2.delay)
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
        return LCNSendeTasteVerzögert2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        return null;
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNSendeTasteVerzögertParentAddress getParent() {
        return parent;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getDelay() {
        return delay;
    }

    private final LCNSendeTasteVerzögertParentAddress parent;

    private final Entity entity;

    private final int delay;

    private final Command2LCNBridge.CommandResetType resetType;
}

/*----------------------------------------------------------------------------*/
