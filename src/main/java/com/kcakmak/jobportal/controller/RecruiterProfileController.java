package com.kcakmak.jobportal.controller;

import com.kcakmak.jobportal.entity.RecruiterProfile;
import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.repository.UsersRepository;
import com.kcakmak.jobportal.services.RecruiterProfileService;
import com.kcakmak.jobportal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(UsersRepository usersRepository,
                                      RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    // To get the recruiter profile form
    @GetMapping("/")
    public String recruiterProfile(Model model) {

        // Retrieve current authentication of the user
        // Check whether the authentication object represents an authenticated user who is not anonymous
        // Retrieve the authenticated username, the User object by a username(email) and recruiter profile by id
        // If the recruiter profile is NOT empty, add it to the model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).
                    orElseThrow(() -> new UsernameNotFoundException("Could not found user"));

            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if (!recruiterProfile.isEmpty()) {
                model.addAttribute("profile", recruiterProfile.get());
            }
        }

        return "recruiter_profile";
    }

    // To post the recruiter profile form data to the DB
    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile,
                         @RequestParam("image")MultipartFile multipartFile, Model model) {

        // Retrieve current authentication of the user
        // Check whether the authentication object represents an authenticated user who is not anonymous
        // Retrieve the authenticated username and the "Users" object by a username(email)
        // Associate recruiter profile with existing user account
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername)
                    .orElseThrow(()-> new UsernameNotFoundException("Could not found user"));

            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }

        // Add the recruiter profile to the model with the name of "profile"
        model.addAttribute("profile", recruiterProfile);

        // Associate the recruiter profile image file name if the uploaded file name is NOT empty string
        String filename = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(filename);
        }

        // Save the new recruiter profile data in DB
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        // Create a directory named as savedUser's id and save the profile image in it
        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        // Use saveFile custom method of the FileUploadUtil class that we created to save a file
        try {
            FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
}
