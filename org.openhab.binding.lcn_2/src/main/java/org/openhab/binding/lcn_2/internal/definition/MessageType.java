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

package org.openhab.binding.lcn_2.internal.definition;

/*----------------------------------------------------------------------------*/

public enum MessageType implements IEnum {
    COMMAND, STATUS;

    @Override
    public String asString() {
        switch (this) {
        case COMMAND:
            return "Command";
        case STATUS:
            return "Status";
        default:
            throw new RuntimeException();
        }
    }

    @Override
    public int asNumber() {
        switch (this) {
        case COMMAND:
            return 1;
        case STATUS:
            return 2;
        default:
            throw new RuntimeException();
        }
    }
}

/*----------------------------------------------------------------------------*/
