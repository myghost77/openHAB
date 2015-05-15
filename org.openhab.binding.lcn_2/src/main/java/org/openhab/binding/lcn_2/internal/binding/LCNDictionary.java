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
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openhab.binding.lcn_2.internal.address.LCNModuleAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNAusgangAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNBinärsensorAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNLämpchenAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNReglerSollwertAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNRelaisAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNSummeAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNTemperaturVariableAddress;
import org.openhab.binding.lcn_2.internal.address.unit.LCNZählRechenVariableAddress;
import org.openhab.binding.lcn_2.internal.definition.IAddress;
import org.openhab.binding.lcn_2.internal.definition.ILCNUnitAddress;

/*----------------------------------------------------------------------------*/

public class LCNDictionary {

    public static LCNDictionary getInstance() {
        return instance;
    }

    public synchronized void register(final ILCNUnitAddress unitAddress) {
        // add module address
        IAddress moduleAddress = unitAddress;
        while (null != moduleAddress) {
            if (moduleAddress instanceof LCNModuleAddress) {
                modules.add((LCNModuleAddress) moduleAddress);
            }
            moduleAddress = moduleAddress.getParentAddress();
        }

        // add unit address
        if (unitAddress instanceof LCNTemperaturVariableAddress) {
            temperatureVals.add((LCNTemperaturVariableAddress) unitAddress);
        }
        if (unitAddress instanceof LCNReglerSollwertAddress) {
            regDesiredVals.add((LCNReglerSollwertAddress) unitAddress);
        }
        if (unitAddress instanceof LCNZählRechenVariableAddress) {
            calculationVars.add((LCNZählRechenVariableAddress) unitAddress);
        }
        if (unitAddress instanceof LCNAusgangAddress) {
            outputs.add((LCNAusgangAddress) unitAddress);
        }
        if (unitAddress instanceof LCNRelaisAddress) {
            relais.add((LCNRelaisAddress) unitAddress);
        }
        if (unitAddress instanceof LCNBinärsensorAddress) {
            binSensors.add((LCNBinärsensorAddress) unitAddress);
        }
        if (unitAddress instanceof LCNLämpchenAddress) {
            smallLights.add((LCNLämpchenAddress) unitAddress);
        }
        if (unitAddress instanceof LCNSummeAddress) {
            sums.add((LCNSummeAddress) unitAddress);
        }
    }

    public synchronized List<LCNModuleAddress> getModules() {
        return new ArrayList<LCNModuleAddress>(modules);
    }

    public synchronized List<LCNTemperaturVariableAddress> getTemperatureVals() {
        return new ArrayList<LCNTemperaturVariableAddress>(temperatureVals);
    }

    public synchronized List<LCNReglerSollwertAddress> getRegDesiredVals() {
        return new ArrayList<LCNReglerSollwertAddress>(regDesiredVals);
    }

    public synchronized List<LCNZählRechenVariableAddress> getCalculationVars() {
        return new ArrayList<LCNZählRechenVariableAddress>(calculationVars);
    }

    public synchronized List<LCNAusgangAddress> getOutputs() {
        return new ArrayList<LCNAusgangAddress>(outputs);
    }

    public synchronized List<LCNRelaisAddress> getRelais() {
        return new ArrayList<LCNRelaisAddress>(relais);
    }

    public synchronized List<LCNBinärsensorAddress> getBinSensors() {
        return new ArrayList<LCNBinärsensorAddress>(binSensors);
    }

    public synchronized List<LCNLämpchenAddress> getSmallLights() {
        return new ArrayList<LCNLämpchenAddress>(smallLights);
    }

    public synchronized List<LCNSummeAddress> getSums() {
        return new ArrayList<LCNSummeAddress>(sums);
    }

    private LCNDictionary() {
        // due to singleton
    }

    private static final LCNDictionary instance = new LCNDictionary();

    private final SortedSet<LCNModuleAddress> modules = new TreeSet<LCNModuleAddress>();

    private final SortedSet<LCNTemperaturVariableAddress> temperatureVals = new TreeSet<LCNTemperaturVariableAddress>();

    private final SortedSet<LCNReglerSollwertAddress> regDesiredVals = new TreeSet<LCNReglerSollwertAddress>();

    private final SortedSet<LCNZählRechenVariableAddress> calculationVars = new TreeSet<LCNZählRechenVariableAddress>();

    private final SortedSet<LCNAusgangAddress> outputs = new TreeSet<LCNAusgangAddress>();

    private final SortedSet<LCNRelaisAddress> relais = new TreeSet<LCNRelaisAddress>();

    private final SortedSet<LCNBinärsensorAddress> binSensors = new TreeSet<LCNBinärsensorAddress>();

    private final SortedSet<LCNLämpchenAddress> smallLights = new TreeSet<LCNLämpchenAddress>();

    private final SortedSet<LCNSummeAddress> sums = new TreeSet<LCNSummeAddress>();
}

/*----------------------------------------------------------------------------*/
