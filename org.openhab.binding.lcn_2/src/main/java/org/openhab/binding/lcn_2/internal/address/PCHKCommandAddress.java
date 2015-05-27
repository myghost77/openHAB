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

public class PCHKCommandAddress implements IAddress {

    public static IAddress getInstance() {
        return instance;
    }

    @Override
    public int compareTo(final IAddress other) {
        final int cmp = Comparator.compareClasses(this, other);
        if (0 != cmp) {
            return cmp;
        } else {
            return 0; // due to singleton
        }
    }

    @Override
    public IAddress getParentAddress() {
        return null;
    }

    @Override
    public String getName() {
        return "PCHKCommand";
    }

    private PCHKCommandAddress() {
        // due to singleton
    }

    private static final IAddress instance = new PCHKCommandAddress();
}

/*----------------------------------------------------------------------------*/
