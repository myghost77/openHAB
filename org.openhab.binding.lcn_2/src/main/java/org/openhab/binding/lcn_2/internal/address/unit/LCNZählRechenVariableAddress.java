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

package org.openhab.binding.lcn_2.internal.address.unit;

import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.binding.bridge.IntegerSensorBridge;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;

/*----------------------------------------------------------------------------*/

public class LCNZählRechenVariableAddress extends BaseLCNUnitAddress {

    public LCNZählRechenVariableAddress(final BaseLCNTargetAddress targetAddress, final LCNValueConverter.Entity entity) {
        super(targetAddress);
        this.entity = entity;
    }

    @Override
    public IAddressBindingBridge getBindingBridge() {
        return IntegerSensorBridge.getInstance(entity);
    }

    @Override
    public IAddress2PCKCommand getPCKTranslator() {
        return null;
    }

    @Override
    public String getUnitPrefix() {
        return "calcVar";
    }

    @Override
    public int getMaxNrOfUnits() {
        return 1;
    }

    private final LCNValueConverter.Entity entity;
}

/*----------------------------------------------------------------------------*/
