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

## [Technologies Used]()
<div style="text-align:justify">

In this application, the following technologies are used:

* **Spring Boot:** For building the backend.
* **Spring Data JPA:** For data persistence.
* **Spring Security:** For user authentication and role-based access control.
* **Thymeleaf:** For rendering HTML views.
* **JavaScript:** For supporting the search list.
* **CSS:** For designing the user interface.
* **WebJars** : bootstrap, jquery, font-awesome and webjars-locator
* **MySQL:** Database to store employee and user data.
* **Maven:** For dependency management and build automation.
</div>

</div>

## [Key Classes]()
<div style="text-align:justify">

Here are some of the key classes that we use in our project:

<table style="text-align:justify">
    <thead>
        <th>Name</th>
        <th>Description</th>
    </thead>
    <tbody>
        <tr>
            <td><strong>HomeController</strong></td>
            <td>Show home page</td>
        </tr>
        <tr>
            <td><strong>JobLocationController</strong></td>
            <td>Managing job locations</td>
        </tr>
        <tr>
            <td><strong>JobPostActivityController</strong></td>
            <td>Managing job posts and searching job posts</td>
        </tr>
        <tr>
            <td><strong>JobSeekerApplyController</strong></td>
            <td>Applying for jobs</td>
        </tr>
        <tr>
            <td><strong>JobSeekerProfileController</strong></td>
            <td>Managing job seeker profile</td>
        </tr>
        <tr>
            <td><strong>JobSeekerSaveController</strong></td>
            <td>Managing jobs that job seeker has applied for</td>
        </tr>
        <tr>
            <td><strong>RecruiterProfileController</strong></td>
            <td>Managing recruiter profile</td>
        </tr>
        <tr>
            <td><strong>UsersController</strong></td>
            <td>Login/Logout/Register</td>
        </tr>
    </tbody>
</table>

I use "jobSeeker" as job candidate in the project. Also, each one of those controllers above, 
there is an associated service and repository for those items.
</div>

## [Database Entities]()
<div style="text-align:justify">

Here our database entities in this project:

<table style="text-align:justify">
    <thead>
        <th>Name</th>
        <th>Description</th>
    </thead>
    <tbody>
        <tr>
            <td><strong>JobCompany</strong></td>
            <td>A job company: name, logo, etc.</td>
        </tr>
        <tr>
            <td><strong>JobPostActivity</strong></td>
            <td>A job post: title, description, salary, remote, etc.</td>
        </tr>
        <tr>
            <td><strong>JobSeekerApply</strong></td>
            <td>Tracks the candidates who have applied for a job</td>
        </tr>
        <tr>
            <td><strong>JobSeekerProfile</strong></td>
            <td>Info about candidate: name, city, state, skills, etc.</td>
        </tr>
        <tr>
            <td><strong>JobSeekerSave</strong></td>
            <td>Tracks the jobs a candidate has applied to</td>
        </tr>
        <tr>
            <td><strong>RecruiterProfile</strong></td>
            <td>Info about recruiter: name, city, state, company, etc.</td>
        </tr>
        <tr>
            <td><strong>Skills</strong></td>
            <td>Info about a skill: name, experience level, years of experience</td>
        </tr>
        <tr>
            <td><strong>Users</strong></td>
            <td>Info about a user: email, password, etc.</td>
        </tr>
        <tr>
            <td><strong>UsersType</strong></td>
            <td>Type/role of user: recruiter or a candidate</td>
        </tr>
    </tbody>
</table>

Here's our database diagram in this project:

<div align="center">
    <img src="https://github.com/korhanertancakmak/JobPortalWebApplication/blob/master/images/image02.png" alt="image02">
</div>

All of our database tables and their associated relationships.

</div>

## [Getting Started]()
### [Clone the Repository]()
<div style="text-align:justify">

````shell
git clone https://github.com/korhanertancakmak/JobPortalWebApplication.git
cd JobPortalWebApplication
````
</div>

### [Set Up MySQL Database]()
<div style="text-align:justify">

Run the following commands in your **MySQL** environment:

````shell
DROP DATABASE IF EXISTS `jobportal`;
CREATE DATABASE `jobportal`;
USE `jobportal`;

