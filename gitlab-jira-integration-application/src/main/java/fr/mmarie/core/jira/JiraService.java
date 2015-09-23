package fr.mmarie.core.jira;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import fr.mmarie.api.jira.Comment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JiraService {

    private final JiraConfiguration jiraConfiguration;

    private final JiraEndPoints jiraEndPoints;

    public JiraService(@NonNull JiraConfiguration jiraConfiguration) {
        this.jiraConfiguration = jiraConfiguration;

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setReadTimeout(120, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        httpClient.interceptors().add(chain -> {
            String credentials = jiraConfiguration.getUsername() + ":" + jiraConfiguration.getPassword();
            String encodedHeader = "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()));

            Request requestWithAuthorization = chain.request().newBuilder().addHeader("Authorization", encodedHeader).build();
            return chain.proceed(requestWithAuthorization);
        });

        this.jiraEndPoints = new Retrofit.Builder()
                .baseUrl(jiraConfiguration.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(JiraEndPoints.class);
    }

    public Response<Object> getIssue(String issue) throws IOException {
        return jiraEndPoints.getIssue(issue).execute();
    }

    public Response<Comment> commentIssue(String issue, Comment comment) throws IOException {
        return jiraEndPoints.commentIssue(issue, comment).execute();
    }

    public Response<Map<String, Object>> serverInfo() throws IOException {
        return jiraEndPoints.serverInfo().execute();
    }

    public boolean isExistingIssue(String issue) {
        try {
            return (getIssue(issue).code() == 200);
        } catch (Exception e) {
            log.error("Unable to get issue <{}>", issue, e);
            return false;
        }
    }
}
