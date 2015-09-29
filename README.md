gitlab-jira-integration 
=======================

[![Build Status](https://travis-ci.org/akraxx/gitlab-jira-integration.svg)](https://travis-ci.org/akraxx/gitlab-jira-integration) [![Coverage Status](https://coveralls.io/repos/akraxx/gitlab-jira-integration/badge.svg?branch=master&service=github)](https://coveralls.io/github/akraxx/gitlab-jira-integration?branch=master) [![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.mmarie/gitlab-jira-integration/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/fr.mmarie/gitlab-jira-integration)

If you are using *GitLab Community Edition* and you want to fully integrate gitlab with JIRA (ie. comment issues), you can use this project. It's a standalone java service which provides endpoints to intercepts hook events from gitlab and use JIRA REST api to interacts with issues.

Requirements
============

* A JVM (jdk 8)
* An account on JIRA which have admin access on projects
* An admin account on gitlab to have a private_token

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

jira:
  username: gitlab
  password: gitlab
  url: http://localhost:8090

gitlab:
  private_token: N1bJ4n8-rbFAEf8Syrh2
  url: http://192.168.59.104:8080
  
```

* Launch your JAR like this : ```java -jar gitlab-jira-integration-application.jar server properties.yml```
* Add a new WebHook service in gitlab settings to : ```http://[IP/HOSTNAME]:9090/hook```

* Commit messages with a JIRA issue prefixed by **#** will be mentionnd in issue comments. (For example : **#TESTGIT-1**)

Dependencies
============

* [Dropwizard](http://www.dropwizard.io/)
* [Retrofit](http://square.github.io/retrofit/)
