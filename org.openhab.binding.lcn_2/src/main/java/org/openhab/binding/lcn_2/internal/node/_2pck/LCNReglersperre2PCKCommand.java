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

import org.openhab.binding.lcn_2.internal.address.unit.LCNReglersperreAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNReglersperre2PCKCommand extends BaseAddress2PCKCommand<LCNReglersperreAddress> {

    public static LCNReglersperre2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNReglersperreAddress unitAddress, final IMessage message) {
        if (ValueType.BOOLEAN == message.getKey().getValueType() && message instanceof BooleanMessage) {
            final String aktion;
            if (((BooleanMessage) message).getValue().booleanValue()) {
                aktion = "S";
            } else {
                aktion = "A";
            }

            final String regler;
            switch (unitAddress.getParent().getUnitNr()) {
            case 1:
                regler = "A";
                break;
            case 2:
                regler = "B";
                break;
            default:
                return null;
            }

            return createCommandStr(unitAddress, "RE" + regler + "X", aktion);
        } else {
            return null;
        }
    }

    private LCNReglersperre2PCKCommand() {
        // due to singleton
    }

    private static final LCNReglersperre2PCKCommand instance = new LCNReglersperre2PCKCommand();
}

/*----------------------------------------------------------------------------*/
