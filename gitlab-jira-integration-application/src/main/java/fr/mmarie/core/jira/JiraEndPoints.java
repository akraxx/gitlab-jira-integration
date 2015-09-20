package fr.mmarie.core.jira;

import fr.mmarie.api.jira.Comment;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.Map;

public interface JiraEndPoints {
    @GET("/rest/api/2/issue/{issue}")
    Call<Object> getIssue(@Path("issue") String issue);

    @POST("/rest/api/2/issue/{issue}/comment")
    Call<Comment> commentIssue(@Path("issue") String issue, @Body Comment comment);

    @GET("/rest/api/2/serverInfo")
    Call<Map<String, Object>> serverInfo();
}
