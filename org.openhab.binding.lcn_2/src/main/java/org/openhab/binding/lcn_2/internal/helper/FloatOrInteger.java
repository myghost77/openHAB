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

/*----------------------------------------------------------------------------*/

public class FloatOrInteger {

    public FloatOrInteger(final float value) {
        this.strVal = Float.toString(value);
        this.floatVal = value;
        this.intVal = Math.round(value);
    }

    public FloatOrInteger(final int value) {
        this.strVal = Integer.toString(value);
        this.floatVal = (float) value;
        this.intVal = value;
    }

    @Override
    public String toString() {
        return strVal;
    }

    public float asFloat() {
        return floatVal;
    }

    public int asInt() {
        return intVal;
    }

    private final String strVal;

    private final float floatVal;

    private final int intVal;
}

/*----------------------------------------------------------------------------*/
