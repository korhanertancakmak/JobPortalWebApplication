package com.kcakmak.jobportal.services;

import com.kcakmak.jobportal.entity.Users;
import com.kcakmak.jobportal.repository.UsersRepository;
import com.kcakmak.jobportal.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // This method comes from the implemented UserDetailsService interface
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // We designed that emails are the usernames in the app
        // loading the user using findByEmail method in the users repository
        Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
        // We return an instance of CustomUserDetails class (that's in the util package) with the user as a parameter
        return new CustomUserDetails(user);
    }
}
