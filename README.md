## DASIT - The Dataset Advertisement and System Integration Tool
#### Problem Statement
###### Background
This is a personal project ment to solve the issue of coordinating between several batch/analytical microservices which produce curated datasets on a periodic basis.  I'm sure the utility of a system this broad might be beyond this, but the intended purpose is to allow one microservice that produces batch datasets to advertise when it has completed produing a dataset for a given period of time, say hourly.

###### Technical Requirements
This microservice is based on Spring Boot 2 using gradle and relies on any reasonably recent version of postgresql as a backend database.
