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

package org.openhab.binding.lcn_2.internal.node;

import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNBinärsensorAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeParentAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.TextMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;

/*----------------------------------------------------------------------------*/

public class PCKStatusTranslator implements INode {

    public PCKStatusTranslator(final IHasAddress pchkCommunicator) {
        this.pchkCommunicator = pchkCommunicator;
    }

    @Override
    public void register(final ISystem system) {
        system.register(this, new MessageKeyImpl(MessageType.STATUS, pchkCommunicator.getAddress(), ValueType.TEXT));
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void join() throws InterruptedException {
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        if (message instanceof TextMessage) {
            final String text = ((TextMessage) message).getValue();
            if (!text.isEmpty()) {
                try {
                    switch (text.charAt(0)) {
                    case '-':
                        notifyFunktionsquittung(system, text);
                        break;
                    case ':':
                        notifyKomponentenStatus(system, text);
                        break;
                    case '%':
                        notifyMesswert(system, text);
                        break;
                    case '=':
                        notifySummeUndLämpchen(system, text);
                        break;
                    default:
                        break;
                    }
                } catch (final NumberFormatException e) {
                    // error in format => do nothing
                }
            }
        }
    }

    private void notifyFunktionsquittung(final ISystem system, final String text) throws InterruptedException {
        if (text.length() >= 9 && text.length() <= 11) {
            final int segmentAddress = Integer.valueOf(text.substring(2, 5));
            final int moduleNr = Integer.valueOf(text.substring(5, 8));
            final String codeStr = text.substring(8);
            final int code;
            if (codeStr.equals("!")) {
                code = -1;
            } else {
                code = Integer.valueOf(codeStr);
            }

            final LCNModuleAddress moduleAddress = new LCNModuleAddress(segmentAddress, moduleNr);
            sendNumber(system, moduleAddress, code, ValueType.ACKNOWLEDGE);
        }
    }

    private void notifyKomponentenStatus(final ISystem system, final String text) throws InterruptedException {
        if (text.length() == 13 && text.charAt(1) == 'M') {
            final int segmentAddress = Integer.valueOf(text.substring(2, 5));
            final int moduleNr = Integer.valueOf(text.substring(5, 8));
            final char unitType = text.charAt(8);
            final String unitNr = text.substring(9, 10);
            final int value = Integer.valueOf(text.substring(10));

            final LCNModuleAddress moduleAddress = new LCNModuleAddress(segmentAddress, moduleNr);

            switch (unitType) {
            case 'A': {
                final IAddress address = new LCNAusgangAddress(moduleAddress, Integer.valueOf(unitNr));
                sendNumber(system, address, value, ValueType.PERCENTAGE);
                break;
            }
            case 'R':
            case 'B': {
                if (unitNr.equals("x")) {
                    int mask = 1;
                    for (int i = 0; i < 8; i++) {
                        final IAddress address = ('R' == unitType) ? new LCNRelaisAddress(moduleAddress, i + 1)
                                : new LCNBinärsensorAddress(moduleAddress, i + 1);
                        sendBoolean(system, address, (value & mask) != 0, ValueType.BOOLEAN);
                        mask <<= 1;
                    }
                }
                break;
            }
            case 'S': {
                switch (value) {
                case 0:
                    sendSumStatus(system, moduleAddress, Integer.valueOf(unitNr), null);
                    break;
                case 25:
                    sendSumStatus(system, moduleAddress, Integer.valueOf(unitNr), LCNSummeAddress.Logic.SOME);
                    break;
                case 50:
                    sendSumStatus(system, moduleAddress, Integer.valueOf(unitNr), LCNSummeAddress.Logic.FULL);
                    break;
                }
                break;
            }
            }
        }
    }

    private void notifyMesswert(final ISystem system, final String text) throws InterruptedException {
        if (text.length() >= 10 && text.charAt(1) == 'M' && text.charAt(8) == '.') {
            final int segmentAddress = Integer.valueOf(text.substring(2, 5));
            final int moduleNr = Integer.valueOf(text.substring(5, 8));
            final LCNModuleAddress moduleAddress = new LCNModuleAddress(segmentAddress, moduleNr);
            final String valueStr = text.substring(9);

            sendNumber(system, moduleAddress, Integer.valueOf(valueStr), ValueType.MEASUREMENT);
        }
    }

    private void notifySummeUndLämpchen(final ISystem system, final String text) throws InterruptedException {
        if (text.length() > 11 && text.charAt(1) == 'M' && text.charAt(8) == '.' && text.charAt(9) == 'T' && text.charAt(10) == 'L') {
            final int segmentAddress = Integer.valueOf(text.substring(2, 5));
            final int moduleNr = Integer.valueOf(text.substring(5, 8));
            final LCNModuleAddress moduleAddress = new LCNModuleAddress(segmentAddress, moduleNr);

            int smallLightCounter = 0;
            int sumCounter = 0;
            for (int i = 11; i < text.length(); i++) {
                switch (text.charAt(i)) {
                case 'A':
                    sendSmallLightStatus(system, moduleAddress, ++smallLightCounter, null);
                    break;
                case 'B':
                    sendSmallLightStatus(system, moduleAddress, ++smallLightCounter, LCNLämpchenAddress.Type.BLINK);
                    break;
                case 'F':
                    sendSmallLightStatus(system, moduleAddress, ++smallLightCounter, LCNLämpchenAddress.Type.FLICKER);
                    break;
                case 'E':
                    sendSmallLightStatus(system, moduleAddress, ++smallLightCounter, LCNLämpchenAddress.Type.ON);
                    break;
                case 'N':
                    sendSumStatus(system, moduleAddress, ++sumCounter, null);
                    break;
                case 'T':
                    sendSumStatus(system, moduleAddress, ++sumCounter, LCNSummeAddress.Logic.SOME);
                    break;
                case 'V':
                    sendSumStatus(system, moduleAddress, ++sumCounter, LCNSummeAddress.Logic.FULL);
                    break;
                default:
                    break;
                }
            }
        }
    }

    private void sendSmallLightStatus(final ISystem system, final LCNModuleAddress moduleAddress, final int unitNr,
            final LCNLämpchenAddress.Type type) throws InterruptedException {
        final LCNLämpchenParentAddress parent = new LCNLämpchenParentAddress(moduleAddress, unitNr);
        final IAddress addressBlink = new LCNLämpchenAddress(parent, LCNLämpchenAddress.Type.BLINK);
        final IAddress addressFlicker = new LCNLämpchenAddress(parent, LCNLämpchenAddress.Type.FLICKER);
        final IAddress addressOn = new LCNLämpchenAddress(parent, LCNLämpchenAddress.Type.ON);
        if (null != type) {
            switch (type) {
            case BLINK:
                sendBoolean(system, addressBlink, true, ValueType.BOOLEAN);
                sendBoolean(system, addressFlicker, false, ValueType.BOOLEAN);
                sendBoolean(system, addressOn, false, ValueType.BOOLEAN);
                break;
            case FLICKER:
                sendBoolean(system, addressBlink, false, ValueType.BOOLEAN);
                sendBoolean(system, addressFlicker, true, ValueType.BOOLEAN);
                sendBoolean(system, addressOn, false, ValueType.BOOLEAN);
                break;
            case ON:
                sendBoolean(system, addressBlink, false, ValueType.BOOLEAN);
                sendBoolean(system, addressFlicker, false, ValueType.BOOLEAN);
                sendBoolean(system, addressOn, true, ValueType.BOOLEAN);
                break;
            }
        } else {
            sendBoolean(system, addressBlink, false, ValueType.BOOLEAN);
            sendBoolean(system, addressFlicker, false, ValueType.BOOLEAN);
            sendBoolean(system, addressOn, false, ValueType.BOOLEAN);
        }
    }

    private void sendSumStatus(final ISystem system, final LCNModuleAddress moduleAddress, final int unitNr,
            final LCNSummeAddress.Logic logic) throws InterruptedException {
        final LCNSummeParentAddress parent = new LCNSummeParentAddress(moduleAddress, unitNr);
        final IAddress addressSome = new LCNSummeAddress(parent, LCNSummeAddress.Logic.SOME);
        final IAddress addressFull = new LCNSummeAddress(parent, LCNSummeAddress.Logic.FULL);
        if (null != logic) {
            switch (logic) {
            case SOME:
                sendBoolean(system, addressSome, true, ValueType.BOOLEAN);
                sendBoolean(system, addressFull, false, ValueType.BOOLEAN);
                break;
            case FULL:
                sendBoolean(system, addressSome, true, ValueType.BOOLEAN);
                sendBoolean(system, addressFull, true, ValueType.BOOLEAN);
                break;
            }
        } else {
            sendBoolean(system, addressSome, false, ValueType.BOOLEAN);
            sendBoolean(system, addressFull, false, ValueType.BOOLEAN);
        }
    }

    private void sendBoolean(final ISystem system, final IAddress address, final boolean value, final ValueType type)
            throws InterruptedException {
        system.send(Priority.NORMAL, new BooleanMessage(new MessageKeyImpl(MessageType.STATUS, address, type), value));
    }

    private void sendNumber(final ISystem system, final IAddress address, final int value, final ValueType type)
            throws InterruptedException {
        system.send(Priority.NORMAL, new NumberMessage(new MessageKeyImpl(MessageType.STATUS, address, type), value));
    }

    private final IHasAddress pchkCommunicator;
}

/*----------------------------------------------------------------------------*/
