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

import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangRampeStoppAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNAusgangRampeStopp2PCKCommand extends BaseAddress2PCKCommand<LCNAusgangRampeStoppAddress> {

    public static LCNAusgangRampeStopp2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNAusgangRampeStoppAddress unitAddress, final IMessage message) {
        return createCommandStr(unitAddress, "A" + translate1Digit(unitAddress.getUnitNr()) + "RS", "");
    }

    private LCNAusgangRampeStopp2PCKCommand() {
        // due to singleton
    }

    private static final LCNAusgangRampeStopp2PCKCommand instance = new LCNAusgangRampeStopp2PCKCommand();
}

/*----------------------------------------------------------------------------*/
