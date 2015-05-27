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

public class LCNLämpchenHandler {

    public static enum State {
        ON, OFF, UNKNOWN
    }

    public void setCurrentFlickerState(final boolean isFlickering) {
        if (isFlickering) {
            flickerState = State.ON;
        } else {
            flickerState = State.OFF;
        }
    }

    public void setCurrentBlinkState(final boolean isBlinking) {
        if (State.UNKNOWN == flickerState) {
            flickerState = State.OFF;
        }

        if (isBlinking) {
            blinkState = State.ON;
        } else {
            blinkState = State.OFF;
        }
    }

    public void setCurrentOnState(final boolean isOn) {
        if (State.UNKNOWN == flickerState) {
            flickerState = State.OFF;
        }

        if (State.UNKNOWN == blinkState) {
            blinkState = State.OFF;
        }

        if (isOn) {
            onState = State.ON;
        } else {
            onState = State.OFF;
        }
    }

    public State getCurrentFlickerState() {
        return flickerState;
    }

    public State getCurrentBlinkState() {
        if (State.ON == blinkState) {
            if (State.ON == flickerState) {
                return State.OFF;
            }
        }

        return blinkState;
    }

    public State getCurrentOnState() {
        if (State.ON == onState) {
            if (State.ON == flickerState) {
                return State.OFF;
            }

            if (State.ON == blinkState) {
                return State.OFF;
            }
        }

        return onState;
    }

    private State flickerState = State.UNKNOWN;

    private State blinkState = State.UNKNOWN;

    private State onState = State.UNKNOWN;
}

/*----------------------------------------------------------------------------*/
