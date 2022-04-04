## DASIT - The Dataset Advertisement and System Integration Tool
#### Problem Statement
###### Background
This is a personal project ment to solve the issue of coordinating between several batch/analytical microservices which produce curated datasets on a periodic basis.  I'm sure the utility of a system this broad might be beyond this, but the intended purpose is to allow one microservice that produces batch datasets to advertise when it has completed produing a dataset for a given period of time, say hourly, for interested clients polling that dataset.

###### Usage
Create one or more groups, which are unique by name and additionally identified by an email address (more than one group may have the same email address).  Create datasets uniquely identified by name and assigned to a group owner.  Publish windows of time describing ranges of time for a completed dataset.  Overlapping or neighboring ranges of time will be merged together.

Set up consumers to query a dataset to see if particular windows of time are available.

Running a local copy of it should allow a swagger ui to be visible at http://localhost:8080/swagger-ui/

###### Technical Requirements
This microservice is based on Spring Boot 2 using gradle and relies on any reasonably recent version of postgresql as a backend database.

###### Potential improvements
* General
  * Data set aliases / alternate names
* Authentication
  * Permissions so that forgotten passwords may be reset
  * Updates to datasets may be done by the group that owns them
  * Inactivated groups may be reactivated by a superuser
* Notifications
  * Clients may 'subscribe' to a dataset and be notified of new published windows or removal/republishing events
