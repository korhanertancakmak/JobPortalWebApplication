package com.kcakmak.jobportal.repository;

import com.kcakmak.jobportal.entity.IRecruiterJobs;
import com.kcakmak.jobportal.entity.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JobPostActivityRepository extends JpaRepository<JobPostActivity, Integer> {

    /*
        Instead of repository level projection of data from DB, we use IRecruiterJobs (interface-based) mapping with
        native SQL query. Spring Data JPA can NOT automatically map a native query to a DTO (RecruiterJobsDto)
        unless it's explicitly instructed to do (by using constructor-based projection manually). In this way, we also
        have getters for required fields, making them faster to handle than DTOs which may include constructors,
        validation logic, or other methods. That's why we first fetch List<IRecruiterJobs> and then transform it into
        List<RecruiterJobsDto> in the service layer and we'll have full control over how the data is converted.

        IRecruiterJobs required methods: Long getTotalCandidates(),
                                         int getJob_post_id(),
                                         String getJob_title();
                                         int getLocationId();
                                         String getCity();
                                         String getState();
                                         String getCountry();
                                         int getCompanyId();
                                         String getName();

        The query below retrieves list of jobs for a given recruiter id, combines/joins job post activity, job location
        and company. It also includes a count of job seekers/candidates who have applied for a given job. This is a native
        SQL, NOT JPQL. That's why we have "nativeQuery = true".
    */
    @Query(value = " SELECT COUNT(s.user_id) as totalCandidates," +
            " j.job_post_id, j.job_title, l.id as locationId, l.city, l.state, l.country, c.id as companyId, c.name" +
            " FROM job_post_activity j " +
            " INNER JOIN job_location l ON j.job_location_id = l.id " +
            " INNER JOIN job_company c ON j.job_company_id = c.id " +
            " LEFT JOIN job_seeker_apply s ON s.job = j.job_post_id " +
            " WHERE j.posted_by_id = :recruiter GROUP BY j.job_post_id" , nativeQuery = true)
    List<IRecruiterJobs> getRecruiterJobs(@Param("recruiter") int recruiter);

    /*
        In this query:
        * retrieve all columns from the job_post_activity table for matching records
        * join the job_post_activity table on job_location_id to include job location details(like city, country, state)
        * filter(WHERE ... LIKE) records where job_title contains the value passed in the job parameter, partially(%)
        * filter continues(AND) jobs by location, against city OR country OR state
        * filter continues(AND) jobs based on their employment type(Part-time, Full-time, Freelance)
        * filter continues(AND) jobs based on work type(Office-only, Remote-only, Partial-Remote)

        Here "type" and "remote" parameters are lists, allowing multiple jobs to be matched simultaneously. This query
        is used when no specific "searchDate" filter is applied. It retrieves jobs based on title, location, job type,
        and work type only.
    */
    @Query(value = "SELECT * FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id " +
            "WHERE j.job_title LIKE %:job% " +
            "AND (l.city LIKE %:location% OR l.country LIKE %:location% OR l.state LIKE %:location%) " +
            "AND (j.job_type IN(:type)) " +
            "AND (j.remote IN(:remote)) ", nativeQuery = true)
    List<JobPostActivity> searchWithoutDate(@Param("job") String job,
                                            @Param("location") String location,
                                            @Param("remote") List<String> remote,
                                            @Param("type") List<String> type);

    /*
        In this query, most of the logic remains the same as in "searchWithoutDate", including the "INNER JOIN"
        and the filtering on "job_title", "location", "job type", and "work type". Additionally we have here:

        * filter continues(AND) jobs that were posted on or after the specified "searchDate"

        The ":date" parameter is a LocalDate object passed from the search method. This query is used
        when a specific "searchDate" is provided (like today, last 7 days, last 30 days). It ensures that
        the results are further filtered by the "posted_date" column.
    */
    @Query(value = "SELECT * FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id  " +
            "WHERE j.job_title LIKE %:job% " +
            "AND (l.city LIKE %:location% OR l.country LIKE %:location% OR l.state LIKE %:location%) " +
            "AND (j.job_type IN(:type)) " +
            "AND (j.remote IN(:remote)) " +
            "AND (posted_date >= :date)", nativeQuery = true)
    List<JobPostActivity> search(@Param("job") String job,
                                 @Param("location") String location,
                                 @Param("remote") List<String> remote,
                                 @Param("type") List<String> type,
                                 @Param("date") LocalDate date);
}
