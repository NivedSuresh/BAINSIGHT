package org.bainsight.portfolio.Dubug;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Debugger {

    @Value("${spring.application.debug}")
    private boolean DEBUG;

    public void DEBUG(Logger logger, String statement, Object... concatOptions){
        if(DEBUG) logger.info(statement, concatOptions);
    }

}
