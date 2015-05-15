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

package org.openhab.binding.lcn_2.internal.helper;

import java.util.Calendar;
import java.util.Date;

import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.definition.Priority;
import org.openhab.binding.lcn_2.internal.message.TimeStatus;

/*----------------------------------------------------------------------------*/

public class TimeThread extends Thread {

    public static long getCurrentMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public TimeThread(final ISystem system) {
        this.system = system;
    }

    @Override
    public void run() {
        try {
            final long secondMillis = 1000L;
            long nextMillis = ((getCurrentMillis() / secondMillis) * secondMillis) + secondMillis;
            while (true) {
                nextMillis += secondMillis;

                final Date nextDate = new Date(nextMillis);
                final long millisToWait = nextMillis - getCurrentMillis();
                if (millisToWait > 0L) {
                    Thread.sleep(millisToWait);
                }
                if (isInterrupted()) {
                    break;
                }

                system.send(Priority.HIGHEST, new TimeStatus(nextDate));
            }
        } catch (final InterruptedException e) {
            system.stop();
        }
    }

    private final ISystem system;
}

/*----------------------------------------------------------------------------*/
