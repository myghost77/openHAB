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

import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNLämpchen2PCKCommand extends BaseAddress2PCKCommand<LCNLämpchenAddress> {

    public static LCNLämpchen2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNLämpchenAddress unitAddress, final IMessage message) {
        if (ValueType.BOOLEAN == message.getKey().getValueType() && message instanceof BooleanMessage) {
            final boolean enable = ((BooleanMessage) message).getValue();
            final String aktion;
            if (enable) {
                switch (unitAddress.getType()) {
                case ON:
                    aktion = "E";
                    break;
                case FLICKER:
                    aktion = "F";
                    break;
                case BLINK:
                    aktion = "B";
                    break;
                default:
                    return null;
                }
            } else {
                aktion = "A";
            }

            return createCommandStr(unitAddress, "LA" + translate3Digits(unitAddress.getParent().getUnitNr()), aktion);
        } else {
            return null;
        }
    }

    private LCNLämpchen2PCKCommand() {
        // due to singleton
    }

    private static final LCNLämpchen2PCKCommand instance = new LCNLämpchen2PCKCommand();
}

/*----------------------------------------------------------------------------*/
