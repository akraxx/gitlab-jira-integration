package fr.mmarie.core.gitlab;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

import javax.ws.rs.QueryParam;

public interface GitLabEndPoints {
    @GET("/api/v3/users/{id}")
    Call<Object> getUser(@Path("id") Long id, @QueryParam("private_token") String privateToken);
}
