# marketing-app - Data Bases 2 Project a.y. 2020/2021
The goal of the project is to implement a web app that deals with gamified consumer data collection.
- User View: a user can access the homepage where the product of the day is published, and he can complete the related questionnaire. The application computes the gamification points of each user that can be checked in the leaderboard.
- Admin View: an admin can access a reserved homepage where he is able to create a new questionnaire for the day of for a future date, inspect or delete a past questionnaire.

## Technologies
The application has been realized in a JEE (Java Enterprise Edition) environment, particularly exploiting JPA, EJBs and Servlets.

#### Backend
- TomEE JEE application server
- JPA for object relational mapping and transaction management
- EJB (stateless) for the business objects
- Java servlet for the client components
- MySQL DBMS
- Triggers and constraints for database-level business constraints and rules.

#### Frontend
- HTML5, CSS, JavaScript

## Documentation
[Here](https://github.com/fulcus/marketing-app/raw/master/docs/specifications.pdf) you can find the complete specifications of the project.

Below you can have a look at the ER Diagram of the project, all design choices are described in detail [here](https://github.com/fulcus/marketing-app/raw/master/docs/documentation.pdf).
![picture](docs/images/ER.jpg)

## Authors
[Luigi Fusco](https://github.com/luigifusco)

[Francesco Gonzales](https://github.com/fulcus)

[Alberto Latino](https://github.com/albertolatino)
