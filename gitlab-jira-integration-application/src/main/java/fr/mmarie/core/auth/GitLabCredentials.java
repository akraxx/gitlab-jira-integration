package fr.mmarie.core.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "service")
public class GitLabCredentials {
    private String service;
    @JsonIgnore
    private String password;
}
