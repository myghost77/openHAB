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

package org.openhab.binding.lcn_2.internal.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.openhab.binding.lcn_2.internal.definition.IMessage;
import org.openhab.binding.lcn_2.internal.definition.IMessageKey;
import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;
import org.openhab.binding.lcn_2.internal.helper.PrioritizedObject;

/*----------------------------------------------------------------------------*/

public class ServerExchangeData {

    public ServerExchangeData(final ISystem system) {
        this.system = system;
    }

    public final ISystem system;

    public final SortedMap<IMessageKey, IMessage> messages = Collections.synchronizedSortedMap(new TreeMap<IMessageKey, IMessage>());

    public final BlockingQueue<PrioritizedObject<IMessage>> queue = new PriorityBlockingQueue<PrioritizedObject<IMessage>>(1024);

    public final List<INode> observers1 = new ArrayList<INode>();

    public final SortedMap<IMessageKey, List<INode>> observers2 = new TreeMap<IMessageKey, List<INode>>();

    public final SortedMap<IMessageKey, List<INode>> observers3 = new TreeMap<IMessageKey, List<INode>>();
}

/*----------------------------------------------------------------------------*/
