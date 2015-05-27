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

package org.openhab.binding.lcn_2.internal.node;

import org.openhab.binding.lcn_2.internal.address.LCNAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.node._2pck.LCNLämpchen2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNLämpchenStatusUpdater implements INode {

    @Override
    public void register(ISystem system) {
        system.register(this, MessageType.STATUS, LCNAddress.getInstance());
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void join() throws InterruptedException {
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        final IAddress address = message.getKey().getAddress();
        if (address instanceof LCNLämpchenAddress && message instanceof BooleanMessage) {
            final LCNLämpchenAddress unitAddress = (LCNLämpchenAddress) address;
            if (!unitAddress.isShadow()) {
                LCNLämpchen2PCKCommand.getInstance().updateStatus(unitAddress, (BooleanMessage) message);
            }
        }
    }
}

/*----------------------------------------------------------------------------*/
