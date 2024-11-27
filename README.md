# Project Description
<div style="text-align:justify">

This project is the source code of job portal web application written by using **Spring Boot**, **Spring MVC**, 
**Thymeleaf**, **Spring Security**, **JPA**, **Hibernate**, **HTML**, **JavaScript** and **MySQL**.
In this application, a recruiter is able to

- post a new job
- view the jobs
- view list of candidates that have applied for a job
- edit profile and upload profile photo

and a candidate is able to

+ search for a job
+ apply for a job
+ view list of jobs that job candidate has applied for
+ edit profile and upload profile photo
+ upload resume/CV as a document

And also we have some common operations for this application where every user is able to

* register for new account
* login / logout
* make a global search for the job list that shows how many candidates applied

</div>

## [Application Architecture]()
<div style="text-align:justify">

This application architecture is based on the regular **Spring MVC**.
Our controllers will handle our web requests.
We have a service layer that provide our business logic and the repository provides our database access.
And we used Thymeleaf templates for our view or our user interface.
So the browser sends a request into the controller, the controller makes the required calls to the service,
we pass it over to the repository, and we hit the database.
Then we get the results back from the database, and then we send it over to our **Thymeleaf** templates, 
and it's finally returned back to the browser.

<div align="center">
    <img src="https://github.com/korhanertancakmak/JobPortalWebApplication/blob/master/images/image01.png" alt="image01">
</div>

</div>

## [Technologies Used]()
<div style="text-align:justify">


</div>
