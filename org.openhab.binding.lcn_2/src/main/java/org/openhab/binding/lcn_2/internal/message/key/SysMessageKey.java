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

import org.openhab.binding.lcn_2.internal.address.SystemAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;

/*----------------------------------------------------------------------------*/

public class SysMessageKey extends BaseMessageKey {

    public SysMessageKey(final MessageType messageType, final ValueType valueType) {
        this.messageType = messageType;
        this.valueType = valueType;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public IAddress getAddress() {
        return SystemAddress.getInstance();
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    private final MessageType messageType;

    private final ValueType valueType;
}

/*----------------------------------------------------------------------------*/
