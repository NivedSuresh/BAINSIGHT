package org.bainsight.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//The stockbroker shall maintain logs of all trading activities to facilitate audit trail.
//The stockbroker shall maintain record of control parameters, orders, trades and
//data points emanating from trades executed through algorithm trading.
@SpringBootApplication
public class JournalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JournalServiceApplication.class, args);
    }

}
