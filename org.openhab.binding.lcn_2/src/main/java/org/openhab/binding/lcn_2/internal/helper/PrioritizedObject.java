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

import org.openhab.binding.lcn_2.internal.definition.Priority;

/*----------------------------------------------------------------------------*/

public class PrioritizedObject<T> implements Comparable<PrioritizedObject<T>> {

    public PrioritizedObject(final Priority priority, final T object) {
        this.priority = priority;
        this.object = object;

        synchronized (maxCounter) {
            counter = maxCounter++;
        }
    }

    @Override
    public int compareTo(final PrioritizedObject<T> other) {
        if (null == other) {
            return -1;
        }

        if (priority.asNumber() < other.priority.asNumber())
            return -1;
        if (priority.asNumber() > other.priority.asNumber())
            return +1;

        return counter.compareTo(other.counter);
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean hasObject() {
        return null != object;
    }

    public T getObject() {
        return object;
    }

    private static Long maxCounter = Long.MIN_VALUE;

    private final Priority priority;

    private final T object;

    private final Long counter;
}

/*----------------------------------------------------------------------------*/
