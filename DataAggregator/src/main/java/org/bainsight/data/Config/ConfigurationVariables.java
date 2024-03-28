package org.bainsight.data.Config;

import org.springframework.beans.factory.annotation.Value;


public class ConfigurationVariables {

    @Value("${is.holiday}")
    public static boolean IS_HOLIDAY;

    @Value("${exchange.id}")
    public static String[] EXCHANGES;


}
