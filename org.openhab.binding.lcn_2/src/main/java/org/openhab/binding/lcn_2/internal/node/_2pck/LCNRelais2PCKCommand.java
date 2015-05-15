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

import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNRelais2PCKCommand extends BaseAddress2PCKCommand<LCNRelaisAddress> {

    public static LCNRelais2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    @Override
    protected String __createCommand(final LCNRelaisAddress unitAddress, final IMessage message) {
        if (ValueType.BOOLEAN == message.getKey().getValueType() && message instanceof BooleanMessage) {
            final boolean value = ((BooleanMessage) message).getValue();

            final StringBuilder relaisActionBuilder = new StringBuilder();
            for (int nr = 1; nr <= LCNRelaisAddress.getMaxNrOfRelais(); nr++) {
                if (unitAddress.getUnitNr() == nr) {
                    if (value) {
                        relaisActionBuilder.append('1');
                    } else {
                        relaisActionBuilder.append('0');
                    }
                } else {
                    relaisActionBuilder.append('-');
                }
            }

            return createCommandStr(unitAddress, "RL", relaisActionBuilder.toString());
        } else {
            return null;
        }
    }

    private LCNRelais2PCKCommand() {
        // due to singleton
    }

    private static final LCNRelais2PCKCommand instance = new LCNRelais2PCKCommand();
}

/*----------------------------------------------------------------------------*/
