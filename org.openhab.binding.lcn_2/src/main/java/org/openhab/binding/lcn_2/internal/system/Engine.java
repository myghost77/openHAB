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
import java.util.List;

import org.openhab.binding.lcn_2.internal.definition.INode;
import org.openhab.binding.lcn_2.internal.definition.ISystem;

/*----------------------------------------------------------------------------*/

public class Engine {

    public void addNode(final INode node) {
        nodes.add(node);
        node.register(server);
    }

    public void start() {
        for (final INode node : nodes) {
            node.start();
        }
        server.start();
    }

    public void end() {
        // stop nodes
        for (final INode node : nodes) {
            node.stop();
        }
        server.stop();

        // join nodes
        for (final INode node : nodes) {
            try {
                node.join();
            } catch (final InterruptedException e) {
                // do nothing
            }
        }
        try {
            server.join();
        } catch (final InterruptedException e) {
            // do nothing
        }
    }

    public ISystem getSystem() {
        return server;
    }

    private final List<INode> nodes = new ArrayList<INode>();

    private final Server server = new Server();
}

/*----------------------------------------------------------------------------*/
