package com.kcakmak.jobportal.controller;

import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.entity.UsersType;
import com.kcakmak.jobportal.repository.UsersTypeRepository;
import com.kcakmak.jobportal.services.UsersService;
import com.kcakmak.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    // Constructor Injection for Users and UsersType Services
    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    // User Registration form mapped as "/register"
    @GetMapping("/register")
    public String register(Model model) {
        // retrieve all UsersTypes from DB
        List<UsersType> usersTypes = usersTypeService.getAll();
        // Add list of all UsersTypes into the model
        model.addAttribute("getAllTypes", usersTypes);
        // Create a new Users instance to use the form data
        model.addAttribute("user", new Users());
        return "register";
    }

    // Process data coming from User Registration form mapped as "/register/new"
    // @Valid applies the validation rules defined in the Users entity
    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users, Model model) {

        // Created to fix the duplicate registration bug.
        // Here we find the users by a specific email
        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            // We add the error message to the model
            model.addAttribute("error", "Email already registered, try to login or register with other email.");
            // Sending back to the @GetMapping("/register") to create a new user
            List<UsersType> usersTypes = usersTypeService.getAll();
            model.addAttribute("getAllTypes", usersTypes);
            model.addAttribute("user", new Users());
            return "register";
        }

        // adding the user into the DB
        usersService.addNew(users);
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }
}
