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

import org.openhab.binding.lcn_2.internal.definition.IMessageKey;

/*----------------------------------------------------------------------------*/

public abstract class BaseMessageKey implements IMessageKey {

    @Override
    public int compareTo(final IMessageKey other) {
        if (null == other) {
            return -1;
        }

        if (getMessageType().asNumber() < other.getMessageType().asNumber())
            return -1;
        if (getMessageType().asNumber() > other.getMessageType().asNumber())
            return +1;

        if (getValueType().asNumber() < other.getValueType().asNumber())
            return -1;
        if (getValueType().asNumber() > other.getValueType().asNumber())
            return +1;

        return getAddress().compareTo(other.getAddress());
    }
}

/*----------------------------------------------------------------------------*/
