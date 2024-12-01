package com.kcakmak.jobportal.controller;

import com.kcakmak.jobportal.entity.JobSeekerProfile;
import com.kcakmak.jobportal.entity.Skills;
import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.repository.UsersRepository;
import com.kcakmak.jobportal.services.JobSeekerProfileService;
import com.kcakmak.jobportal.util.FileDownloadUtil;
import com.kcakmak.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    // Injection of Users repository and job seeker profile service
    private JobSeekerProfileService jobSeekerProfileService;
    private UsersRepository usersRepository;

    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService,
                                      UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    // Get the job-seeker/candidate profile form
    @GetMapping("/")
    public String JobSeekerProfile(Model model) {

        // Create a new job-seeker profile instance
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();

        // Create a list of Skills for the job-seeker profile
        List<Skills> skills = new ArrayList<>();

        // Check whether the authentication object represents an authenticated user who is not anonymous
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            // Retrieve the User object by a username(email) and job-seeker profile by User object's id
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());

            // If the job-seeker profile is present, set it to our new job-seeker profile instance
            if (seekerProfile.isPresent()) {
                jobSeekerProfile = seekerProfile.get();
                // Check the job-seeker profile's skills is empty?
                if (jobSeekerProfile.getSkills().isEmpty()) {
                    // Set its skills with a new instance of Skills entity
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }

            // add skills and profile to the model
            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
        }
        return "job-seeker-profile";
    }

    // Post the new job-seeker profile to the DB
    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile,
                         @RequestParam("image")MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model) {

        // Retrieve the current authentication
        // Check whether the authentication object represents an authenticated user who is not anonymous
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            // Retrieve the User object by the name field of authentication(email)
            Users user = usersRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            // Set our job seeker profile parameter passed to this method to the User object
            // Set our job seeker profile's user account Id to the Id of the User object
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }

        // Create a list of Skills for the job-seeker profile
        List<Skills> skillsList = new ArrayList<>();
        // add skills and profile to the model
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);

        // Associate the skills with the appropriate job seeker profile accordingly
        for (Skills skill : jobSeekerProfile.getSkills()) {
            skill.setJobSeekerProfile(jobSeekerProfile);
        }

        // Associate the filenames for the profile image and resume
        String imageName = "";
        String resumeName = "";
        if (!Objects.equals(image.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }
        if (!Objects.equals(pdf.getOriginalFilename(), "")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        // Save the job seeker profile into the DB
        jobSeekerProfileService.addNew(jobSeekerProfile);

        // Save the image and resume files into the server
        try {
            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            if (!Objects.equals(pdf.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return "redirect:/dashboard/";
    }


    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model) {

        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(id);
        model.addAttribute("profile", seekerProfile.get());
        return "job-seeker-profile";
    }


    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName,
                                            @RequestParam(value = "userID") String userId) {

        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();
        Resource resource = null;

        try {
            resource = fileDownloadUtil.getFileAsResource("photos/candidate/" + userId, fileName);
        } catch (IOException io) {
            return ResponseEntity.badRequest().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
