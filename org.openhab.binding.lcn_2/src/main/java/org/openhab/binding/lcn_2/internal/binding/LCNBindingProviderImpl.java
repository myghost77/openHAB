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

package org.openhab.binding.lcn_2.internal.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.lcn_2.ILCNBindingProvider;
import org.openhab.binding.lcn_2.internal.address.BaseLCNTargetAddress;
import org.openhab.binding.lcn_2.internal.address.LCNGroupAddress;
import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.address.unit.BaseLCNUnitAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangFlackernAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangFlackernParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangRampeAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangRampeStoppAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNBinärsensorAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLichtszeneAktionAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLichtszeneAktionParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLichtszeneRegistersatzAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNPieperAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNPieperParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNReglerSollwertAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNReglersperreAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNReglersperreParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisGroupAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisGroupAddress.SwitchType;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisGroupParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSendeTasteAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSendeTasteVerzögertAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSendeTasteVerzögertParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeParentAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNTastensperreAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNTemperaturVariableAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNZählRechenVariableAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddressBindingBridge;
import org.openhab.binding.lcn_2.internal.definition.IEnum;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;
import org.openhab.binding.lcn_2.internal.helper.LCNValueConverter;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNBindingProviderImpl extends AbstractGenericBindingProvider implements ILCNBindingProvider {

    public LCNBindingProviderImpl() {
        if (unitAddressDict.isEmpty()) {
            try {
                fillUnitAddressDict();
            } catch (final BindingConfigParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBindingType() {
        return "lcn_2";
    }

    @Override
    public void validateItemType(final Item item, final String bindingConfig) throws BindingConfigParseException {
        // validation is done in 'processBindingConfiguration'
    }

    @Override
    public void processBindingConfiguration(final String context, final Item item, final String bindingConfig)
            throws BindingConfigParseException {
        super.processBindingConfiguration(context, item, bindingConfig);

        logger.debug("Process binding for item '" + item.getName() + "' => '" + bindingConfig.toString() + "' ...");
        final ILCNUnitAddress unitAddress = parse(item, bindingConfig);
        logger.debug("Item '" + item.getName() + "' bound to '" + unitAddress.getName() + "'.");

        LCNDictionary.getInstance().register(unitAddress);
        addBindingConfig(item, unitAddress);
        items.put(item.getName(), item);
    }

    @Override
    public List<ItemWithUnitAddress> getItemsFor(final ILCNUnitAddress unitAddress) {
        final List<ItemWithUnitAddress> result = new ArrayList<ItemWithUnitAddress>();
        for (final String itemName : getItemNames()) {
            final ILCNUnitAddress tempUnitAddress = (ILCNUnitAddress) bindingConfigs.get(itemName);
            if (tempUnitAddress.compareTo(unitAddress) == 0) {
                result.add(new ItemWithUnitAddress(items.get(itemName), tempUnitAddress));
            }
        }
        return result;
    }

    @Override
    public ILCNUnitAddress getUnitAddressFor(final String itemName) {
        if (bindingConfigs.containsKey(itemName)) {
            return (ILCNUnitAddress) bindingConfigs.get(itemName);
        } else {
            return null;
        }
    }

    @Override
    public Item getItemFor(final String itemName) {
        if (items.containsKey(itemName)) {
            return items.get(itemName);
        } else {
            return null;
        }
    }

    // helper class
    private static abstract class UnitAddressCreator {

        public String getUnitConfigText() throws BindingConfigParseException {
            if (null == refAddr) {
                final ILCNUnitAddress tempUnitAddress = create(dummyTargetAddress, null, new HashMap<String, String>());
                if (tempUnitAddress instanceof BaseLCNUnitAddress) {
                    refAddr = (BaseLCNUnitAddress) tempUnitAddress;
                }
                if (null == refAddr) {
                    throw new NullPointerException(); // internal error
                }
            }
            return refAddr.getUnitName();
        }

        public abstract ILCNUnitAddress create(BaseLCNTargetAddress targetAddress, Item item, Map<String, String> bindingConfigParts)
                throws BindingConfigParseException;

        private BaseLCNUnitAddress refAddr = null;
    }

    // helper class
    private static class CheckEnum<T extends IEnum> {

        public CheckEnum(final T[] enumList) {
            this.enumList = enumList;
        }

        public T get(String valueStr) {
            if (null != valueStr) {
                valueStr = valueStr.toUpperCase();
                for (final T value : enumList) {
                    if (valueStr.equals(value.asString().toUpperCase())) {
                        return value;
                    }
                }
            }
            return null;
        }

        private final T[] enumList;
    }

    private static BindingConfigParseException createParseException(final Item item, final String message) {
        final StringBuilder excMsgBuilder = new StringBuilder();
        excMsgBuilder.append("Parsing error for '");
        excMsgBuilder.append(item.getName());
        excMsgBuilder.append("': ");
        excMsgBuilder.append(message);
        return new BindingConfigParseException(excMsgBuilder.toString());
    }

    private static Map<String, String> getParts(final Item item, final String bindingConfig) throws BindingConfigParseException {
        final String[] configParts = bindingConfig.split("\\,");
        final Map<String, String> result = new HashMap<String, String>();
        for (final String configPart : configParts) {
            final String[] keyValPair = configPart.split("\\=");
            if (keyValPair.length != 2) {
                throw createParseException(item, "Part of config not correct: '" + configPart + "'");
            } else {
                final String key = keyValPair[0].trim();
                final String val = keyValPair[1].trim();

                if (key.isEmpty()) {
                    throw createParseException(item, "A key inside the item config is empty.");
                }
                if (val.isEmpty()) {
                    throw createParseException(item, "A value for some item config is empty.");
                }

                result.put(key, val);
            }
        }
        return result;
    }

    private static String popValue(final Item item, final Map<String, String> target, final String key, final String defaultValue)
            throws BindingConfigParseException {
        if (target.containsKey(key)) {
            final String result = target.get(key);
            target.remove(key);
            if (null != result) {
                return result;
            }
        }

        if (null == defaultValue) {
            throw createParseException(item, "Value for '" + key + "' is not defined!");
        }
        return defaultValue;
    }

    private static int popValue(final Item item, final Map<String, String> target, final String key, final String defaultValue,
            final int minValue, final int maxValue) throws BindingConfigParseException {
        final String value = popValue(item, target, key, defaultValue);
        if (null != value) {
            final int result;
            try {
                result = Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                throw createParseException(item, "Cannot convert value to number for '" + key + "': '" + value + "'");
            }

            if (result < minValue) {
                throw createParseException(item, "Number too small for '" + key + "': " + result + " < " + minValue);
            }
            if (result > maxValue) {
                throw createParseException(item, "Number too big for '" + key + "': " + result + " > " + maxValue);
            }

            return result;
        } else {
            throw createParseException(item, "Internal error for '" + key + "'.");
        }
    }

    private static int[] createUnitNumberArray(final int maxNrOfUnits) {
        final int[] result = new int[maxNrOfUnits];
        for (int i = 1; i <= maxNrOfUnits; i++) {
            result[i - 1] = i;
        }
        return result;
    }

    private static void addToUnitAddressDict(final UnitAddressCreator obj) throws BindingConfigParseException {
        unitAddressDict.put(obj.getUnitConfigText(), obj);
    }

    private static void fillUnitAddressDict() throws BindingConfigParseException {
        // LCNAusgangAddress
        for (final int unitNr : createUnitNumberArray(new LCNAusgangAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNAusgangAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNAusgangFlackernParentAddress
        for (final int unitNr : createUnitNumberArray(new LCNAusgangFlackernParentAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNAusgangFlackernParentAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNAusgangRampeAddress
        for (final int unitNr : createUnitNumberArray(new LCNAusgangRampeAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNAusgangRampeAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNAusgangRampeStoppAddress
        for (final int unitNr : createUnitNumberArray(new LCNAusgangRampeStoppAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNAusgangRampeStoppAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNBinärsensorAddress
        for (final int unitNr : createUnitNumberArray(new LCNBinärsensorAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNBinärsensorAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNLämpchenParentAddress
        for (final int unitNr : createUnitNumberArray(new LCNLämpchenParentAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNLämpchenParentAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNLichtszeneAktionParentAddress
        for (final int unitNr : createUnitNumberArray(new LCNLichtszeneAktionParentAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNLichtszeneAktionParentAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNLichtszeneRegistersatzAddress
        addToUnitAddressDict(new UnitAddressCreator() {
            @Override
            public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                    final Map<String, String> bindingConfigParts) {
                return new LCNLichtszeneRegistersatzAddress(targetAddress);
            }
        });

        // LCNPieperParentAddress
        addToUnitAddressDict(new UnitAddressCreator() {
            @Override
            public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                    final Map<String, String> bindingConfigParts) {
                return new LCNPieperParentAddress(targetAddress);
            }
        });

        // LCNReglerSollwertAddress
        for (final int unitNr : createUnitNumberArray(new LCNReglerSollwertAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNReglerSollwertAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNReglersperreParentAddress
        for (final int unitNr : createUnitNumberArray(new LCNReglersperreParentAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNReglersperreParentAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNRelaisAddress
        for (final int unitNr : createUnitNumberArray(new LCNRelaisAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNRelaisAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNRelaisGroupParentAddress
        addToUnitAddressDict(new UnitAddressCreator() {
            @Override
            public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                    final Map<String, String> bindingConfigParts) {
                return new LCNRelaisGroupParentAddress(targetAddress);
            }
        });

        // LCNSendeTasteAddress
        for (final LCNSendeTasteAddress.Type type : LCNSendeTasteAddress.Type.asList()) {
            for (final LCNSendeTasteAddress.Bank bank : LCNSendeTasteAddress.Bank.asList()) {
                for (final int unitNr : createUnitNumberArray(new LCNSendeTasteAddress(dummyTargetAddress, 0, type, bank).getMaxNrOfUnits())) {
                    addToUnitAddressDict(new UnitAddressCreator() {
                        @Override
                        public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                                final Map<String, String> bindingConfigParts) {
                            return new LCNSendeTasteAddress(targetAddress, unitNr, type, bank);
                        }
                    });
                }
            }
        }

        // LCNSendeTasteVerzögertParentAddress
        for (final LCNSendeTasteAddress.Bank bank : LCNSendeTasteAddress.Bank.asList()) {
            for (final int unitNr : createUnitNumberArray(new LCNSendeTasteVerzögertParentAddress(dummyTargetAddress, 0, bank)
                    .getMaxNrOfUnits())) {
                addToUnitAddressDict(new UnitAddressCreator() {
                    @Override
                    public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                            final Map<String, String> bindingConfigParts) {
                        return new LCNSendeTasteVerzögertParentAddress(targetAddress, unitNr, bank);
                    }
                });
            }
        }

        // LCNTastensperreAddress
        for (final LCNSendeTasteAddress.Bank bank : LCNSendeTasteAddress.Bank.asList()) {
            for (final int unitNr : createUnitNumberArray(new LCNTastensperreAddress(dummyTargetAddress, 0, bank).getMaxNrOfUnits())) {
                addToUnitAddressDict(new UnitAddressCreator() {
                    @Override
                    public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                            final Map<String, String> bindingConfigParts) {
                        return new LCNTastensperreAddress(targetAddress, unitNr, bank);
                    }
                });
            }
        }

        // LCNSummeParentAddress
        for (final int unitNr : createUnitNumberArray(new LCNSummeParentAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNSummeParentAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNTemperaturVariableAddress
        for (final int unitNr : createUnitNumberArray(new LCNTemperaturVariableAddress(dummyTargetAddress, 0).getMaxNrOfUnits())) {
            addToUnitAddressDict(new UnitAddressCreator() {
                @Override
                public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                        final Map<String, String> bindingConfigParts) {
                    return new LCNTemperaturVariableAddress(targetAddress, unitNr);
                }
            });
        }

        // LCNZählRechenVariableAddress
        addToUnitAddressDict(new UnitAddressCreator() {
            @Override
            public ILCNUnitAddress create(final BaseLCNTargetAddress targetAddress, final Item item,
                    final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
                // recognize entity
                final String entityStr = popValue(item, bindingConfigParts, "entity", "");
                final LCNValueConverter.Entity entity;
                if (!entityStr.isEmpty()) {
                    entity = new CheckEnum<LCNValueConverter.Entity>(LCNValueConverter.Entity.asList()).get(entityStr);
                    if (null == entity) {
                        throw createParseException(item, "Entity not recognized: '" + entityStr + "'");
                    }
                } else {
                    entity = null;
                }

                // return object
                return new LCNZählRechenVariableAddress(targetAddress, entity);
            }
        });
    }

    private static LCNAusgangFlackernAddress createSpecial(final Item item, final LCNAusgangFlackernParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine type
        final String typeStr = popValue(item, bindingConfigParts, "type", null);
        final LCNAusgangFlackernAddress.Type type = new CheckEnum<LCNAusgangFlackernAddress.Type>(LCNAusgangFlackernAddress.Type.asList())
                .get(typeStr);
        if (null == type) {
            throw createParseException(item, "Type not recognized: '" + typeStr + "'");
        }

        // determine speed
        final String speedStr = popValue(item, bindingConfigParts, "speed", LCNAusgangFlackernAddress.Speed.MEDIUM.asString());
        final LCNAusgangFlackernAddress.Speed speed = new CheckEnum<LCNAusgangFlackernAddress.Speed>(
                LCNAusgangFlackernAddress.Speed.asList()).get(speedStr);
        if (null == speed) {
            throw createParseException(item, "Speed not recognized: '" + typeStr + "'");
        }

        // determine count
        final int count = popValue(item, bindingConfigParts, "count", null, 1, 15);

        // create object
        return new LCNAusgangFlackernAddress(parentAddress, type, speed, count);
    }

    private static LCNLämpchenAddress createSpecial(final Item item, final LCNLämpchenParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine type
        final String typeStr = popValue(item, bindingConfigParts, "type", LCNLämpchenAddress.Type.ON.asString());
        final LCNLämpchenAddress.Type type = new CheckEnum<LCNLämpchenAddress.Type>(LCNLämpchenAddress.Type.asList()).get(typeStr);
        if (null == type) {
            throw createParseException(item, "Type not recognized: '" + typeStr + "'");
        }

        // create object
        return new LCNLämpchenAddress(parentAddress, type);
    }

    private static LCNLichtszeneAktionAddress createSpecial(final Item item, final LCNLichtszeneAktionParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine action
        final String actionStr = popValue(item, bindingConfigParts, "action", LCNLichtszeneAktionAddress.Action.CALL.asString());
        final LCNLichtszeneAktionAddress.Action action = new CheckEnum<LCNLichtszeneAktionAddress.Action>(
                LCNLichtszeneAktionAddress.Action.asList()).get(actionStr);
        if (null == action) {
            throw createParseException(item, "Action not recognized: '" + actionStr + "'");
        }

        // determine register
        final int register = popValue(item, bindingConfigParts, "register", null, 0, 9);

        // create object
        return new LCNLichtszeneAktionAddress(parentAddress, action, register);
    }

    private static LCNPieperAddress createSpecial(final Item item, final LCNPieperParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine mode
        final String modeStr = popValue(item, bindingConfigParts, "mode", null);
        final LCNPieperAddress.Mode mode = new CheckEnum<LCNPieperAddress.Mode>(LCNPieperAddress.Mode.asList()).get(modeStr);
        if (null == mode) {
            throw createParseException(item, "Mode not recognized: '" + modeStr + "'");
        }

        // determine beeps
        final int beeps = popValue(item, bindingConfigParts, "beeps", null, 1, 15);

        // create object
        return new LCNPieperAddress(parentAddress, mode, beeps);
    }

    private static LCNReglersperreAddress createSpecial(final Item item, final LCNReglersperreParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine target
        final String targetModuleStr = popValue(item, bindingConfigParts, "targetModule", "");
        final String targetUnitStr = popValue(item, bindingConfigParts, "targetUnit", "");
        final ILCNUnitAddress target;
        if (targetModuleStr.isEmpty()) {
            if (targetUnitStr.isEmpty()) {
                target = null;
            } else {
                throw createParseException(item, "Target module is missing for item '" + item.getName() + "'.");
            }
        } else {
            if (targetUnitStr.isEmpty()) {
                throw createParseException(item, "Target unit is missing for item '" + item.getName() + "'.");
            } else {
                final Map<String, String> targetBindingConfigParts = new HashMap<String, String>();
                targetBindingConfigParts.put("module", targetModuleStr);
                targetBindingConfigParts.put("unit", targetUnitStr);
                target = parse(item, targetBindingConfigParts);
            }
        }

        // create object
        return new LCNReglersperreAddress(parentAddress, target);
    }

    private static LCNRelaisGroupAddress createSpecial(final Item item, final LCNRelaisGroupParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // recognize relais (switch types)
        final SwitchType[] relais = new SwitchType[LCNRelaisAddress.getMaxNrOfRelais()];
        for (int i = 0; i < relais.length; i++) {
            final String switchTypeStr = popValue(item, bindingConfigParts, "r" + Integer.toString(i + 1), "");
            final LCNRelaisGroupAddress.SwitchType switchType;
            if (switchTypeStr.equals("")) {
                switchType = null;
            } else {
                switchType = new CheckEnum<LCNRelaisGroupAddress.SwitchType>(LCNRelaisGroupAddress.SwitchType.asList()).get(switchTypeStr);
                if (null == switchType) {
                    throw createParseException(item, "Relais switch type not recognized: '" + switchTypeStr + "'");
                }
            }
            relais[i] = switchType;
        }

        // create object
        return new LCNRelaisGroupAddress(parentAddress, relais);
    }

    private static LCNSendeTasteVerzögertAddress createSpecial(final Item item, final LCNSendeTasteVerzögertParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // recognize entity
        final String entityStr = popValue(item, bindingConfigParts, "entity", LCNSendeTasteVerzögertAddress.Entity.SECONDS.asString());
        final LCNSendeTasteVerzögertAddress.Entity entity = new CheckEnum<LCNSendeTasteVerzögertAddress.Entity>(
                LCNSendeTasteVerzögertAddress.Entity.asList()).get(entityStr);
        if (null == entity) {
            throw createParseException(item, "Entity not recognized: '" + entityStr + "'");
        }

        // recognize delay
        final int maxValue;
        switch (entity) {
        case SECONDS:
            maxValue = 60;
            break;
        case MINUTES:
            maxValue = 90;
            break;
        case HOURS:
            maxValue = 50;
            break;
        case DAYS:
            maxValue = 45;
            break;
        default:
            throw new RuntimeException();
        }
        final int delay = popValue(item, bindingConfigParts, "delay", null, 1, maxValue);

        // create object
        return new LCNSendeTasteVerzögertAddress(parentAddress, entity, delay);
    }

    private static LCNSummeAddress createSpecial(final Item item, final LCNSummeParentAddress parentAddress,
            final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // determine logic
        final String logicStr = popValue(item, bindingConfigParts, "logic", null);
        final LCNSummeAddress.Logic logic = new CheckEnum<LCNSummeAddress.Logic>(LCNSummeAddress.Logic.asList()).get(logicStr);
        if (null == logic) {
            throw createParseException(item, "Logic not recognized: '" + logicStr + "'");
        }

        // create object
        return new LCNSummeAddress(parentAddress, logic);
    }

    private static ILCNUnitAddress parse(final Item item, final String bindingConfig) throws BindingConfigParseException {
        if (bindingConfig == null || bindingConfig.isEmpty()) {
            throw createParseException(item, "No binding configuration found.");
        }
        final Map<String, String> bindingConfigParts = getParts(item, bindingConfig);
        return parse(item, bindingConfigParts);
    }

    private static ILCNUnitAddress parse(final Item item, final Map<String, String> bindingConfigParts) throws BindingConfigParseException {
        // read binding config
        final int segmentAddress = popValue(item, bindingConfigParts, "segment", "0", 0, 255);
        final BaseLCNTargetAddress targetAddress;
        final boolean isGroup;
        if (bindingConfigParts.containsKey("module")) {
            if (bindingConfigParts.containsKey("group")) {
                throw createParseException(item, "Module AND group address together is not allowed for item '" + item.getName() + "'!");
            }
            final int moduleNr = popValue(item, bindingConfigParts, "module", null, 0, 255);
            targetAddress = new LCNModuleAddress(segmentAddress, moduleNr);
            isGroup = false;
        } else {
            if (!bindingConfigParts.containsKey("group")) {
                throw createParseException(item, "Module or group address is missing for item '" + item.getName() + "'.");
            }
            final int groupNr = popValue(item, bindingConfigParts, "group", null, 0, 255);
            targetAddress = new LCNGroupAddress(segmentAddress, groupNr);
            isGroup = true;
        }
        final String unitText = popValue(item, bindingConfigParts, "unit", null);

        // create unit address
        ILCNUnitAddress result;
        if (unitAddressDict.containsKey(unitText)) {
            result = unitAddressDict.get(unitText).create(targetAddress, item, bindingConfigParts);
        } else {
            result = null;
        }

        // check if group address is allowed
        if (isGroup && null != result && null != result.getBindingBridge() && !result.getBindingBridge().isGroupAllowed()) {
            throw createParseException(item, "Group address is not allowed for item '" + item.getName() + "'.");
        }

        // check special units
        if (null != result && null == result.getBindingBridge()) {
            if (result instanceof LCNAusgangFlackernParentAddress) {
                result = createSpecial(item, (LCNAusgangFlackernParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNLämpchenParentAddress) {
                result = createSpecial(item, (LCNLämpchenParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNLichtszeneAktionParentAddress) {
                result = createSpecial(item, (LCNLichtszeneAktionParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNPieperParentAddress) {
                result = createSpecial(item, (LCNPieperParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNReglersperreParentAddress) {
                result = createSpecial(item, (LCNReglersperreParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNRelaisGroupParentAddress) {
                result = createSpecial(item, (LCNRelaisGroupParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNSendeTasteVerzögertParentAddress) {
                result = createSpecial(item, (LCNSendeTasteVerzögertParentAddress) result, bindingConfigParts);
            } else if (result instanceof LCNSummeParentAddress) {
                result = createSpecial(item, (LCNSummeParentAddress) result, bindingConfigParts);
            } else {
                result = null; // seems to be an internal error
            }
        }

        // check binding config, if all parts are evaluated
        for (final String key : bindingConfigParts.keySet()) {
            logger.warn("Config part '" + key + "' is ignored to bind item '" + item.getName() + "'!");
        }

        // get binding bridge
        final IAddressBindingBridge bindingBridge;
        if (null != result) {
            bindingBridge = result.getBindingBridge();
        } else {
            bindingBridge = null;
        }

        // check item type
        if (null != result && !(bindingBridge.checkAllowedCommand(result, item) && bindingBridge.checkAllowedState(result, item))) {
            throw createParseException(item, "Wrong item type used for item '" + item.getName() + "'!");
        }

        // return result
        if (null != result) {
            return result;
        } else {
            throw createParseException(item, "LCN unit is unkown: '" + unitText + "'");
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNBindingProviderImpl.class);

    private static final BaseLCNTargetAddress dummyTargetAddress = new LCNGroupAddress(0);

    private static final Map<String, UnitAddressCreator> unitAddressDict = new HashMap<String, UnitAddressCreator>();

    private final Map<String, Item> items = new HashMap<String, Item>();
}

/*----------------------------------------------------------------------------*/
