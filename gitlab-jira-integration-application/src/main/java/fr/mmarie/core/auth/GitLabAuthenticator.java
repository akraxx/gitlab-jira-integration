package fr.mmarie.core.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.NonNull;

import java.security.Principal;

public class GitLabAuthenticator implements Authenticator<GitLabCredentials, Principal> {

    private final String password;

    public GitLabAuthenticator(@NonNull String password) {
        this.password = password;
    }

    @Override
    public Optional<Principal> authenticate(GitLabCredentials credentials) throws AuthenticationException {
        if(password.contentEquals(credentials.getPassword())) {
            return Optional.of(credentials::getService);
        } else {
            return Optional.absent();
        }
    }
}
