package htwberlin.focustimer.service;

import java.util.Collections;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.UserAccountRepository;

/**
 * CustomUserDetailsService is a service that implements the UserDetailsService 
 * interface to provide user authentication and authorization based on user details stored in a repository.
 * 
 * Inspired by: https://www.youtube.com/watch?v=mn5UZYtPLjg
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserAccountRepository userAccountRepository;

    /**
     * Constructs a CustomUserDetailsService with the provided UserAccountRepository.
     *
     * @param userAccountRepository The repository for accessing user account information.
     */
    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Loads user details by their username (email) from the repository and constructs a UserDetails object for authentication and authorization.
     *
     * @param username The username (email) of the user to load.
     * @return UserDetails object for the user.
     * @throws UsernameNotFoundException If the user is not found in the repository.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer wurde nicht gefunden."));

        return new org.springframework.security.core.userdetails.User(
                userAccount.getEmail(),
                userAccount.getPassword(),
                Collections.emptyList()
        );
    }
}