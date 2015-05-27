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

import org.openhab.binding.lcn_2.internal.definition.IEnum;

/*----------------------------------------------------------------------------*/

public class LCNValueConverter {

    public enum Entity implements IEnum {
        CELSIUS, OLD_LUX, LUX, AMP, VOLT, WIND, CO2;

        public static Entity[] asList() {
            return new Entity[] { CELSIUS, OLD_LUX, LUX, AMP, VOLT, WIND, CO2 };
        }

        @Override
        public String asString() {
            switch (this) {
            case CELSIUS:
                return "celsius";
            case OLD_LUX:
                return "old_lux";
            case LUX:
                return "lux";
            case AMP:
                return "amp";
            case VOLT:
                return "volt";
            case WIND:
                return "wind";
            case CO2:
                return "co2";
            default:
                throw new RuntimeException();
            }
        }

        @Override
        public int asNumber() {
            switch (this) {
            case CELSIUS:
                return 1;
            case OLD_LUX:
                return 2;
            case LUX:
                return 3;
            case AMP:
                return 4;
            case VOLT:
                return 5;
            case WIND:
                return 6;
            case CO2:
                return 7;
            default:
                throw new RuntimeException();
            }
        }
    }

    public static IConverter get(final Entity entity) {
        if (null != entity) {
            switch (entity) {
            case CELSIUS:
                return celsiusConverter;
            case OLD_LUX:
                return oldLuxConverter;
            case LUX:
                return luxConverter;
            case AMP:
                return ampConverter;
            case VOLT:
                return voltConverter;
            case WIND:
                return windConverter;
            case CO2:
                return co2Converter;
            default:
                return null;
            }
        } else {
            return null;
        }
    }

    public static interface IConverter {

        float fromLcn(int value);

        int toLCN(float value);
    }

    private static class Celsius implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return (((float) value) - offset) / f;
        }

        @Override
        public int toLCN(final float value) {
            return Math.round(value * f + offset);
        }

        private static final float offset = 1000.0f;

        private static final float f = 10.0f;
    }

    private static class OldLux implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return (float) Math.exp(f1 * (double) value + f2);
        }

        @Override
        public int toLCN(final float value) {
            return Math.round((float) ((Math.log(value) - f2) / f1));
        }

        private static final float f1 = 0.010380664f;

        private static final float f2 = 1.689646994f;
    }

    private static class Lux implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return (float) Math.exp((float) value / f);
        }

        @Override
        public int toLCN(final float value) {
            return Math.round((float) (Math.log(value) * f));
        }

        private static final float f = 100.0f;
    }

    private static class Amp implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return ((float) value) / f;
        }

        @Override
        public int toLCN(final float value) {
            return Math.round(value * f);
        }

        private static final float f = 200.0f;
    }

    private static class Volt implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return ((float) value) / f;
        }

        @Override
        public int toLCN(final float value) {
            return Math.round(value * f);
        }

        private static final float f = 400.0f;
    }

    private static class Wind implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return ((float) value) / f;
        }

        @Override
        public int toLCN(final float value) {
            return Math.round(value * f);
        }

        private static final float f = 10.0f;
    }

    private static class CO2 implements IConverter {

        @Override
        public float fromLcn(final int value) {
            return (float) value;
        }

        @Override
        public int toLCN(final float value) {
            return Math.round(value);
        }
    }

    private static final Celsius celsiusConverter = new Celsius();

    private static final OldLux oldLuxConverter = new OldLux();

    private static final Lux luxConverter = new Lux();

    private static final Amp ampConverter = new Amp();

    private static final Volt voltConverter = new Volt();

    private static final Wind windConverter = new Wind();

    private static final CO2 co2Converter = new CO2();
}

/*----------------------------------------------------------------------------*/