CREATE TABLE `users_type` (
  `user_type_id` int NOT NULL AUTO_INCREMENT,
  `user_type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users_type` VALUES (1,'Recruiter'),(2,'Job Seeker');

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `registration_date` datetime(6) DEFAULT NULL,
  `user_type_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FK5snet2ikvi03wd4rabd40ckdl` (`user_type_id`),
  CONSTRAINT `FK5snet2ikvi03wd4rabd40ckdl` 
  FOREIGN KEY (`user_type_id`) REFERENCES `users_type` (`user_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_location` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_seeker_profile` (
  `user_account_id` int NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `employment_type` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `resume` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `work_authorization` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_account_id`),
  CONSTRAINT `FKohp1poe14xlw56yxbwu2tpdm7` 
  FOREIGN KEY (`user_account_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `recruiter_profile` (
  `user_account_id` int NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(64) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_account_id`),
  CONSTRAINT `FK42q4eb7jw1bvw3oy83vc05ft6` 
  FOREIGN KEY (`user_account_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_post_activity` (
  `job_post_id` int NOT NULL AUTO_INCREMENT,
  `description_of_job` varchar(10000) DEFAULT NULL,
  `job_title` varchar(255) DEFAULT NULL,
  `job_type` varchar(255) DEFAULT NULL,
  `posted_date` datetime(6) DEFAULT NULL,
  `remote` varchar(255) DEFAULT NULL,
  `salary` varchar(255) DEFAULT NULL,
  `job_company_id` int DEFAULT NULL,
  `job_location_id` int DEFAULT NULL,
  `posted_by_id` int DEFAULT NULL,
  PRIMARY KEY (`job_post_id`),
  KEY `FKpjpv059hollr4tk92ms09s6is` (`job_company_id`),
  KEY `FK44003mnvj29aiijhsc6ftsgxe` (`job_location_id`),
  KEY `FK62yqqbypsq2ik34ngtlw4m9k3` (`posted_by_id`),
  CONSTRAINT `FK44003mnvj29aiijhsc6ftsgxe` 
  FOREIGN KEY (`job_location_id`) REFERENCES `job_location` (`id`),
  CONSTRAINT `FK62yqqbypsq2ik34ngtlw4m9k3` 
  FOREIGN KEY (`posted_by_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKpjpv059hollr4tk92ms09s6is` 
  FOREIGN KEY (`job_company_id`) REFERENCES `job_company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_seeker_save` (
  `id` int NOT NULL AUTO_INCREMENT,
  `job` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1vn1w4dxfiavb5q2gu1n0whxo` (`user_id`,`job`),
  KEY `FKpb44x040gkdltxqy9m7jmvvf3` (`job`),
  CONSTRAINT `FK96dyvgd8hmdohqsfdpvyl89mg` 
  FOREIGN KEY (`user_id`) REFERENCES `job_seeker_profile` (`user_account_id`),
  CONSTRAINT `FKpb44x040gkdltxqy9m7jmvvf3` 
  FOREIGN KEY (`job`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `job_seeker_apply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `apply_date` datetime(6) DEFAULT NULL,
  `cover_letter` varchar(255) DEFAULT NULL,
  `job` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8v6qok40anljlhpkc486nsdmu` (`user_id`,`job`),
  KEY `FKmfhx9q4uclbb74vm49lv9dmf4` (`job`),
  CONSTRAINT `FKmfhx9q4uclbb74vm49lv9dmf4` 
  FOREIGN KEY (`job`) REFERENCES `job_post_activity` (`job_post_id`),
  CONSTRAINT `FKs9fftlyxws2ak05q053vi57qv` 
  FOREIGN KEY (`user_id`) REFERENCES `job_seeker_profile` (`user_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `skills` (
  `id` int NOT NULL AUTO_INCREMENT,
  `experience_level` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `years_of_experience` varchar(255) DEFAULT NULL,
  `job_seeker_profile` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsjdksau8sat30c00aqh5xf2wh` (`job_seeker_profile`),
  CONSTRAINT `FKsjdksau8sat30c00aqh5xf2wh` 
  FOREIGN KEY (`job_seeker_profile`) REFERENCES `job_seeker_profile` (`user_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
````
</div>

### [Run the Application]()
<div style="text-align:justify">

To run the application, use **Maven** to build and start the **Spring Boot** application:

````shell
mvn spring-boot:run
````

You can access the application by navigating to `http://localhost:8080`
</div>

### [License]()
<div style="text-align:justify">

This project is licensed under the MIT License -
see the [LICENSE](https://github.com/korhanertancakmak/JobPortalWebApplication/blob/master/LICENSE) file for details.
</div>