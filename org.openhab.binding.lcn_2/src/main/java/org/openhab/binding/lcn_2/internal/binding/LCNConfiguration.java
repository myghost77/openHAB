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

import java.util.Dictionary;

import org.openhab.binding.lcn_2.internal.helper.Comparator;
import org.openhab.binding.lcn_2.internal.helper.socket.ISocketHostAddress;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*----------------------------------------------------------------------------*/

public class LCNConfiguration implements ISocketHostAddress {

    public static LCNConfiguration getInstance() {
        return instance;
    }

    @Override
    public int compareTo(final ISocketHostAddress other) {
        final int cmp = Comparator.compareClasses(this, other);
        if (0 != cmp) {
            return cmp;
        } else {
            return 0; // due to singleton
        }
    }

    @Override
    public String getHost() {
        return getPCHKHost();
    }

    @Override
    public int getPort() {
        return getPCHKPort();
    }

    /**
     * Parses and validates the properties from the "openhab.cfg".
     */
    public void parse(Dictionary<String, ?> properties) throws ConfigurationException {
        // set to invalid first
        isValid = false;

        // evaluate properties
        if (null != properties) {
            final Object pchkHostStr = properties.get("pchkHost");
            final Object pchkPortStr = properties.get("pchkPort");
            final Object internalBusMonitorPortStr = properties.get("internalBusMonitorPort");
            final Object pchkUsernameStr = properties.get("pchkUsername");
            final Object pchkPasswordStr = properties.get("pchkPassword");

            // check username and password
            if (null == pchkUsernameStr || null == pchkPasswordStr) {
                throw new ConfigurationException("LCN", "Username and/or password missing.");
            }

            // check host
            if (null != pchkHostStr) {
                pchkHost = (String) pchkHostStr;
            } else {
                pchkHost = standardPCHKHost;
            }

            // check port
            if (null != pchkPortStr) {
                pchkPort = parseInteger((String) pchkPortStr);
            } else {
                pchkPort = standardPCHKPort;
            }

            // check debugging port for console server
            if (null != internalBusMonitorPortStr) {
                internalBusMonitorPort = parseInteger((String) internalBusMonitorPortStr);
            } else {
                internalBusMonitorPort = null;
            }

            // set username and password
            pchkUsername = (String) pchkUsernameStr;
            pchkPassword = (String) pchkPasswordStr;

            // write logging
            logger.info("Configured LCN-PCHK on host '" + pchkHost + "' with port " + pchkPort + ".");
            if (null != internalBusMonitorPort) {
                logger.info("Configured console server for LCN debugging on port " + internalBusMonitorPort + ".");
            } else {
                logger.info("No console server configured for LCN debugging.");
            }

            // set to valid
            isValid = true;
        }
    }

    public String getPCHKHost() {
        return pchkHost;
    }

    public int getPCHKPort() {
        if (null != pchkPort) {
            return pchkPort;
        } else {
            return 0;
        }
    }

    public int getInternalBusMonitorPort() {
        if (null != internalBusMonitorPort) {
            return internalBusMonitorPort;
        } else {
            return 0;
        }
    }

    public String getPCHKUsername() {
        return pchkUsername;
    }

    public String getPCHKPassword() {
        return pchkPassword;
    }

    public boolean isValid() {
        return isValid;
    }

    private static Integer parseInteger(final String value) throws ConfigurationException {
        if (null != value && !value.equals("")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                throw new ConfigurationException("LCN", "Parameter '" + value + "' in wrong format.");
            }
        } else {
            return null;
        }
    }

    private LCNConfiguration() {
        // due to singleton
    }

    private static final Logger logger = LoggerFactory.getLogger(LCNConfiguration.class);

    private static final String standardPCHKHost = "localhost";

    private static final int standardPCHKPort = 4114;

    private static final LCNConfiguration instance = new LCNConfiguration();

    private String pchkHost = null;

    private Integer pchkPort = null;

    private Integer internalBusMonitorPort = null;

    private String pchkUsername = null;

    private String pchkPassword = null;

    private boolean isValid = false;
}

/*----------------------------------------------------------------------------*/
