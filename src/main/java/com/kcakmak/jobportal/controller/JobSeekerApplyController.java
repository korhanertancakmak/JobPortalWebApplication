package com.kcakmak.jobportal.controller;

import com.kcakmak.jobportal.entity.*;
import com.kcakmak.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService,
                                    UsersService usersService, JobSeekerApplyService jobSeekerApplyService,
                                    JobSeekerSaveService jobSeekerSaveService,
                                    RecruiterProfileService recruiterProfileService,
                                    JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    // Get request for job details to apply/save mapped as "job-details-apply/{id}"
    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {

        // Retrieve the job by id
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);

        // Add the job details and user into the model
        model.addAttribute("jobDetails", jobDetails);
        model.addAttribute("user", usersService.getCurrentUserProfile());

        // Get the lists of job candidates that applied and saved for a given job
        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);

        // Retrieve current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check whether the authentication object represents an authenticated user who is not anonymous
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // If the logged-in user is authenticated as "Recruiter":
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {

                // Get the current recruiter profile
                RecruiterProfile user = recruiterProfileService.getCurrentRecruiterProfile();
                // Add the candidate list that applied to the job to the model if the user's not null
                if (user != null) {
                    model.addAttribute("applyList", jobSeekerApplyList);
                }

                // If the logged-in user is authenticated as "Job Seeker":
            } else {

                // Get the current candidate profile
                JobSeekerProfile user = jobSeekerProfileService.getCurrentSeekerProfile();
                // If the user's not null, then we will have an applied and a saved list
                if (user != null) {

                    // Initialize the flags for activity and saved jobs
                    boolean exists = false;
                    boolean saved = false;
                    // Loop trough the candidate's applied job list to check whether the job is one of the jobs that applied by the candidate
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if(jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            exists = true; // flag is up
                            break;
                        }
                    }
                    // Loop trough the candidate's saved job list to check whether the job is one of the jobs that saved by the candidate
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            saved = true; // flag is up
                            break;
                        }
                    }

                    // If the job is already applied or saved, add the flag to the model
                    model.addAttribute("alreadyApplied", exists);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }

        // Create a new instance of JobSeekerApply to add to the model
        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob", jobSeekerApply);

        return "job-details";
    }

    // Post request for job details to apply/save mapped as "job-details-apply/{id}"
    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id) {

        // Retrieve current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check whether the authentication object represents an authenticated user who is not anonymous
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            // Retrieve username from the authentication object
            String currentUsername = authentication.getName();
            // Find the user by retrieved username
            Users user = usersService.findByEmail(currentUsername);
            // Retrieve the candidate profile by the user's id
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            // Retrieve the job by id
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            // If candidate profile and job are NOT null:
            if (seekerProfile.isPresent() && jobPostActivity != null) {

                // Create a new instance of JobSeekerApply
                JobSeekerApply jobSeekerApply = new JobSeekerApply();
                // Associate candidate profile, job and current date data into the jobSeekerApply
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());

                // Save the instance of JobSeekerApply into DB
                jobSeekerApplyService.addNew(jobSeekerApply);
            } else {
                throw new RuntimeException("User not found");
            }
        }
        return "redirect:/dashboard/";
    }
}
