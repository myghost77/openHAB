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

import org.openhab.binding.lcn_2.internal.address.unit.LCNL�mpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNL�mpchenParentAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.LCNL�mpchenHandler;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNL�mpchen2PCKCommand extends BaseAddress2PCKCommand<LCNL�mpchenAddress> {

    public static LCNL�mpchen2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    public synchronized void updateStatus(final LCNL�mpchenAddress unitAddress, final BooleanMessage message) {
        if (unitAddress.isMaster()) {
            final LCNL�mpchenParentAddress parent = unitAddress.getParent();
            if (!handlers.containsKey(parent)) {
                handlers.put(parent, new LCNL�mpchenHandler());
            }
            final LCNL�mpchenHandler targetHandler = handlers.get(parent);
            if (null != targetHandler) {
                final boolean enable = message.getValue();
                switch (unitAddress.getType()) {
                case ON:
                    targetHandler.setCurrentOnState(enable);
                    break;
                case FLICKER:
                    targetHandler.setCurrentFlickerState(enable);
                    break;
                case BLINK:
                    targetHandler.setCurrentBlinkState(enable);
                    break;
                default:
                    break;
                }
            }
        }
    }

    @Override
    protected String __createCommand(final LCNL�mpchenAddress unitAddress, final IMessage message) {
        if (unitAddress.isMaster()) {
            if (ValueType.BOOLEAN == message.getKey().getValueType() && message instanceof BooleanMessage) {
                updateStatus(unitAddress, (BooleanMessage) message);
                final String aktion = determineAction(unitAddress);
                if (null != aktion) {
                    return createCommandStr(unitAddress, "LA" + translate3Digits(unitAddress.getParent().getUnitNr()), aktion);
                }
            }
        }
        return null;
    }

    private LCNL�mpchen2PCKCommand() {
        // due to singleton
    }

    private synchronized String determineAction(final LCNL�mpchenAddress unitAddress) {
        final LCNL�mpchenHandler targetHandler = handlers.get(unitAddress.getParent());
        if (null == targetHandler) {
            return null;
        }

        int actionCode = 0;
        if (LCNL�mpchenHandler.State.ON == targetHandler.getCurrentBlinkState())
            actionCode = actionCode | 1;
        if (LCNL�mpchenHandler.State.ON == targetHandler.getCurrentFlickerState())
            actionCode = actionCode | 2;
        if (LCNL�mpchenHandler.State.ON == targetHandler.getCurrentOnState())
            actionCode = actionCode | 4;

        switch (actionCode) {
        case 4:
            return "E";
        case 2:
            return "F";
        case 1:
            return "B";
        case 0:
            return "A";
        default:
            return null;
        }
    }

    private static final LCNL�mpchen2PCKCommand instance = new LCNL�mpchen2PCKCommand();

    private final SortedMap<LCNL�mpchenParentAddress, LCNL�mpchenHandler> handlers = new TreeMap<LCNL�mpchenParentAddress, LCNL�mpchenHandler>();
}

/*----------------------------------------------------------------------------*/
