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

public enum ValueType implements IEnum {
    UNKNOWN, COMMAND, ACKNOWLEDGE, LCN_INTEGER, BOOLEAN, PERCENT, SUMMAND, TEXT, TIME;

    @Override
    public String asString() {
        switch (this) {
        case UNKNOWN:
            return "unknown";
        case COMMAND:
            return "command";
        case ACKNOWLEDGE:
            return "acknowledge";
        case LCN_INTEGER:
            return "lcnInteger";
        case BOOLEAN:
            return "boolean";
        case PERCENT:
            return "percent";
        case SUMMAND:
            return "summand";
        case TEXT:
            return "text";
        case TIME:
            return "time";
        default:
            throw new RuntimeException();
        }
    }

    @Override
    public int asNumber() {
        switch (this) {
        case UNKNOWN:
            return 1;
        case COMMAND:
            return 2;
        case ACKNOWLEDGE:
            return 3;
        case LCN_INTEGER:
            return 4;
        case BOOLEAN:
            return 5;
        case PERCENT:
            return 6;
        case SUMMAND:
            return 7;
        case TEXT:
            return 8;
        case TIME:
            return 9;
        default:
            throw new RuntimeException();
        }
    }
}

/*----------------------------------------------------------------------------*/
