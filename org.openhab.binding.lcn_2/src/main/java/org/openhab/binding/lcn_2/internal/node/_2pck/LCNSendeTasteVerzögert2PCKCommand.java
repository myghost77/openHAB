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
import org.openhab.binding.lcn_2.internal.address.unit.LCNSendeTasteVerz�gertAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNSendeTasteVerz�gert2PCKCommand extends BaseAddress2PCKCommand<LCNSendeTasteVerz�gertAddress> {

    public static LCNSendeTasteVerz�gert2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNSendeTasteVerz�gertAddress unitAddress, final IMessage message) {
        final StringBuilder tastenBuilder = new StringBuilder();
        for (int nr = 1; nr <= LCNSendeTasteAddress.getMaxNrOfButtons(); nr++) {
            if (unitAddress.getParent().getUnitNr() == nr) {
                tastenBuilder.append('1');
            } else {
                tastenBuilder.append('0');
            }
        }

        final String tabelle;
        switch (unitAddress.getParent().getBank()) {
        case A:
            tabelle = "A";
            break;
        case B:
            tabelle = "B";
            break;
        case C:
            tabelle = "C";
            break;
        case D:
            tabelle = "D";
            break;
        default:
            return null;
        }

        final String einheit;
        switch (unitAddress.getEntity()) {
        case SECONDS:
            einheit = "S";
            break;
        case MINUTES:
            einheit = "M";
            break;
        case HOURS:
            einheit = "H";
            break;
        case DAYS:
            einheit = "D";
            break;
        default:
            return null;
        }

        return createCommandStr(unitAddress, "TV" + tabelle, translate3Digits(unitAddress.getDelay()) + einheit + tastenBuilder.toString());
    }

    private LCNSendeTasteVerz�gert2PCKCommand() {
        // due to singleton
    }

    private static final LCNSendeTasteVerz�gert2PCKCommand instance = new LCNSendeTasteVerz�gert2PCKCommand();
}

/*----------------------------------------------------------------------------*/
