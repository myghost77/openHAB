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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public final class LCNActivator implements BundleActivator {

    public void start(final BundleContext context) throws Exception {
        LCNActivator.context = context;
        logger.info("LCN binding has been started.");
    }

    public void stop(final BundleContext context) throws Exception {
        logger.info("LCN binding has been stopped.");
        LCNActivator.context = null;
    }

    public static BundleContext getContext() {
        return context;
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNActivator.class);

    private static BundleContext context = null;
}

/*----------------------------------------------------------------------------*/
