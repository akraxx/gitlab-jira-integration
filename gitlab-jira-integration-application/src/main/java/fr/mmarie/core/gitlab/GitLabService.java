package fr.mmarie.core.gitlab;

import com.google.inject.Inject;
import fr.mmarie.api.gitlab.User;
import lombok.NonNull;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;

import static fr.mmarie.utils.Common.sanitizeURL;

public class GitLabService {

    private final GitLabEndPoints gitLabEndPoints;

    private final GitLabConfiguration gitLabConfiguration;

    @Inject
    public GitLabService(@NonNull GitLabConfiguration gitLabConfiguration) {
        this.gitLabConfiguration = gitLabConfiguration;

        this.gitLabEndPoints = new Retrofit.Builder()
                .baseUrl(sanitizeURL(gitLabConfiguration.getUrl()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GitLabEndPoints.class);
    }

    public Response<User> getUser(Long id) throws IOException {
        return gitLabEndPoints.getUser(id, gitLabConfiguration.getPrivateToken()).execute();
    }

    public Response<User> getLoggedUser() throws IOException {
        return gitLabEndPoints.getLoggedUser(gitLabConfiguration.getPrivateToken()).execute();
    }

    public String getUserUrl(String username) {
        String baseUrl = gitLabConfiguration.getUrl();

        return sanitizeURL(baseUrl).concat("u/" + username);
    }

}
