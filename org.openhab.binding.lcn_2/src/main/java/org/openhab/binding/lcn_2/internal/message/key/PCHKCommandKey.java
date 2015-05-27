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

package org.openhab.binding.lcn_2.internal.message.key;

import org.openhab.binding.lcn_2.internal.address.PCHKCommandAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;

/*----------------------------------------------------------------------------*/

public class PCHKCommandKey extends BaseMessageKey {

    @Override
    public MessageType getMessageType() {
        return MessageType.COMMAND;
    }

    @Override
    public IAddress getAddress() {
        return PCHKCommandAddress.getInstance();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.TEXT;
    }
}

/*----------------------------------------------------------------------------*/
