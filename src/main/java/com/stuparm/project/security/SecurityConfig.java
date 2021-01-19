package com.stuparm.project.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationContext appContext;

    @Value("${server.ssl.enabled}")
    private Boolean sslEnabled;

    @Value("${server.security.users.file}")
    private String usersFilePath;

    @Value("${server.security.password.encoder}")
    private String passwordEncoder;

    @Value("${springdoc.swagger-ui.path}")
    private String openapiUri;

    @Value("${server.security.csrf.enabled}")
    private Boolean csrfEnabled;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (sslEnabled) {
            log.debug("Change http channel comm");
            http.requiresChannel().
                 anyRequest().
                 requiresSecure();
        }


        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole(Role.ADMIN.name())
                .antMatchers("/gates/**").hasRole(Role.USER.name())
                .and()
                .httpBasic();

        if (csrfEnabled == null || !csrfEnabled) {
            log.info("Disabling CSRF ...");
            http.csrf().disable();
        }


        http.authorizeRequests().
                antMatchers(openapiUri).permitAll().
                antMatchers("/v3/api-docs/**").permitAll().
                antMatchers("/swagger-ui/**").permitAll();

        http.authorizeRequests().antMatchers("/**").denyAll();
    }





    @Bean
    @Override
    public UserDetailsService userDetailsService(){

        Set<FileUser> fileUsers = loadUsers();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        for (FileUser fileUser : fileUsers) {
            manager.createUser(User.
                    withUsername(fileUser.username).
                    password(fileUser.encodedPassword).
                    roles(fileUser.role.name()).
                    build());

        }

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        switch (passwordEncoder) {
            case "bcrypt": return new BCryptPasswordEncoder();
            // add different case statements for different encoders
        }

        // switch/case not found, stop application
        log.error("Not valid password encoder {}, possible values are [ bcrypt, scrypt, pbkdf2 ]", passwordEncoder);
        SpringApplication.exit(appContext, () -> 1);
        return null;  // not reachable, important because of compiler
    }


    private Set<FileUser> loadUsers() {

        Set<FileUser> fileUsers = new HashSet<>();


//        File f = ResourceUtils.getFile(usersFilePath).toURI();

        try (Stream<String> stream = Files.lines(Paths.get(ResourceUtils.getFile(usersFilePath).toURI()))) {
            stream.forEach(line -> {
                if (line.isBlank()) return;   // skip blank line, probably end of file ...

                String[] fields = line.trim().split(":");

                if (fields.length != 3) {
                    log.error("users file {} at line {} does not have 3 csv values", usersFilePath, line);
                    SpringApplication.exit(appContext, () -> 1);
                }

                FileUser fileUser = new FileUser();
                fileUser.username = fields[0].trim();
                fileUser.encodedPassword = fields[1].trim();
                fileUser.role = Role.valueOf(fields[2].trim());

                if (fileUsers.contains(fileUser)) {
                    log.error("Duplicate username [ {} ] in users file {}", fileUser.username, usersFilePath);
                    SpringApplication.exit(appContext, () -> 1);
                }
                fileUsers.add(fileUser);

            });
        } catch (IOException e) {

            log.error("Error during reading the file {} {}", usersFilePath, e);
            SpringApplication.exit(appContext, () -> 1);
        }

        return fileUsers;

    }


    /**
     * User that is stored in file as a single line
     * Format: username:hashed-password:role
     */
    private class FileUser {
        String username;
        String encodedPassword;
        Role role;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileUser fileUser = (FileUser) o;
            return new EqualsBuilder().append(username, fileUser.username).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(username).toHashCode();
        }
    }
}
