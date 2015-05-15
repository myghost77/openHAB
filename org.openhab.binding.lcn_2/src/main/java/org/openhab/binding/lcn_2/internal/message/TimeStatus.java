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

package org.openhab.binding.lcn_2.internal.message;

import java.util.Date;

import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.key.SysMessageKey;

/*----------------------------------------------------------------------------*/

public class TimeStatus implements IMessage {

    public static IMessageKey getMessageKey() {
        return messageKey;
    }

    public TimeStatus(final Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public IMessageKey getKey() {
        return messageKey;
    }

    @Override
    public boolean hasSameValue(final IMessage other) {
        if (null != other && other instanceof TimeStatus) {
            return dateTime.equals(((TimeStatus) other).dateTime);
        } else {
            return false;
        }
    }

    @Override
    public String asText() {
        return dateTime.toString();
    }

    @Override
    public IMessage getCopy(final IMessageKey key) {
        return new TimeStatus(dateTime);
    }

    public Date getDateTime() {
        return dateTime;
    }

    private static final IMessageKey messageKey = new SysMessageKey(MessageType.STATUS, ValueType.TIME);

    private final Date dateTime;
}

/*----------------------------------------------------------------------------*/
