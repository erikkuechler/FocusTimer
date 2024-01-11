package htwberlin.focustimer.controller;

import java.util.Optional;
import java.util.Arrays;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.ProductRepository;
import htwberlin.focustimer.repository.UserAccountRepository;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @WithMockUser("test1@example.com")
    @DisplayName("Get User should return Account details when authenticated")
    void testAccountDetailsGetRoute() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setId(1L);
        mockUserAccount.setEmail("test1@example.com");
        mockUserAccount.setUserName("Test User");
        mockUserAccount.setCoins(5);

        Product mockProduct1 = new Product("Test1", 10, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        mockUserAccount.setPurchasedProducts(Arrays.asList(mockProduct1, mockProduct2));
        mockUserAccount.setActiveForeground(mockProduct1);
        mockUserAccount.setActiveBackground(mockProduct2);

        when(userRepository.findByEmail("test1@example.com")).thenReturn(Optional.of(mockUserAccount));

        String expected = "{\"id\":1,\"email\":\"test1@example.com\",\"userName\":\"Test User\",\"coins\":5,\"activeBackground\":{\"id\":null,\"name\":\"Test2\",\"price\":20,\"type\":\"Background\",\"imagePath\":\"bg-blue\"},\"activeForeground\":{\"id\":null,\"name\":\"Test1\",\"price\":10,\"type\":\"Foreground\",\"imagePath\":\"Baum_Default\"}";

        this.mockMvc.perform(get("/me/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(expected)));
    }

    @Test
    @WithMockUser("test2@example.com")
    @DisplayName("Earn coin endpoint should return success message")
    void testEarnCoinPostRoute() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("test2@example.com");
        mockUserAccount.setLastEarnTime(LocalDateTime.now().minusMinutes(2));
        mockUserAccount.setCoins(0);

        when(userRepository.findByEmail("test2@example.com")).thenReturn(Optional.of(mockUserAccount));

        this.mockMvc.perform(post("/me/earn"))
            .andExpect(status().isOk())
            .andExpect(content().string("Coin earned successfully!"));
    }

    @Test
    @WithMockUser("test3@example.com")
    @DisplayName("Earn coin endpoint should return failure message due to frequent request")
    void testEarnCoinFailure() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("test3@example.com");
        mockUserAccount.setLastEarnTime(LocalDateTime.now().minusSeconds(30));
        mockUserAccount.setCoins(0);

        when(userRepository.findByEmail("test3@example.com")).thenReturn(Optional.of(mockUserAccount));

        this.mockMvc.perform(post("/me/earn"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("You can earn only one coin per minute."));
    }
    
}
