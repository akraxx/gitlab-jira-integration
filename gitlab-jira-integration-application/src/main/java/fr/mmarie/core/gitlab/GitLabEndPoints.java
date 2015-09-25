package fr.mmarie.core.gitlab;

import fr.mmarie.api.gitlab.User;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface GitLabEndPoints {
    @GET("api/v3/users/{id}")
    Call<User> getUser(@Path("id") Long id, @Query("private_token") String privateToken);

    @GET("api/v3/user")
    Call<User> getLoggedUser(@Query("private_token") String privateToken);
}
