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
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;

/*----------------------------------------------------------------------------*/

public class LCNAusgang2PCKCommand extends BaseAddress2PCKCommand<LCNAusgangAddress> {

    public static LCNAusgang2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNAusgangAddress unitAddress, final IMessage message) {
        switch (message.getKey().getValueType()) {
        case PERCENT: {
            final int percent;
            final int ramp;
            if (message instanceof BooleanMessage) {
                percent = ((BooleanMessage) message).getValue() ? 100 : 0;
                ramp = 0; // don't use ramp for switching on/off to avoid receiving status updates
            } else if (message instanceof NumberMessage) {
                percent = ((NumberMessage) message).getValue();
                ramp = LCNAusgangRampeDictionary.getInstance().getRamp(unitAddress); // get ramp from dictionary
            } else {
                return null;
            }

            return createCommandStr(unitAddress, "A" + translate1Digit(unitAddress.getUnitNr()) + "DI", translate3Digits(percent)
                    + translate3Digits(ramp));
        }

        case BOOLEAN:
            if (message instanceof BooleanMessage) {
                final String percentRampStr = ((BooleanMessage) message).getValue() ? "100000" : "000000";
                return createCommandStr(unitAddress, "A" + translate1Digit(unitAddress.getUnitNr()) + "DI", percentRampStr);
            } else {
                return null;
            }

        case SUMMAND:
            if (message instanceof NumberMessage) {
                int toAdd = ((NumberMessage) message).getValue();
                final String subCmdStr;
                if (toAdd < 0) {
                    toAdd = -toAdd; // abs
                    subCmdStr = "SB";
                } else {
                    subCmdStr = "AD";
                }

                return createCommandStr(unitAddress, "A" + translate1Digit(unitAddress.getUnitNr()) + subCmdStr, translate3Digits(toAdd));
            } else {
                return null;
            }

        default:
            return null;
        }
    }

    private LCNAusgang2PCKCommand() {
        // due to singleton
    }

    private static final LCNAusgang2PCKCommand instance = new LCNAusgang2PCKCommand();
}

/*----------------------------------------------------------------------------*/
