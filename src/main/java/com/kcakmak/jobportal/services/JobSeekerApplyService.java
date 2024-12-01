package com.kcakmak.jobportal.services;

import com.kcakmak.jobportal.entity.JobPostActivity;
import com.kcakmak.jobportal.entity.JobSeekerApply;
import com.kcakmak.jobportal.entity.JobSeekerProfile;
import com.kcakmak.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    @Autowired
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    // This method returns a list of applied jobs by the candidate's id
    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId) {
        return jobSeekerApplyRepository.findByUserId(userAccountId);
    }

    // This method returns a list of candidates that are applied to the job by the job
    public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
        return jobSeekerApplyRepository.findByJob(job);
    }

    // This method saves the jobSeekerApply data into the DB
    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }
}
