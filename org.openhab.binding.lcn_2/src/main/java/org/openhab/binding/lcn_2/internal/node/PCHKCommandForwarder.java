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

import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.binding.lcn_2.internal.message.key.PCHKCommandKey;

/*----------------------------------------------------------------------------*/

public class PCHKCommandForwarder implements INode {

    public PCHKCommandForwarder(final IHasAddress pchkCommunicator) {
        this.pchkCommunicator = pchkCommunicator;
    }

    @Override
    public void register(final ISystem system) {
        system.register(this, new PCHKCommandKey());
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
        system.send(priority, message.getCopy(new MessageKeyImpl(message.getKey().getMessageType(), pchkCommunicator.getAddress(), message
                .getKey().getValueType())));
    }

    private final IHasAddress pchkCommunicator;
}

/*----------------------------------------------------------------------------*/
