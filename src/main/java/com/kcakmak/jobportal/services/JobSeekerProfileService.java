package com.kcakmak.jobportal.services;

import com.kcakmak.jobportal.entity.JobSeekerProfile;
import com.kcakmak.jobportal.entity.RecruiterProfile;
import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.repository.JobSeekerProfileRepository;
import com.kcakmak.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    // Injection of Users and job seeker profile repositories
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UsersRepository usersRepository;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository,
                                   UsersRepository usersRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.usersRepository = usersRepository;
    }

    // This method returns job seeker profile by id
    public Optional<JobSeekerProfile> getOne(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }

    // This method saves the job seeker profile passed to here
    public void addNew(JobSeekerProfile jobSeekerProfile) {
        jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    // This method returns the candidate profile if the authenticated user is a candidate profile, otherwise it's null
    public JobSeekerProfile getCurrentSeekerProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users user = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> jobSeekerProfile = getOne(user.getUserId());
            return jobSeekerProfile.orElse(null);
        } else return null;
    }
}
