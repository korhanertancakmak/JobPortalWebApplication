package com.kcakmak.jobportal.repository;

import com.kcakmak.jobportal.entity.JobPostActivity;
import com.kcakmak.jobportal.entity.JobSeekerApply;
import com.kcakmak.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    // abstract methods for candidate profiles that are applied to jobs
    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);
    List<JobSeekerApply> findByJob(JobPostActivity job);
}
