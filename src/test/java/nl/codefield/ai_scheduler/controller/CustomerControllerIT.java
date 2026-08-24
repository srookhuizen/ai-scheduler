package nl.codefield.ai_scheduler.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.controller.CustomerController;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.model.Customer;
import nl.codefield.ai_scheduler.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void save_ShouldReturnSavedCustomer_WhenValidRequest() throws Exception {
        CustomerDTO inputDto = CustomerDTO.builder().name("Jan Janssen").email("jan.janssen@email.nl")
                .gender("Male").phoneNumber("0612345678").build();

        mockMvc.perform(post("/customer/save")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Jan Janssen"))
                .andExpect(jsonPath("$.email").value("jan.janssen@email.nl"))
                .andExpect(jsonPath("$.phoneNumber").value("0612345678"));
    }
}
