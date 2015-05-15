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

public class Comparator {

    public static class InternalError extends RuntimeException {
        private static final long serialVersionUID = 2592214182735831174L;
    }

    public static int compareClasses(final Object o1, final Object o2) {
        if (null == o1) {
            throw new NullPointerException();
        }

        if (null == o2) {
            return -1;
        }

        final Class<?> c1 = o1.getClass();
        final Class<?> c2 = o2.getClass();

        if (c1.equals(c2)) {
            return 0;
        } else {
            final int cmp = c1.getName().compareTo(c2.getName());
            if (0 == cmp) {
                throw new InternalError();
            }
            return cmp;
        }
    }
}

/*----------------------------------------------------------------------------*/
