package htwberlin.focustimer.controller;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.request.AuthRequest;
import htwberlin.focustimer.service.JwtTokenProvider;

/**
 * AuthController is a RESTful controller responsible for handling user authentication and registration.
 * Inspired by: https://www.youtube.com/watch?v=mn5UZYtPLjg
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserAccountRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs an AuthController with the necessary dependencies.
     *
     * @param userRepository The repository for accessing user account information.
     * @param passwordEncoder The password encoder for securing user passwords.
     * @param authenticationManager The authentication manager for user login.
     * @param jwtTokenProvider The JWT token provider for token generation and validation.
     */
    public AuthController(UserAccountRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user account based on the provided authentication request.
     *
     * @param authRequest The authentication request containing user email and password.
     * @return ResponseEntity containing the created user account or a bad request response if the email is already in use.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<UserAccount> register(@RequestBody AuthRequest authRequest) {
        Optional<UserAccount> userOptional = userRepository.findByEmail(authRequest.getEmail());

        if (userOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        UserAccount user = new UserAccount();
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        UserAccount created = userRepository.save(user);

        return ResponseEntity.ok(created);
    }

    /**
     * Authenticates a user based on the provided authentication request and issues a JWT token upon successful login.
     *
     * @param authRequest The authentication request containing user email and password.
     * @return ResponseEntity containing the JWT token or an unauthorized response if login fails.
     */
    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        return ResponseEntity.ok(jwtTokenProvider.generateToken(authentication));
    }
    
}
