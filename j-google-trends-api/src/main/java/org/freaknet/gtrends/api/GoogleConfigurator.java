/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-api
 *
 *     j-google-trends-api is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     j-google-trends-api is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with j-google-trends-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freaknet.gtrends.api;

/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-api
 *
 * j-google-trends-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * j-google-trends-api is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * j-google-trends-api. If not, see <http://www.gnu.org/licenses/>.
 */
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleConfigurator {

  private static final Logger logger = LoggerFactory.getLogger(GoogleConfigurator.class);
  private static DataConfiguration config = null;
  private static final String CONFIG_FILE = "org/freaknet/gtrends/api/config.properties";

  private GoogleConfigurator() {
  }

  public static DataConfiguration getConfiguration() throws ConfigurationException {
    if (config == null) {
      config = new DataConfiguration(new PropertiesConfiguration(CONFIG_FILE));
    }
    return config;
  }
  
  public static String getLoggerPrefix() {
    try {
      return (String) getConfiguration().getProperty("defaultLoggerPrefix");
    } catch (ConfigurationException ex) {
      logger.warn("Cannot find prefix for logger, messages might not be displayed. Please check config.properties", ex);
      return "";
    }
  }
}
