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

package org.openhab.binding.lcn_2.internal.binding;

import java.util.Dictionary;

import org.openhab.binding.lcn_2.ILCNBindingProvider;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.node.InternalBusMonitor;
import org.openhab.binding.lcn_2.internal.node.LCNAcknowledgeManager;
import org.openhab.binding.lcn_2.internal.node.LCNBindingPublisher;
import org.openhab.binding.lcn_2.internal.node.LCNRegulatorLockManager;
import org.openhab.binding.lcn_2.internal.node.LCNStatusGetter;
import org.openhab.binding.lcn_2.internal.node.LCNValueGetter;
import org.openhab.binding.lcn_2.internal.node.LCNVirtualActuatorManager;
import org.openhab.binding.lcn_2.internal.node.PCHKCommunicator;
import org.openhab.binding.lcn_2.internal.node.PCKCommandTranslator;
import org.openhab.binding.lcn_2.internal.node.PCKStatusTranslator;
import org.openhab.binding.lcn_2.internal.node.ShadowMessageSender;
import org.openhab.binding.lcn_2.internal.node.TimeSupplier;
import org.openhab.binding.lcn_2.internal.system.Engine;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNBinding extends AbstractBinding<ILCNBindingProvider> implements ManagedService {

    @Override
    public synchronized void setEventPublisher(final EventPublisher eventPublisher) {
        super.setEventPublisher(eventPublisher);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void activate() {
        super.activate();
    }

    @Override
    public synchronized void deactivate() {
        clearEngine();
        super.deactivate();
    }

    @Override
    public synchronized void updated(final Dictionary<String, ?> config) throws ConfigurationException {
        clearEngine();
        LCNConfiguration.getInstance().parse(config);
        if (LCNConfiguration.getInstance().isValid()) {
            createEngine();
            engine.start();
        }
    }

    public synchronized void postUpdate(final IMessage message) {
        if (null != message) {
            final IAddress address = message.getKey().getAddress();
            if (address instanceof ILCNUnitAddress) {
                final ILCNUnitAddress unitAddress = (ILCNUnitAddress) address;
                if (null != unitAddress) {
                    final IAddressBindingBridge bindingBridge = unitAddress.getBindingBridge();
                    if (null != bindingBridge) {
                        for (final ILCNBindingProvider lcnProvider : providers) {
                            for (final Item item : lcnProvider.getItemsFor(unitAddress)) {
                                final State state = bindingBridge.createState(message, item);
                                if (null != state) {
                                    eventPublisher.postUpdate(item.getName(), state);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected synchronized void internalReceiveCommand(final String itemName, final Command command) {
        if (null != lcnPublisher) {
            for (final ILCNBindingProvider lcnProvider : providers) {
                final ILCNUnitAddress unitAddress = lcnProvider.getUnitAddressFor(itemName);
                if (null != unitAddress) {
                    final IAddressBindingBridge bindingBridge = unitAddress.getBindingBridge();
                    if (null != bindingBridge) {
                        final IMessage message = bindingBridge.createMessage(unitAddress, command);
                        if (null != message) {
                            // forward message to internal event bus of binding
                            lcnPublisher.send(engine.getSystem(), message);
                        }
                    }
                }
            }
        }
    }

    private void createEngine() {
        if (LCNConfiguration.getInstance().isValid()) {
            engine = new Engine();

            engine.addNode(new InternalBusMonitor());
            engine.addNode(new TimeSupplier());

            final PCHKCommunicator communicator = new PCHKCommunicator();
            engine.addNode(communicator);
            engine.addNode(new PCKCommandTranslator(communicator));
            engine.addNode(new PCKStatusTranslator(communicator));
            engine.addNode(new LCNStatusGetter(communicator));
            engine.addNode(new LCNValueGetter(communicator));
            engine.addNode(new LCNAcknowledgeManager());
            engine.addNode(new ShadowMessageSender());
            engine.addNode(new LCNVirtualActuatorManager());
            engine.addNode(new LCNRegulatorLockManager());

            lcnPublisher = new LCNBindingPublisher(this);
            engine.addNode(lcnPublisher);

            logger.info("Engine created.");
        } else {
            lcnPublisher = null;
            engine = null;
        }
    }

    private void clearEngine() {
        if (engine != null) {
            logger.info("Destroy engine.");

            engine.end();

            lcnPublisher = null;
            engine = null;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNBinding.class);

    private EventPublisher eventPublisher = null;

    private LCNBindingPublisher lcnPublisher = null;

    private Engine engine = null;
}

/*----------------------------------------------------------------------------*/
