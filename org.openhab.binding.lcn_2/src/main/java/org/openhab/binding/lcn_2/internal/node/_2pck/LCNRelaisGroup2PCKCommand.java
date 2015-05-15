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

import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisGroupAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;

/*----------------------------------------------------------------------------*/

public class LCNRelaisGroup2PCKCommand extends BaseAddress2PCKCommand<LCNRelaisGroupAddress> {

    public static LCNRelaisGroup2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return true;
    }

    @Override
    protected String __createCommand(final LCNRelaisGroupAddress unitAddress, final IMessage message) {
        final StringBuilder relaisActionBuilder = new StringBuilder();
        for (int i = 0; i < unitAddress.getRelais().length; i++) {
            final LCNRelaisGroupAddress.SwitchType switchType = unitAddress.getRelais()[i];
            if (null == switchType) {
                relaisActionBuilder.append('-');
            } else {
                switch (switchType) {
                case TOGGLE:
                    relaisActionBuilder.append('U');
                    break;
                case ON:
                    relaisActionBuilder.append('1');
                    break;
                case OFF:
                    relaisActionBuilder.append('0');
                    break;
                default:
                    return null;
                }
            }
        }

        return createCommandStr(unitAddress, "RL", relaisActionBuilder.toString());
    }

    private LCNRelaisGroup2PCKCommand() {
        // due to singleton
    }

    private static final LCNRelaisGroup2PCKCommand instance = new LCNRelaisGroup2PCKCommand();
}

/*----------------------------------------------------------------------------*/
