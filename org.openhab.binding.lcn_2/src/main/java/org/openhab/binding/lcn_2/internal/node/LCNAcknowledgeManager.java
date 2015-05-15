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

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.address.LCNAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.MessageType;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.definition.ValueType;
import org.openhab.binding.lcn_2.internal.helper.ReceiptHelper;
import org.openhab.binding.lcn_2.internal.message.BooleanMessage;
import org.openhab.binding.lcn_2.internal.message.NumberMessage;
import org.openhab.binding.lcn_2.internal.message.key.MessageKeyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNAcknowledgeManager implements INode {

    @Override
    public void register(final ISystem system) {
        system.register(this, MessageType.COMMAND, LCNAddress.getInstance());
        system.register(this, MessageType.STATUS, LCNAddress.getInstance());
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void join() throws InterruptedException {
        thread.join();
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
        // check for a new command
        if (MessageType.COMMAND == message.getKey().getMessageType()) {
            final long currentTimeMillis = System.currentTimeMillis(); // take current time first

            final IAddress address = message.getKey().getAddress();
            if (address instanceof ILCNUnitAddress) {
                final ILCNUnitAddress unitAddress = (ILCNUnitAddress) address;
                final IAddress2PCKCommand translator = unitAddress.getPCKTranslator();
                if (null != translator) {
                    if (translator.requestReceipt()) {
                        if (ReceiptHelper.requestReceipt(unitAddress)) {
                            final BaseLCNTargetAddress targetAddress = unitAddress.getTargetAddress();
                            synchronized (commands) {
                                if (!commands.containsKey(targetAddress)) {
                                    commands.put(targetAddress, new TreeSet<Data>());
                                }
                                commands.get(targetAddress).add(new Data(unitAddress, currentTimeMillis));
                            }
                        } else {
                            // send a pseudo receipt
                            sendReceipt(system, unitAddress, true);
                        }
                    }
                }
            }
        }

        // check for acknowledgement
        if (MessageType.STATUS == message.getKey().getMessageType() && ValueType.ACKNOWLEDGE == message.getKey().getValueType()
                && message instanceof NumberMessage) {
            final NumberMessage numberMessage = (NumberMessage) message;
            final IAddress address = message.getKey().getAddress();
            if (address instanceof BaseLCNTargetAddress) {
                final BaseLCNTargetAddress targetAddress = (BaseLCNTargetAddress) address;
                final boolean positiveAck = (numberMessage.getValue().asInt() < 0);
                if (!positiveAck) {
                    logger.error("Negative acknowledgement (code: " + numberMessage.getValue().asInt() + ") for '"
                            + targetAddress.getName() + "'.");
                }

                ILCNUnitAddress unitAddress = null;
                synchronized (commands) {
                    if (commands.containsKey(targetAddress)) {
                        final SortedSet<Data> timings = commands.get(targetAddress);
                        if (!timings.isEmpty()) {
                            unitAddress = timings.first().getUnitAddress();
                            timings.remove(timings.first());
                        }
                    }
                }
                if (null != unitAddress) {
                    sendReceipt(system, unitAddress, positiveAck);
                }
            }
        }
    }

    private class TimeoutThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(millisToWait);
                } catch (final InterruptedException e) {
                    // do nothing here
                }

                if (isInterrupted()) {
                    break;
                }

                synchronized (commands) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    for (final BaseLCNTargetAddress targetAddress : commands.keySet()) {
                        final SortedSet<LCNAcknowledgeManager.Data> timings = commands.get(targetAddress);
                        while (!timings.isEmpty() && (currentTimeMillis - timings.first().getTimestamp()) >= maxMillisForTimeout) {
                            logger.warn("Timeout for acknowledgement for '" + timings.first().getUnitAddress() + "'.");
                            timings.remove(timings.first());
                        }
                    }
                }
            }
        }
    }

    private static class Data implements Comparable<Data> {

        public Data(final ILCNUnitAddress unitAddress, final long timestamp) {
            this.unitAddress = unitAddress;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(final Data other) {
            return timestamp.compareTo(other.timestamp);
        }

        public ILCNUnitAddress getUnitAddress() {
            return unitAddress;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        private final ILCNUnitAddress unitAddress;

        private final Long timestamp;
    }

    private void sendReceipt(final ISystem system, final ILCNUnitAddress unitAddress, final boolean result) throws InterruptedException {
        final IMessageKey messageKey = new MessageKeyImpl(MessageType.STATUS, unitAddress, ValueType.ACKNOWLEDGE);
        system.send(Priority.HIGH, new BooleanMessage(messageKey, result));
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNAcknowledgeManager.class);

    private static final long maxMillisForTimeout = 5000; // 5 seconds

    private static final long millisToWait = 250;

    private final SortedMap<BaseLCNTargetAddress, SortedSet<Data>> commands = new TreeMap<BaseLCNTargetAddress, SortedSet<Data>>();

    private final Thread thread = new TimeoutThread();
}

/*----------------------------------------------------------------------------*/
