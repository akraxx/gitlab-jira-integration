gitlab-jira-integration 
=======================

[![Build Status](https://travis-ci.org/akraxx/gitlab-jira-integration.svg)](https://travis-ci.org/akraxx/gitlab-jira-integration) [![Coverage Status](https://coveralls.io/repos/akraxx/gitlab-jira-integration/badge.svg?branch=master&service=github)](https://coveralls.io/github/akraxx/gitlab-jira-integration?branch=master) [![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.mmarie/gitlab-jira-integration/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/fr.mmarie/gitlab-jira-integration) [![Issue Stats](http://issuestats.com/github/akraxx/gitlab-jira-integration/badge/issue?style=flat)](http://issuestats.com/github/akraxx/gitlab-jira-integration)

If you are using *GitLab Community Edition* and you want to fully integrate gitlab with JIRA (ie. comment issues), you can use this project. It's a standalone java service which provides endpoints to intercepts hook events from gitlab and use JIRA REST api to interacts with issues.

Requirements
============

* A JVM (jdk 8)
* An account on JIRA which can comment issues on projects
* An account on gitlab to have a private_token (admin rights are not required)

How to use
==========

* Download the fatjar **gitlab-jira-integration-application** from maven central
* Create a yaml file as follow and configure it

```yaml

server:
  applicationConnectors:
    - type: http
      port: 9090

  adminConnectors:
    - type: http
      port: 9091

logging:
  appenders:
    - type: console
      logFormat: "%-5p [%d{ISO8601,UTC}] [%thread] [%c{5}] [hookId:%X{hookId:--}] : %m%n%rEx"
      
password: test-password

jira:
  username: gitlab
  password: gitlab
  url: http://localhost:8090

gitlab:
  private_token: N1bJ4n8-rbFAEf8Syrh2
  url: http://192.168.59.104:8080
  
```

* Launch your JAR like this : ```java -jar gitlab-jira-integration-application.jar server properties.yml```

If everything is ok you should have something like this in your console :

```
INFO  [2015-10-10 17:48:43,361] [main] [i.d.j.DropwizardResourceConfig] [hookId:-] : The following paths were found for the configured resources:

    POST    /hook (fr.mmarie.resources.HookResource)

INFO  [2015-10-10 17:48:43,365] [main] [o.e.j.s.h.ContextHandler] [hookId:-] : Started i.d.j.MutableServletContextHandler@43a0a32d{/,null,AVAILABLE}
INFO  [2015-10-10 17:48:43,371] [main] [i.d.s.AdminEnvironment] [hookId:-] : tasks = 

    POST    /tasks/log-level (io.dropwizard.servlets.tasks.LogConfigurationTask)
    POST    /tasks/gc (io.dropwizard.servlets.tasks.GarbageCollectionTask)

INFO  [2015-10-10 17:48:43,376] [main] [o.e.j.s.h.ContextHandler] [hookId:-] : Started i.d.j.MutableServletContextHandler@5aaaa446{/,null,AVAILABLE}
INFO  [2015-10-10 17:48:43,394] [main] [o.e.j.s.ServerConnector] [hookId:-] : Started application@794b435f{HTTP/1.1}{0.0.0.0:9090}
INFO  [2015-10-10 17:48:43,398] [main] [o.e.j.s.ServerConnector] [hookId:-] : Started admin@38f2e97e{HTTP/1.1}{0.0.0.0:9091}
INFO  [2015-10-10 17:48:43,399] [main] [o.e.j.s.Server] [hookId:-] : Started @3812ms
```

You need to generate a token to authenticate your hook, format of the token is :
[service:pwd] encoded in Base64, where service is any value you want to identify your gitlab hook, and password is the one defined in the YAML configuration file.

* Add a new WebHook service in gitlab settings to : ```http://[IP/HOSTNAME]:9090/hook?token=[see above]```

* Commit messages with a JIRA issue prefixed by **#** will be mentionnd in issue comments. (For example : **#TESTGIT-1**)

Dependencies
============

* [Dropwizard](http://www.dropwizard.io/)
* [Retrofit](http://square.github.io/retrofit/)
