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

package org.openhab.binding.lcn_2;

import java.util.List;

import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.items.Item;

/*----------------------------------------------------------------------------*/

public interface ILCNBindingProvider extends BindingProvider {

    public static class ItemWithUnitAddress {

        public ItemWithUnitAddress(final Item item, ILCNUnitAddress unitAddress) {
            this.item = item;
            this.unitAddress = unitAddress;
        }

        public Item getItem() {
            return item;
        }

        public ILCNUnitAddress getUnitAddress() {
            return unitAddress;
        }

        private final Item item;

        private final ILCNUnitAddress unitAddress;

    }

    List<ItemWithUnitAddress> getItemsFor(ILCNUnitAddress unitAddress);

    ILCNUnitAddress getUnitAddressFor(String itemName);

    Item getItemFor(String itemName);
}

/*----------------------------------------------------------------------------*/
