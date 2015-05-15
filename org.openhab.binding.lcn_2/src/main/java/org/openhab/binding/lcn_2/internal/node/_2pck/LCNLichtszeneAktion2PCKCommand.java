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

import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLichtszeneAktionAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNLichtszeneAktion2PCKCommand extends BaseAddress2PCKCommand<LCNLichtszeneAktionAddress> {

    public static LCNLichtszeneAktion2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNLichtszeneAktionAddress unitAddress, final IMessage message) {
        final String aktion;
        final String ramp3D;
        switch (unitAddress.getAction()) {
        case CALL:
            aktion = "A";
            ramp3D = ""; // stored ramp
            break;
        case SAVE:
            aktion = "S";
            ramp3D = translate3Digits(LCNAusgangRampeDictionary.getInstance().getRamp(
                    new LCNAusgangAddress(unitAddress.getParent().getTargetAddress(), unitAddress.getParent().getUnitNr())));
            break;
        default:
            return null;
        }

        return createCommandStr(unitAddress, "SZ" + aktion + translate1Digit(unitAddress.getParent().getUnitNr()),
                translate3Digits(unitAddress.getRegister()) + ramp3D);
    }

    private LCNLichtszeneAktion2PCKCommand() {
        // due to singleton
    }

    private static final LCNLichtszeneAktion2PCKCommand instance = new LCNLichtszeneAktion2PCKCommand();
}

/*----------------------------------------------------------------------------*/
