package com.kcakmak.jobportal.config;

import com.kcakmak.jobportal.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    // These are the URLs that Spring Security will not provide protection support
    private final String[] publicUrl = {"/",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css", "/*.js", "/*js.map", "/fonts**", "/favicon.ico", "/resources/**", "/error"};

    // Injection of custom UserDetailsService
    private final CustomUserDetailsService customUserDetailsService;

    // Injection of custom AuthenticationSuccessHandler
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);


    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(auth ->{
            auth.requestMatchers(publicUrl).permitAll();    // No need for user to login
            auth.anyRequest().authenticated();              // Any other requests that comes in has to be authenticated
        });

        // Setup login page, custom AuthenticationSuccessHandler and logout page
        http.formLogin(form -> form.loginPage("/login").permitAll()
                .successHandler(customAuthenticationSuccessHandler))
                .logout(logout->{
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                }).cors(Customizer.withDefaults())
                .csrf(csrf->csrf.disable());

        return http.build();
    }

    // This is our custom authentication provider method
    // that tells Spring Security how to find our users and how to authenticate passwords for the users
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        // This sets up the user detail service
        // that tells Spring security how to retrieve the users from DB
        // custom user details service is injected
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        return authenticationProvider;
    }

    // This is our custom password encoder
    // that tells Spring Security how to authenticate passwords (plain text or encryption)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
