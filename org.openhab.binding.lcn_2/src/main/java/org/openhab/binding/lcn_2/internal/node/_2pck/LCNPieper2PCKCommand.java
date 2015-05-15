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

import org.openhab.binding.lcn_2.internal.address.unit.LCNPieperAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNPieper2PCKCommand extends BaseAddress2PCKCommand<LCNPieperAddress> {

    public static LCNPieper2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNPieperAddress unitAddress, final IMessage message) {
        final String tonart;
        switch (unitAddress.getMode()) {
        case NORMAL:
            tonart = "N";
            break;
        case SPECIAL:
            tonart = "S";
            break;
        default:
            return null;
        }

        return createCommandStr(unitAddress, "PI", tonart + translate3Digits(unitAddress.getBeeps()));
    }

    private LCNPieper2PCKCommand() {
        // due to singleton
    }

    private static final LCNPieper2PCKCommand instance = new LCNPieper2PCKCommand();
}

/*----------------------------------------------------------------------------*/
