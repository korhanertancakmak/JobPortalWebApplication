package com.kcakmak.jobportal.services;

import com.kcakmak.jobportal.entity.*;
import com.kcakmak.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    // Saving the new job post in DB
    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    // This method returns a list of RecruiterJobsDto based on the query with recruiter's id parameter
    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter) {
        // Retrieve a list of IRecruiterJobs (interface-based) data by getRecruiterJobs() method in the repository
        List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);
        // Convert the info from recruiterJobsDtos (interface-based) data to DTOs
        List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();
        for (IRecruiterJobs rec : recruiterJobsDtos) {
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
            recruiterJobsDtoList.add(new RecruiterJobsDto(rec.getTotalCandidates(), rec.getJob_post_id(),
                                                          rec.getJob_title(), loc, comp));
        }

        // return constructed DTO back
        return recruiterJobsDtoList;
    }

    // This method returns the job by id
    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // This method returns all jobs as a list of JobPostActivity from DB
    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    // This method returns a list of JobPostActivity from DB with a search logic with required parameters
    public List<JobPostActivity> search(String job, String location,
                                        List<String> type, List<String> remote,
                                        LocalDate searchDate) {

        // If we have a not null search date flag parameter, we use search(),
        // If we don't, then we use searchWithoutDate() native sql query methods in the interface
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate(job, location, remote, type)
                : jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }
}
