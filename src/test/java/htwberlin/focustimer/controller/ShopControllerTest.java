package htwberlin.focustimer.controller;

import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;
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
import htwberlin.focustimer.service.ProductService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserAccountRepository userRepository;

    @Test
    @WithMockUser("test1@example.com")
    @DisplayName("Get User Products should return user-specific product details")
    public void testProductsGetRoute() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        Product mockProduct1 = new Product("Test1", 10, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        mockUserAccount.setPurchasedProducts(Arrays.asList(mockProduct1));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.getAll()).thenReturn(Arrays.asList(mockProduct1, mockProduct2));

        String expected = "{\"product\":{\"id\":null,\"name\":\"Test1\",\"price\":10,\"type\":\"Foreground\",\"imagePath\":\"Baum_Default\"},\"purchased\":true,\"active\":false},{\"product\":{\"id\":null,\"name\":\"Test2\",\"price\":20,\"type\":\"Background\",\"imagePath\":\"bg-blue\"},\"purchased\":false,\"active\":false}";

        this.mockMvc.perform(get("/shop/products"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(expected)));
    }

    @Test
    @WithMockUser("test2@example.com")
    @DisplayName("Buy Product should successfully purchase a product")
    public void testBuyProductPostRoute() throws Exception {
        Product mockProduct1 = new Product("Test1", 30, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setPurchasedProducts(new ArrayList<>(Arrays.asList(mockProduct2)));
        mockUserAccount.setCoins(50);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.get(anyLong())).thenReturn(mockProduct1);

        this.mockMvc.perform(post("/shop/buy/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Product purchased successfully!"));
    }

    @Test
    @WithMockUser("test3@example.com")
    @DisplayName("Buy Product should return error for insufficient coins")
    public void testBuyProductInsufficientCoins() throws Exception {
        Product mockProduct1 = new Product("Test1", 30, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setPurchasedProducts(new ArrayList<>(Arrays.asList(mockProduct2)));
        mockUserAccount.setCoins(20);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.get(anyLong())).thenReturn(mockProduct1);

        this.mockMvc.perform(post("/shop/buy/1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Insufficient coins to buy this product."));
    }

    @Test
    @WithMockUser("test4@example.com")
    @DisplayName("Buy Product should return error if product is already purchased")
    public void testBuyProductAlreadyPurchased() throws Exception {
        Product mockProduct1 = new Product("Test1", 30, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setPurchasedProducts(new ArrayList<>(Arrays.asList(mockProduct1, mockProduct2)));
        mockUserAccount.setCoins(20);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.get(anyLong())).thenReturn(mockProduct2);

        this.mockMvc.perform(post("/shop/buy/1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Product already purchased."));
    }

    @Test
    @WithMockUser("test5@example.com")
    @DisplayName("Activate Product should successfully activate a product")
    public void testActivateProductPostRoute() throws Exception {
        Product mockProduct = new Product("TestProduct", 30, "Foreground", "Baum_Default");
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setPurchasedProducts(Arrays.asList(mockProduct));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.get(anyLong())).thenReturn(mockProduct);

        this.mockMvc.perform(post("/shop/activate/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Product activated successfully!"));
    }

    @Test
    @WithMockUser("test6@example.com")
    @DisplayName("Activate Product should return error when trying to activate a product not purchased")
    public void testActivateProductNotPurchased() throws Exception {
        Product mockProduct1 = new Product("Test1", 30, "Foreground", "Baum_Default");
        Product mockProduct2 = new Product("Test2", 20, "Background", "bg-blue");
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setPurchasedProducts(Arrays.asList(mockProduct2));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));
        when(productService.get(anyLong())).thenReturn(mockProduct1);

        this.mockMvc.perform(post("/shop/activate/1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Product needs to be purchased before activation."));
    }
    
}
