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

/*----------------------------------------------------------------------------*/

public class LCNModuleAddress extends BaseLCNTargetAddress {

    public LCNModuleAddress(final int segmentAddress, final int nr) {
        super(segmentAddress, nr);
    }

    public LCNModuleAddress(final int segmentAddress) {
        super(segmentAddress);
    }

    @Override
    protected char getTargetPrefix() {
        return 'M';
    }
}

/*----------------------------------------------------------------------------*/
