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

import java.util.SortedMap;
import java.util.TreeMap;

import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangRampeAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;

/*----------------------------------------------------------------------------*/

public class LCNAusgangRampeDictionary extends BaseAddress2PCKCommand<LCNAusgangRampeAddress> {

    public static LCNAusgangRampeDictionary getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    private synchronized void storeRamp(final LCNAusgangAddress unitAddress, final int ramp) {
        ramps.put(unitAddress, ramp);
    }

    public synchronized int getRamp(final LCNAusgangAddress unitAddress) {
        if (ramps.containsKey(unitAddress)) {
            return ramps.get(unitAddress);
        } else {
            return 1; // default ramp
        }
    }

    @Override
    protected String __createCommand(final LCNAusgangRampeAddress unitAddress, final IMessage message) {
        if (message.getKey().getValueType() == ValueType.LCN_INTEGER && message instanceof NumberMessage) {
            int ramp = ((NumberMessage) message).getValue();
            if (ramp < 0)
                ramp = 0;
            if (ramp > 250)
                ramp = 250;

            storeRamp(new LCNAusgangAddress(unitAddress.getTargetAddress(), unitAddress.getUnitNr()), ramp);
        }

        return null; // ramp is only stored in this dictionary => no command
    }

    private LCNAusgangRampeDictionary() {
        // due to singleton
    }

    private static final LCNAusgangRampeDictionary instance = new LCNAusgangRampeDictionary();

    private final SortedMap<LCNAusgangAddress, Integer> ramps = new TreeMap<LCNAusgangAddress, Integer>();
}

/*----------------------------------------------------------------------------*/
