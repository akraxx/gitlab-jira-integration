package fr.mmarie.guice;

import com.google.inject.AbstractModule;
import fr.mmarie.GitLabJiraConfiguration;
import fr.mmarie.core.auth.GitLabAuthFilter;
import fr.mmarie.core.auth.GitLabAuthenticator;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import ru.vyarus.dropwizard.guice.module.support.ConfigurationAwareModule;
import ru.vyarus.dropwizard.guice.module.support.EnvironmentAwareModule;

import javax.ws.rs.core.Response;
import java.security.Principal;

public class AuthenticationModule extends AbstractModule implements EnvironmentAwareModule,
        ConfigurationAwareModule<GitLabJiraConfiguration> {

    private Environment environment;
    private GitLabJiraConfiguration configuration;

    @Override
    protected void configure() {
        environment.jersey().register(new AuthDynamicFeature(new GitLabAuthFilter.Builder()
                .setAuthenticator(new GitLabAuthenticator(configuration.getPassword()))
                .setUnauthorizedHandler((s, s1) -> Response.status(Response.Status.UNAUTHORIZED).build())
                .setRealm("GitLab HOOK")
                .buildAuthFilter()));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setConfiguration(GitLabJiraConfiguration configuration) {
        this.configuration = configuration;
    }
}
