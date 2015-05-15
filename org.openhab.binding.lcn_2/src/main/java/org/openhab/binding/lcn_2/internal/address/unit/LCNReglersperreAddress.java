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
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNReglersperre2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNReglersperreAddress implements ILCNUnitAddress {

    public LCNReglersperreAddress(final LCNReglersperreParentAddress parent, final ILCNUnitAddress target) {
        this.parent = parent;
        this.target = target;
    }

    @Override
    public IAddress getParentAddress() {
        return parent;
    }

    @Override
    public String getName() {
        if (null != target) {
            return parent.getName() + ":" + target.getName();
        } else {
            return parent.getName();
        }
    }

    @Override
    public int compareTo(final IAddress other) {
        if (other instanceof LCNReglersperreAddress) {
            return parent.compareTo(((LCNReglersperreAddress) other).parent);
        } else {
            return Comparator.compareClasses(this, other);
        }
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return BooleanVirtualActuatorBridge.getInstance();
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return LCNReglersperre2PCKCommand.getInstance();
    }

    @Override
    public IMessage getShadowMessage(final IMessage parentMessage) {
        if (null != target && MessageType.COMMAND == parentMessage.getKey().getMessageType() && parentMessage instanceof BooleanMessage) {
            final boolean locked = ((BooleanMessage) parentMessage).getValue();
            if (locked) {
                final IMessageKey targetMessageKey = new MessageKeyImpl(MessageType.COMMAND, target, ValueType.BOOLEAN);
                return new BooleanMessage(targetMessageKey, false); // switch off target
            } else {
                return null; // TODO: "Wiederhole Statuskommando" for regulator ???
            }
        } else {
            return null;
        }
    }

    @Override
    public BaseLCNTargetAddress getTargetAddress() {
        return parent.getTargetAddress();
    }

    public LCNReglersperreParentAddress getParent() {
        return parent;
    }

    private final LCNReglersperreParentAddress parent;

    private final ILCNUnitAddress target;
}

/*----------------------------------------------------------------------------*/
