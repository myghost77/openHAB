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

public class BooleanMessage extends BaseValueMessage<Boolean> {

    public BooleanMessage(final IMessageKey messageKey, final Boolean value) {
        super(messageKey, value);
    }

    @Override
    public String asText() {
        return value.booleanValue() ? "TRUE" : "FALSE";
    }

    @Override
    public IMessage getCopy(final IMessageKey key) {
        return new BooleanMessage(key, value);
    }
}

/*----------------------------------------------------------------------------*/
