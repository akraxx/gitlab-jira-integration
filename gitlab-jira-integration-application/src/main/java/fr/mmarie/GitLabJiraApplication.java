package fr.mmarie;

import fr.mmarie.guice.AuthenticationModule;
import fr.mmarie.guice.ConfigurationModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class GitLabJiraApplication extends Application<GitLabJiraConfiguration> {

    @Override
    public void initialize(Bootstrap<GitLabJiraConfiguration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.<GitLabJiraConfiguration>builder()
                        .enableAutoConfig(getClass().getPackage().getName())
                        .modules(new ConfigurationModule(), new AuthenticationModule())
                        .searchCommands(true)
                        .build()
        );
    }

    @Override
    public void run(GitLabJiraConfiguration configuration, Environment environment) throws Exception {
    }

    public static void main(String[] args) throws Exception {
        new GitLabJiraApplication().run(args);
    }

}
