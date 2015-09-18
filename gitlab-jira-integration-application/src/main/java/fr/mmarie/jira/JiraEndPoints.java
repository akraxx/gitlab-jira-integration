package fr.mmarie.jira;

import fr.mmarie.api.jira.Comment;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface JiraEndPoints {
    @POST("/issue/${issue}/comment")
    Call<Void> commentIssue(@Path("id") String issue, @Body Comment comment);
}
