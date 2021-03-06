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

import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;

/*----------------------------------------------------------------------------*/

public abstract class BaseValueMessage<T> extends BaseMessage {

    public BaseValueMessage(final IMessageKey messageKey, final T value) {
        super(messageKey);
        if (null == value) {
            throw new NullPointerException();
        }
        this.value = value;
    }

    @Override
    public boolean hasSameValue(final IMessage other) {
        if (null != other && other instanceof BaseValueMessage<?>) {
            return value.equals(((BaseValueMessage<?>) other).value);
        } else {
            return false;
        }
    }

    public T getValue() {
        return value;
    }

    protected final T value;
}

/*----------------------------------------------------------------------------*/
