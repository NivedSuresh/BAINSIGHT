package org.bainsight.portfolio.Config.JobRunr;


import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JobRunrConfig {

    @Bean
    public JobScheduler jobScheduler(DataSource dataSource){
        return JobRunr.configure().useStorageProvider(new PostgresStorageProvider(dataSource))
                .useBackgroundJobServer()
                .initialize()
                .getJobScheduler();
    }


}
