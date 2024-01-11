package htwberlin.focustimer.controller;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.ProductRepository;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.request.AuthRequest;
import htwberlin.focustimer.request.UpdateRequest;
import htwberlin.focustimer.service.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserAccountRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @DisplayName("Registration endpoint should return valid user data")
    public void testRegistrationPostRoute() throws Exception {
        AuthRequest mockAuthRequest = new AuthRequest("test@example.com", "password123");
        mockAuthRequest.setUserName("Test");

        UserAccount savedUser = new UserAccount();
        savedUser.setId(1L);
        savedUser.setEmail(mockAuthRequest.getEmail());
        savedUser.setUserName(mockAuthRequest.getUserName());

        when(userRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        this.mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(mockAuthRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value(mockAuthRequest.getEmail()))
            .andExpect(jsonPath("$.userName").value(mockAuthRequest.getUserName()));
    }

    @Test
    @DisplayName("Registration endpoint should return bad request when email is already in use")
    public void testRegistrationPostRouteEmailAlreadyExists() throws Exception {
        AuthRequest mockAuthRequest = new AuthRequest("existing@example.com", "password123");
        mockAuthRequest.setUserName("Test");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new UserAccount()));

        this.mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(mockAuthRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Registration endpoint should return bad request when username or password is empty")
    public void testRegistrationPostRouteEmptyUserNameOrPassword() throws Exception {
        AuthRequest mockAuthRequest = new AuthRequest("test@example.com", "");
        mockAuthRequest.setUserName("");

        this.mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(mockAuthRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login endpoint should return JWT token for valid credentials")
    public void testLoginPostRoute() throws Exception {
        AuthRequest mockAuthRequest = new AuthRequest("test@example.com", "password123");
        mockAuthRequest.setUserName("Test");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(mockAuthRequest.getEmail());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mockedJWTToken");

        this.mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mockAuthRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("mockedJWTToken"));
    }

    @Test
    @DisplayName("Update endpoint should return success message for valid data")
    @WithMockUser("test@example.com")
    public void testUpdateAccountWithValidData() throws Exception {
        UserAccount existingUser = new UserAccount();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(passwordEncoder.encode("currentPassword"));
    
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", existingUser.getPassword())).thenReturn(true);
    
        UpdateRequest updateRequest = new UpdateRequest("newemail@example.com", "NewUsername", "currentPassword", "newPassword", false);

        this.mockMvc.perform(post("/auth/update")
            .content(objectMapper.writeValueAsString(updateRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("User account has been updated!"));    
    }

    @Test
    @DisplayName("Update endpoint should return error message for invalid password")
    @WithMockUser("test@example.com")
    public void testUpdateAccountWithInvalidPassword() throws Exception {
        UserAccount existingUser = new UserAccount();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(passwordEncoder.encode("currentPassword"));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPassword", existingUser.getPassword())).thenReturn(false);

        UpdateRequest updateRequest = new UpdateRequest("newemail@example.com", "NewUsername", "currentPassword", "newPassword", false);

        this.mockMvc.perform(post("/auth/update")
            .content(objectMapper.writeValueAsString(updateRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Wrong password."));    
    }
    
}
