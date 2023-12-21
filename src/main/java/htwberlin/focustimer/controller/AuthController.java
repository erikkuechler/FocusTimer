package htwberlin.focustimer.controller;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.ProductRepository;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.request.AuthRequest;
import htwberlin.focustimer.request.UpdateRequest;
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
    private ProductRepository productRepository;

    /**
     * Constructs an AuthController with the necessary dependencies.
     *
     * @param userRepository The repository for accessing user account information.
     * @param passwordEncoder The password encoder for securing user passwords.
     * @param authenticationManager The authentication manager for user login.
     * @param jwtTokenProvider The JWT token provider for token generation and validation.
     * @param productRepository
     */
    public AuthController(UserAccountRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.productRepository = productRepository;
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

        if (authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (authRequest.getUserName() == null || authRequest.getUserName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserAccount user = new UserAccount();
        user.setUserName(authRequest.getUserName());
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        // Default Products
        Product defaultForeground = productRepository.findById(1L).orElse(null);
        Product defaultBackground = productRepository.findById(4L).orElse(null);
        List<Product> purchasedProducts = new ArrayList<>();
        purchasedProducts.add(defaultForeground);
        purchasedProducts.add(defaultBackground);
        user.setPurchasedProducts(purchasedProducts);
        user.setActiveForeground(defaultForeground);
        user.setActiveBackground(defaultBackground);

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

    /**
     * Updates user account information based on the provided request.
     *
     * Handles password, email, and username updates. Deletes the account if requested.
     *
     * @param updateRequest The request object containing updated user information.
     * @return ResponseEntity shows whether the update operation was successful or not.
     */
    @PostMapping(value = "/update")
    public ResponseEntity<String> updateAccount(@RequestBody UpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (!optionalUserAccount.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        UserAccount user = optionalUserAccount.get();

        // Passwort
        if (!passwordEncoder.matches(updateRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong password.");
        }

        // new Passwort
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }

        // E-Mail
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            user.setEmail(updateRequest.getEmail());
        }

        // Username
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isEmpty()) {
            user.setUserName(updateRequest.getUsername());
        }

        // delete = true
        if (updateRequest.isDelete()) {
            userRepository.delete(user);
            return ResponseEntity.ok("User account has been deleted!");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User account has been updated!");
    }

}
