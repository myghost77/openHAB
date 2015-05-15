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

package org.openhab.binding.lcn_2.internal.address;

import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.helper.socket.ISocketHostAddress;

/*----------------------------------------------------------------------------*/

public class SocketClientAddress implements IAddress {

    public SocketClientAddress(final ISocketHostAddress hostAddress) {
        this.hostAddress = hostAddress;

        synchronized (maxCounter) {
            counter = maxCounter++;
        }
    }

    @Override
    public int compareTo(final IAddress other) {
        final int cmp = Comparator.compareClasses(this, other);
        if (0 != cmp) {
            return cmp;
        } else {
            final SocketClientAddress other2 = (SocketClientAddress) other;

            if (counter < other2.counter)
                return -1;
            if (counter > other2.counter)
                return +1;

            return hostAddress.compareTo(other2.hostAddress);
        }
    }

    @Override
    public IAddress getParentAddress() {
        return null;
    }

    @Override
    public String getName() {
        return "ClientSocket(" + hostAddress.getHost() + ":" + hostAddress.getPort() + ")";
    }

    private static Long maxCounter = 1L;

    private final ISocketHostAddress hostAddress;

    private final long counter;
}

/*----------------------------------------------------------------------------*/
