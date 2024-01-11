package htwberlin.focustimer.controller;

import java.util.Optional;
import java.util.Arrays;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import htwberlin.focustimer.entity.Interval;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.ProductRepository;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.service.IntervalService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntervalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IntervalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    IntervalService intervalService;

    @MockBean
    private UserAccountRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @WithMockUser("test1@example.com")
    @DisplayName("Get all intervals should return intervals")
    public void testGetAllIntervalsGetRoute() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("test1@example.com");
        
        Interval interval1 = new Interval("10 Minuten", LocalDate.of(2023, 12, 24), 10);
        Interval interval2 = new Interval("30 Sekunden", LocalDate.of(2023, 6, 13), 1);
        mockUserAccount.setIntervals(Arrays.asList(interval1, interval2));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));

        String expected = "{\"id\":null,\"duration\":\"10 Minuten\",\"date\":\"2023-12-24\",\"coinValue\":10},{\"id\":null,\"duration\":\"30 Sekunden\",\"date\":\"2023-06-13\",\"coinValue\":1}";
        
        this.mockMvc.perform(get("/intervals/all"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(expected)));
    }

    @Test
    @WithMockUser("test2@example.com")
    @DisplayName("Add interval should return success message")
    public void testAddIntervalPostRoute() throws Exception {
        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("test2@example.com");

        Interval interval = new Interval();
        interval.setUserAccount(mockUserAccount);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUserAccount));

        this.mockMvc.perform(post("/intervals/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(interval)))
            .andExpect(status().isOk())
            .andExpect(content().string("Interval created for user: test2@example.com"));
    }
    
}
