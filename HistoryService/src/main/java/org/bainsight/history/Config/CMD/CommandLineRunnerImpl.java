package org.bainsight.history.Config.CMD;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = "dev")
public class CommandLineRunnerImpl implements CommandLineRunner {
    @Override
    public void run(String... args) {



    }

}
