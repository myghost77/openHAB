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

import org.openhab.binding.lcn_2.internal.address.unit.LCNReglerSollwertAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;

/*----------------------------------------------------------------------------*/

public class LCNReglerSollwert2PCKCommand extends BaseAddress2PCKCommand<LCNReglerSollwertAddress> {

    public static LCNReglerSollwert2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNReglerSollwertAddress unitAddress, final IMessage message) {
        if (ValueType.LCN_INTEGER == message.getKey().getValueType() && message instanceof NumberMessage) {
            final int value = ((NumberMessage) message).getValue();
            if (value < 0 || value > 2000) {
                return null;
            }

            final String regler;
            switch (unitAddress.getUnitNr()) {
            case 1:
                regler = "A";
                break;
            case 2:
                regler = "B";
                break;
            default:
                return null;
            }

            return createCommandStr(unitAddress, "RE" + regler, "SSE" + String.valueOf(value));
        } else {
            return null;
        }
    }

    private LCNReglerSollwert2PCKCommand() {
        // due to singleton
    }

    private static final LCNReglerSollwert2PCKCommand instance = new LCNReglerSollwert2PCKCommand();
}

/*----------------------------------------------------------------------------*/
