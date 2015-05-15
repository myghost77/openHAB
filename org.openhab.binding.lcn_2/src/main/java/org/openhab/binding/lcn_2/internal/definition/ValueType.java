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
    UNKNOWN, MEASUREMENT, TEMPERATURE, INTEGER, BOOLEAN, PERCENTAGE, ADDING, TEXT, SECONDS, TIME, COMMAND, ACKNOWLEDGE;

    @Override
    public String asString() {
        switch (this) {
        case UNKNOWN:
            return "Unknown";
        case MEASUREMENT:
            return "Measurement";
        case TEMPERATURE:
            return "Temperature";
        case INTEGER:
            return "Integer";
        case BOOLEAN:
            return "Boolean";
        case PERCENTAGE:
            return "Percentage";
        case ADDING:
            return "Adding";
        case TEXT:
            return "Text";
        case SECONDS:
            return "Seconds";
        case TIME:
            return "Time";
        case COMMAND:
            return "Command";
        case ACKNOWLEDGE:
            return "Acknowledge";
        default:
            throw new RuntimeException();
        }
    }

    @Override
    public int asNumber() {
        switch (this) {
        case UNKNOWN:
            return 10;
        case MEASUREMENT:
            return 11;
        case TEMPERATURE:
            return 12;
        case INTEGER:
            return 13;
        case BOOLEAN:
            return 14;
        case PERCENTAGE:
            return 15;
        case ADDING:
            return 16;
        case TEXT:
            return 17;
        case SECONDS:
            return 18;
        case TIME:
            return 19;
        case COMMAND:
            return 20;
        case ACKNOWLEDGE:
            return 21;
        default:
            throw new RuntimeException();
        }
    }
}

/*----------------------------------------------------------------------------*/
