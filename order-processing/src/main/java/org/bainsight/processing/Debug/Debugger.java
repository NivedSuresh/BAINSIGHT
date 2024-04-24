package org.bainsight.processing.Debug;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Debugger {

    @Value("${spring.application.debug:false}")
    private boolean DEBUG;

    public void DEBUG(Logger logger, String statement, Object... concatOptions){
        if(DEBUG) logger.info(statement, concatOptions);
    }

}
