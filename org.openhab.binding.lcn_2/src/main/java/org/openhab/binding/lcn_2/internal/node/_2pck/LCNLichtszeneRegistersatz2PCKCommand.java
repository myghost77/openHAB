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

package org.openhab.binding.lcn_2.internal.node._2pck;

import org.openhab.binding.lcn_2.internal.address.unit.LCNLichtszeneRegistersatzAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;

/*----------------------------------------------------------------------------*/

public class LCNLichtszeneRegistersatz2PCKCommand extends BaseAddress2PCKCommand<LCNLichtszeneRegistersatzAddress> {

    public static LCNLichtszeneRegistersatz2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNLichtszeneRegistersatzAddress unitAddress, final IMessage message) {
        if (ValueType.INTEGER == message.getKey().getValueType() && message instanceof NumberMessage) {
            int registerSet = ((NumberMessage) message).getValue().asInt();
            return createCommandStr(unitAddress, "SZW", translate3Digits(registerSet));
        } else {
            return null;
        }
    }

    private LCNLichtszeneRegistersatz2PCKCommand() {
        // due to singleton
    }

    private static final LCNLichtszeneRegistersatz2PCKCommand instance = new LCNLichtszeneRegistersatz2PCKCommand();
}

/*----------------------------------------------------------------------------*/
