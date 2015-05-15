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

import org.openhab.binding.lcn_2.internal.address.unit.LCNSendeTasteAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNTastensperreAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNTastensperre2PCKCommand extends BaseAddress2PCKCommand<LCNTastensperreAddress> {

    public static LCNTastensperre2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNTastensperreAddress unitAddress, final IMessage message) {
        if (ValueType.BOOLEAN == message.getKey().getValueType() && message instanceof BooleanMessage) {
            final boolean value = ((BooleanMessage) message).getValue();

            final StringBuilder tastenBuilder = new StringBuilder();
            for (int nr = 1; nr <= LCNSendeTasteAddress.getMaxNrOfButtons(); nr++) {
                if (unitAddress.getUnitNr() == nr) {
                    if (value) {
                        tastenBuilder.append('1');
                    } else {
                        tastenBuilder.append('0');
                    }
                } else {
                    tastenBuilder.append('-');
                }
            }

            final String tabelle;
            switch (unitAddress.getBank()) {
            case A:
                tabelle = "A";
                break;
            case B:
                tabelle = "B";
                break;
            case C:
                tabelle = "C";
                break;
            default:
                return null;
            }

            return createCommandStr(unitAddress, "TX" + tabelle, tastenBuilder.toString());
        } else {
            return null;
        }
    }

    private LCNTastensperre2PCKCommand() {
        // due to singleton
    }

    private static final LCNTastensperre2PCKCommand instance = new LCNTastensperre2PCKCommand();
}

/*----------------------------------------------------------------------------*/
