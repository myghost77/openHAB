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

package org.openhab.binding.lcn_2.internal.binding.bridge;

import org.openhab.binding.lcn_2.internal.definition.IVirtualActuatorBindingBridge;

/*----------------------------------------------------------------------------*/

public class BooleanVirtualActuatorBridge extends BooleanActuatorBridge implements IVirtualActuatorBindingBridge {

    public static BooleanVirtualActuatorBridge getInstance() {
        return instance;
    }

    @Override
    public boolean isGroupAllowed() {
        return true;
    }

    protected BooleanVirtualActuatorBridge() {
        // supposed to be a singleton
    }

    private static final BooleanVirtualActuatorBridge instance = new BooleanVirtualActuatorBridge();
}

/*----------------------------------------------------------------------------*/
