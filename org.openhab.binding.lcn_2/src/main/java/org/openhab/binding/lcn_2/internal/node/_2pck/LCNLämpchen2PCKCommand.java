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

import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenParentAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.LCNLämpchenHandler;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;

/*----------------------------------------------------------------------------*/

public class LCNLämpchen2PCKCommand extends BaseAddress2PCKCommand<LCNLämpchenAddress> {

    public static LCNLämpchen2PCKCommand getInstance() {
        return instance;
    }

    @Override
    public boolean requestReceipt() {
        return false;
    }

    public synchronized void updateStatus(final LCNLämpchenAddress unitAddress, final BooleanMessage message) {
        if (unitAddress.isMaster()) {
            final LCNLämpchenParentAddress parent = unitAddress.getParent();
            if (!handlers.containsKey(parent)) {
                handlers.put(parent, new LCNLämpchenHandler());
            }
            final LCNLämpchenHandler targetHandler = handlers.get(parent);
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
    protected String __createCommand(final LCNLämpchenAddress unitAddress, final IMessage message) {
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

    private LCNLämpchen2PCKCommand() {
        // due to singleton
    }

    private synchronized String determineAction(final LCNLämpchenAddress unitAddress) {
        final LCNLämpchenHandler targetHandler = handlers.get(unitAddress.getParent());
        if (null == targetHandler) {
            return null;
        }

        int actionCode = 0;
        if (LCNLämpchenHandler.State.ON == targetHandler.getCurrentBlinkState())
            actionCode = actionCode | 1;
        if (LCNLämpchenHandler.State.ON == targetHandler.getCurrentFlickerState())
            actionCode = actionCode | 2;
        if (LCNLämpchenHandler.State.ON == targetHandler.getCurrentOnState())
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

    private static final LCNLämpchen2PCKCommand instance = new LCNLämpchen2PCKCommand();

    private final SortedMap<LCNLämpchenParentAddress, LCNLämpchenHandler> handlers = new TreeMap<LCNLämpchenParentAddress, LCNLämpchenHandler>();
}

/*----------------------------------------------------------------------------*/
