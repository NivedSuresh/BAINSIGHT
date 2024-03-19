package org.bainsight.liquidity.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class ConfigurationVariables {

    @Value("${is.holiday}")
    public static boolean IS_HOLIDAY;

    @Value("${exchange.id}")
    public static String[] EXCHANGES;

}
