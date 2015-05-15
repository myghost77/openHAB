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

import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;

/*----------------------------------------------------------------------------*/

public class ReceiptHelper {

    public static boolean requestReceipt(final ILCNUnitAddress unitAddress) {
        if (null != unitAddress) {
            if (unitAddress.getTargetAddress() instanceof LCNModuleAddress) {
                final IAddress2PCKCommand translator = unitAddress.getPCKTranslator();
                if (null != translator) {
                    return translator.requestReceipt();
                }
            }
        }
        return false;
    }
}

/*----------------------------------------------------------------------------*/
