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

import org.openhab.core.items.Item;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/*----------------------------------------------------------------------------*/

public interface IAddressBindingBridge {

    boolean checkAllowedCommand(ILCNUnitAddress unitAddress, Item item);

    boolean checkAllowedState(ILCNUnitAddress unitAddress, Item item);

    boolean isGroupAllowed();

    IMessage createMessage(ILCNUnitAddress unitAddress, Command command);

    State createState(IMessage message, Item item);
}

/*----------------------------------------------------------------------------*/
