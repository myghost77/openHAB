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
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNSendeTaste2PCKCommand extends BaseAddress2PCKCommand<LCNSendeTasteAddress> {

    public static LCNSendeTaste2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNSendeTasteAddress unitAddress, final IMessage message) {
        final StringBuilder tastenBuilder = new StringBuilder();
        for (int nr = 1; nr <= LCNSendeTasteAddress.getMaxNrOfButtons(); nr++) {
            if (unitAddress.getUnitNr() == nr) {
                tastenBuilder.append('1');
            } else {
                tastenBuilder.append('0');
            }
        }

        final String singleType;
        switch (unitAddress.getType()) {
        case SHORT:
            singleType = "K";
            break;
        case LONG:
            singleType = "L";
            break;
        case LOOSE:
            singleType = "O";
            break;
        default:
            return null;
        }

        final String typeList;
        switch (unitAddress.getBank()) {
        case A:
            typeList = singleType + "---";
            break;
        case B:
            typeList = "-" + singleType + "--";
            break;
        case C:
            typeList = "--" + singleType + "-";
            break;
        case D:
            typeList = "---" + singleType;
            break;
        default:
            return null;
        }

        return createCommandStr(unitAddress, "TS", typeList + tastenBuilder.toString());
    }

    private LCNSendeTaste2PCKCommand() {
        // due to singleton
    }

    private static final LCNSendeTaste2PCKCommand instance = new LCNSendeTaste2PCKCommand();
}

/*----------------------------------------------------------------------------*/
