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
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.key.SysMessageKey;

/*----------------------------------------------------------------------------*/

public class StopServerCommand implements IMessage {

    public static IMessage getInstance() {
        return instance;
    }

    @Override
    public IMessageKey getKey() {
        return messageKey;
    }

    @Override
    public boolean hasSameValue(final IMessage other) {
        return true; // always true due to singleton
    }

    @Override
    public String asText() {
        return "StopServer";
    }

    @Override
    public IMessage getCopy(final IMessageKey key) {
        return null;
    }

    private StopServerCommand() {
        // due to singleton
    }

    private static final IMessageKey messageKey = new SysMessageKey(MessageType.COMMAND, ValueType.COMMAND);

    private static final IMessage instance = new StopServerCommand();
}

/*----------------------------------------------------------------------------*/
