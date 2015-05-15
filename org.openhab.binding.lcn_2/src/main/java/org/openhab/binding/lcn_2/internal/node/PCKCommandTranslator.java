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
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.TextMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;

/*----------------------------------------------------------------------------*/

public class PCKCommandTranslator implements INode {

    public PCKCommandTranslator(final IHasAddress pchkCommunicator) {
        this.pchkCommunicator = pchkCommunicator;
    }

    @Override
    public void register(final ISystem system) {
        system.register(this, MessageType.COMMAND, LCNAddress.getInstance());
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
        if (address instanceof ILCNUnitAddress) {
            final ILCNUnitAddress unitAddress = (ILCNUnitAddress) address;
            final IAddress2PCKCommand pckTranslator = unitAddress.getPCKTranslator();
            if (null != pckTranslator) {
                final String command = pckTranslator.createCommand(unitAddress, message);
                if (null != command) {
                    // forward command to PCHK communicator
                    system.send(Priority.HIGH, new TextMessage(new MessageKeyImpl(MessageType.COMMAND, pchkCommunicator.getAddress(),
                            ValueType.TEXT), command));
                }
            }
        }
    }

    private final IHasAddress pchkCommunicator;
}

/*----------------------------------------------------------------------------*/
