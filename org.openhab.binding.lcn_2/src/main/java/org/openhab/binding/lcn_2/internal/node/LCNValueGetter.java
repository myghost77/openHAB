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

import java.util.ArrayList;
import java.util.List;

import org.openhab.binding.lcn_2.internal.address.LCNAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNReglerSollwertAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNTemperaturVariableAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNZählRechenVariableAddress;
import org.openhab.binding.lcn_2.internal.binding.LCNDictionary;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.TextMessage;
import org.openhab.binding.lcn_2.internal.message.TimeStatus;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.binding.lcn_2.internal.node._2pck.BaseAddress2PCKCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNValueGetter implements INode {

    public LCNValueGetter(final IHasAddress pchkCommunicator) {
        this.pchkCommunicator = pchkCommunicator;
    }

    @Override
    public void register(final ISystem system) {
        system.register(this, MessageType.STATUS, LCNAddress.getInstance());
        system.register(this, TimeStatus.getMessageKey());
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
        final long currentTimeMillis = System.currentTimeMillis();
        final long timeDiff = currentTimeMillis - lastActionMillis;

        if (ValueType.TIME == message.getKey().getValueType()) {
            if (null != currentValue) {
                if (timeDiff >= maxMillisForTimeout) {
                    logger.warn("Timeout for requested value '" + currentValue.getUnitAddress().getName() + "'.");
                    currentValue = null;
                }
            }

            if (null == currentValue) {
                if (timeDiff >= millisToWait) {
                    // fill values list
                    if (values.isEmpty()) {
                        for (final LCNTemperaturVariableAddress tempVarAddr : LCNDictionary.getInstance().getTemperatureVals()) {
                            switch (tempVarAddr.getUnitNr()) {
                            case 1:
                                values.add(new ValueDefinition(tempVarAddr, "MWTA", true));
                                break;
                            case 2:
                                values.add(new ValueDefinition(tempVarAddr, "MWTB", true));
                                break;
                            default:
                                break;
                            }
                        }

                        for (final LCNReglerSollwertAddress regDesValAddr : LCNDictionary.getInstance().getRegDesiredVals()) {
                            switch (regDesValAddr.getUnitNr()) {
                            case 1:
                                values.add(new ValueDefinition(regDesValAddr, "MWSA", true));
                                break;
                            case 2:
                                values.add(new ValueDefinition(regDesValAddr, "MWSB", true));
                                break;
                            default:
                                break;
                            }
                        }

                        for (final LCNZählRechenVariableAddress calcVarAddr : LCNDictionary.getInstance().getCalculationVars()) {
                            values.add(new ValueDefinition(calcVarAddr, "MWV", false));
                        }
                    }

                    // pick out next item
                    if (!values.isEmpty()) {
                        lastActionMillis = currentTimeMillis;
                        currentValue = values.get(0);
                        values.remove(0);
                    }

                    // send command
                    if (null != currentValue) {
                        final String commandStr = BaseAddress2PCKCommand.createCommandStr(currentValue.getUnitAddress().getTargetAddress(),
                                false, currentValue.getPCHKCommand(), "");
                        system.send(Priority.LOW, new TextMessage(new MessageKeyImpl(MessageType.COMMAND, pchkCommunicator.getAddress(),
                                ValueType.TEXT), commandStr));
                    }
                }
            }
        } else {
            if (null != currentValue) {
                if (ValueType.MEASUREMENT == message.getKey().getValueType() && message instanceof NumberMessage) {
                    final int value = ((NumberMessage) message).getValue().asInt();
                    if (currentValue.isTemperature()) {
                        system.send(Priority.NORMAL, new NumberMessage(new MessageKeyImpl(MessageType.STATUS,
                                currentValue.getUnitAddress(), ValueType.TEMPERATURE), LCNValueConverter.measurement2Temperature(value)));
                    } else {
                        system.send(Priority.NORMAL, new NumberMessage(new MessageKeyImpl(MessageType.STATUS,
                                currentValue.getUnitAddress(), ValueType.INTEGER), value));
                    }

                    lastActionMillis = currentTimeMillis;
                    currentValue = null;
                }
            }
        }
    }

    private static class ValueDefinition {

        public ValueDefinition(final ILCNUnitAddress unitAddress, final String pchkCommand, boolean isTemperature) {
            this.unitAddress = unitAddress;
            this.pchkCommand = pchkCommand;
            this.isTemperature = isTemperature;
        }

        public ILCNUnitAddress getUnitAddress() {
            return unitAddress;
        }

        public String getPCHKCommand() {
            return pchkCommand;
        }

        public boolean isTemperature() {
            return isTemperature;
        }

        private final ILCNUnitAddress unitAddress;

        private final String pchkCommand;

        private final boolean isTemperature;
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNValueGetter.class);

    private static final long maxMillisForTimeout = 7000; // 7 seconds

    private static final long millisToWait = 2000; // 2 seconds

    private final IHasAddress pchkCommunicator;

    private final List<ValueDefinition> values = new ArrayList<ValueDefinition>();

    private ValueDefinition currentValue = null;

    private long lastActionMillis = 0;
}

/*----------------------------------------------------------------------------*/
