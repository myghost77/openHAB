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

/*----------------------------------------------------------------------------*/

public class SocketServerClientAddress implements IAddress {

    public SocketServerClientAddress(final SocketServerAddress parentAddress) {
        this.parentAddress = parentAddress;

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
            final SocketServerClientAddress other2 = (SocketServerClientAddress) other;
            final int parentCompare = parentAddress.compareTo(other2.parentAddress);
            if (0 == parentCompare) {
                return counter.compareTo(other2.counter);
            } else {
                return parentCompare;
            }
        }
    }

    @Override
    public IAddress getParentAddress() {
        return parentAddress;
    }

    @Override
    public String getName() {
        return parentAddress.getName() + ":" + counter;
    }

    private static Long maxCounter = 1L;

    private final SocketServerAddress parentAddress;

    private final Long counter;
}

/*----------------------------------------------------------------------------*/
