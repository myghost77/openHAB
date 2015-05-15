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

import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangFlackernAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNAusgangFlackern2PCKCommand extends BaseAddress2PCKCommand<LCNAusgangFlackernAddress> {

    public static LCNAusgangFlackern2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNAusgangFlackernAddress unitAddress, final IMessage message) {
        final String flackertiefe;
        switch (unitAddress.getType()) {
        case SLIGHT:
            flackertiefe = "G";
            break;
        case MEDIUM:
            flackertiefe = "M";
            break;
        case STRONG:
            flackertiefe = "S";
            break;
        case TERMINATE:
            flackertiefe = "A";
            break;
        default:
            return null;
        }

        final String geschwindigkeit;
        switch (unitAddress.getSpeed()) {
        case SLOW:
            geschwindigkeit = "L";
            break;
        case MEDIUM:
            geschwindigkeit = "M";
            break;
        case FAST:
            geschwindigkeit = "S";
            break;
        default:
            return null;
        }

        return createCommandStr(unitAddress, "A" + translate1Digit(unitAddress.getParent().getUnitNr()) + "FL", flackertiefe
                + geschwindigkeit + translate3Digits(unitAddress.getCount()));
    }

    private LCNAusgangFlackern2PCKCommand() {
        // due to singleton
    }

    private static final LCNAusgangFlackern2PCKCommand instance = new LCNAusgangFlackern2PCKCommand();
}

/*----------------------------------------------------------------------------*/
