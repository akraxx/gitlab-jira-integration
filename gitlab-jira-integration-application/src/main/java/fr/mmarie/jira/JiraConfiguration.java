package fr.mmarie.jira;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class JiraConfiguration {
    @NotEmpty
    @NotNull
    private String user;

    @NotEmpty
    @NotNull
    private String password;

    @NotEmpty
    @NotNull
    private String url;
}
