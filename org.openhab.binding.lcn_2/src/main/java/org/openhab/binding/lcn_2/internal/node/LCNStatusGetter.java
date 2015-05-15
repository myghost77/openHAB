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
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeAddress;
import org.openhab.binding.lcn_2.internal.binding.LCNDictionary;
import org.openhab.binding.lcn_2.internal.definition.IHasAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.message.TextMessage;
import org.openhab.binding.lcn_2.internal.message.TimeStatus;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.openhab.binding.lcn_2.internal.node._2pck.BaseAddress2PCKCommand;

/*----------------------------------------------------------------------------*/

public class LCNStatusGetter implements INode {

    public LCNStatusGetter(final IHasAddress pchkCommunicator) {
        this.pchkCommunicator = pchkCommunicator;

        // fill definitions
        for (int nr = 1; nr <= LCNAusgangAddress.getMaxNrOfOutputs(); nr++) {
            this.definitions.add(new OutputStatusDef(nr));
        }
        this.definitions.add(new RelaisStatusDef());
        this.definitions.add(new BinSensorStatusDef());
        this.definitions.add(new TableauStatusDef());
    }

    @Override
    public void register(final ISystem system) {
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
        if (message instanceof TimeStatus) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(((TimeStatus) message).getDateTime());
            final int minutes = calendar.get(Calendar.MINUTE);
            final int seconds = calendar.get(Calendar.SECOND);

            for (final IStatusDefinition definition : definitions) {
                if (definition.checkExecution(minutes, seconds)) {
                    // determine module addresses
                    final SortedSet<LCNModuleAddress> moduleAddresses = new TreeSet<LCNModuleAddress>();
                    for (final ILCNUnitAddress unitAddress : definition.getUnits()) {
                        final BaseLCNTargetAddress targetAddress = unitAddress.getTargetAddress();
                        if (targetAddress instanceof LCNModuleAddress) {
                            moduleAddresses.add((LCNModuleAddress) targetAddress);
                        }
                    }

                    // send commands
                    for (final LCNModuleAddress moduleAddress : moduleAddresses) {
                        final String commandStr = BaseAddress2PCKCommand.createCommandStr(moduleAddress, false,
                                definition.getPCHKCommand(), "");
                        system.send(Priority.LOWEST, new TextMessage(new MessageKeyImpl(MessageType.COMMAND, pchkCommunicator.getAddress(),
                                ValueType.TEXT), commandStr));
                    }
                }
            }
        }
    }

    private static interface IStatusDefinition {

        boolean checkExecution(int minutes, int seconds);

        List<? extends ILCNUnitAddress> getUnits();

        String getPCHKCommand();
    }

    private static class OutputStatusDef implements IStatusDefinition {

        public OutputStatusDef(final int nr) {
            this.nr = nr;
        }

        @Override
        public boolean checkExecution(final int minutes, final int seconds) {
            return ((nr - 1) == minutes % LCNAusgangAddress.getMaxNrOfOutputs()) && (45 == seconds);
        }

        @Override
        public List<? extends ILCNUnitAddress> getUnits() {
            final List<ILCNUnitAddress> result = new ArrayList<ILCNUnitAddress>();
            for (final LCNAusgangAddress outputAddress : LCNDictionary.getInstance().getOutputs()) {
                if (nr == outputAddress.getUnitNr()) {
                    result.add(outputAddress);
                }
            }
            return result;
        }

        @Override
        public String getPCHKCommand() {
            return "SMA" + nr + "F";
        }

        private final int nr;
    }

    private static class RelaisStatusDef implements IStatusDefinition {

        @Override
        public boolean checkExecution(final int minutes, final int seconds) {
            return (1 == minutes % 2) && (15 == seconds);
        }

        @Override
        public List<? extends ILCNUnitAddress> getUnits() {
            return LCNDictionary.getInstance().getRelais();
        }

        @Override
        public String getPCHKCommand() {
            return "SMRF";
        }
    }

    private static class BinSensorStatusDef implements IStatusDefinition {

        @Override
        public boolean checkExecution(final int minutes, final int seconds) {
            return (0 == minutes % 2) && (15 == seconds);
        }

        @Override
        public List<? extends ILCNUnitAddress> getUnits() {
            return LCNDictionary.getInstance().getBinSensors();
        }

        @Override
        public String getPCHKCommand() {
            return "SMBF";
        }
    }

    private static class TableauStatusDef implements IStatusDefinition {

        @Override
        public boolean checkExecution(final int minutes, final int seconds) {
            return (0 == seconds) || (30 == seconds);
        }

        @Override
        public List<? extends ILCNUnitAddress> getUnits() {
            final List<ILCNUnitAddress> result = new ArrayList<ILCNUnitAddress>();
            for (final LCNLämpchenAddress smallLightAddress : LCNDictionary.getInstance().getSmallLights()) {
                result.add(smallLightAddress);
            }
            for (final LCNSummeAddress sumAddress : LCNDictionary.getInstance().getSums()) {
                result.add(sumAddress);
            }
            return result;
        }

        @Override
        public String getPCHKCommand() {
            return "SMT";
        }
    }

    private final List<IStatusDefinition> definitions = new ArrayList<IStatusDefinition>();

    private final IHasAddress pchkCommunicator;
}

/*----------------------------------------------------------------------------*/
