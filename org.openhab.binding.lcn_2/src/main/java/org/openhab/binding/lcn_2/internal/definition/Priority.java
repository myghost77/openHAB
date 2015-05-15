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

public enum Priority implements IEnum {
    HIGHEST, HIGH, NORMAL, LOW, LOWEST;

    @Override
    public String asString() {
        switch (this) {
        case HIGHEST:
            return "highest";
        case HIGH:
            return "high";
        default: // NORMAL
            return "normal";
        case LOW:
            return "low";
        case LOWEST:
            return "lowest";
        }
    }

    @Override
    public int asNumber() {
        switch (this) {
        case HIGHEST:
            return 1;
        case HIGH:
            return 2;
        default: // NORMAL
            return 3;
        case LOW:
            return 4;
        case LOWEST:
            return 5;
        }
    }
}

/*----------------------------------------------------------------------------*/
