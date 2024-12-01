package com.kcakmak.jobportal.services;

import com.kcakmak.jobportal.entity.JobSeekerProfile;
import com.kcakmak.jobportal.entity.RecruiterProfile;
import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.repository.JobSeekerProfileRepository;
import com.kcakmak.jobportal.repository.RecruiterProfileRepository;
import com.kcakmak.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    // Constructor Injections
    private final UsersRepository usersRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, JobSeekerProfileRepository jobSeekerProfileRepository,
                        RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user into DB
    public Users addNew(Users users) {
        // Initial values for the user
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        // Encrypt the user's password
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // Save the user in "Users" Table of DB
        Users savedUser = usersRepository.save(users);

        // If user's type id is 1 then it's a "recruiter"
        // If user's type id is NOT 1 then it's a "job seeker"
        // We use Spring Data JPA repository classes to save the recruiter/job seeker profile
        int userTypeId = users.getUsersTypeId().getUserTypeId();
        if (userTypeId == 1) {
            recruiterProfileRepository.save(new RecruiterProfile(savedUser));
        } else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
        }
        return savedUser;
    }

    // Created to fix the duplicate registration bug that finds the users by a specific email
    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    // Retrieve the current user profile as a Recruiter or Job Seeker
    public Object getCurrentUserProfile() {

        // Retrieve current authentication of the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check whether the authentication object represents an authenticated user who is not anonymous
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            // Retrieve the authenticated username
            String username = authentication.getName();
            // Retrieve the User object by a username(email)
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            // Retrieve the user's Id
            int userId = users.getUserId();
            // Check whether the user is a Recruiter or a Job Seeker
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
                return recruiterProfile;
            } else {
                JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                return jobSeekerProfile;
            }
        }
        return null;
    }

    // This method returns the authenticated user
    public Users getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof  AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            return user;
        }
        return null;
    }

    // This method returns the user by authenticated username
    public Users findByEmail(String currentUsername) {
        return usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
