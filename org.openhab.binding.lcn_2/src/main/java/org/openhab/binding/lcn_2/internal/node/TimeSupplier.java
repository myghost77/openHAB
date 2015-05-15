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

import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.helper.TimeThread;

/*----------------------------------------------------------------------------*/

public class TimeSupplier implements INode {

    @Override
    public void register(final ISystem system) {
        if (null == timeThread) {
            timeThread = new TimeThread(system);
        }
    }

    @Override
    public void start() {
        if (null != timeThread) {
            timeThread.start();
        }
    }

    @Override
    public void stop() {
        if (null != timeThread) {
            timeThread.interrupt();
        }
    }

    @Override
    public void join() throws InterruptedException {
        if (null != timeThread) {
            timeThread.join();
        }
    }

    @Override
    public void notify(final ISystem system, final IMessage message, final Priority priority) throws InterruptedException {
    }

    private Thread timeThread = null;
}

/*----------------------------------------------------------------------------*/
