package fr.mmarie.core.jira;

import fr.mmarie.api.jira.Comment;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface JiraEndPoints {
    @POST("/rest/api/2/issue/{issue}/comment")
    Call<Comment> commentIssue(@Path("issue") String issue, @Body Comment comment);
}
