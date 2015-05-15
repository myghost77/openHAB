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

package org.openhab.binding.lcn_2.internal.node._2pck;

import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.address.LCNGroupAddress;
import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress2PCKCommand;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.helper.ReceiptHelper;

/*----------------------------------------------------------------------------*/

public abstract class BaseAddress2PCKCommand<T extends ILCNUnitAddress> implements IAddress2PCKCommand {

    public static String createCommandStr(final BaseLCNTargetAddress targetAddress, boolean receipt, String command, String data) {
        if (null != targetAddress) {
            final StringBuilder commandBuilder = new StringBuilder();

            commandBuilder.append('>');
            if (targetAddress instanceof LCNModuleAddress) {
                commandBuilder.append('M');
            } else if (targetAddress instanceof LCNGroupAddress) {
                commandBuilder.append('G');
            } else {
                return null;
            }
            commandBuilder.append(translate3Digits(targetAddress.getSegmentAddress()));
            commandBuilder.append(translate3Digits(targetAddress.getNr()));
            if (receipt) {
                commandBuilder.append('!');
            } else {
                commandBuilder.append('.');
            }
            commandBuilder.append(command);
            commandBuilder.append(data);

            return commandBuilder.toString();
        } else {
            return null;
        }
    }

    public static String createCommandStr(final ILCNUnitAddress unitAddress, String command, String data) {
        return createCommandStr(unitAddress.getTargetAddress(), ReceiptHelper.requestReceipt(unitAddress), command, data);
    }

    public static String translate3Digits(int value) {
        if (value < 0)
            value = 0;
        if (value > 255)
            value = 255;

        final String s = ("000" + value);
        return s.substring(s.length() - 3);
    }

    public static String translate1Digit(int value) {
        if (value < 0)
            value = 0;
        if (value > 9)
            value = 9;

        return Integer.toString(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String createCommand(final ILCNUnitAddress unitAddress, final IMessage message) {
        try {
            return __createCommand((T) unitAddress, message);
        } catch (final ClassCastException e) {
            return null;
        }
    }

    protected abstract String __createCommand(final T unitAddress, final IMessage message);
}

/*----------------------------------------------------------------------------*/
