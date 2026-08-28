package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({"test", "ollama"})
class CustomerControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void save_ShouldReturnSavedCustomer_WhenValidRequest() throws Exception {
        CustomerDTO inputDto = CustomerDTO.builder().name("Jan Janssen").email("jan.janssen@email.nl")
                .imageUrl("http://my.image.png").build();

        mockMvc.perform(post("/customer/save")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jan Janssen"))
                .andExpect(jsonPath("$.email").value("jan.janssen@email.nl"))
                .andExpect(jsonPath("$.imageUrl").value("http://my.image.png"))
                .andExpect(jsonPath("$.onboarded").value("false"));

    }

    @Test
    void update_ShouldReturnSavedCustomer_WhenValidRequest() throws Exception {
        CustomerDTO inputDto = CustomerDTO.builder().name("Jan Janssen").email("jan.janssen@email.nl")
                .gender("Male").build();

        MvcResult mvcResult = mockMvc.perform(post("/customer/update")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        CustomerDTO savedCustomer = objectMapper.readValue(body, CustomerDTO.class);

        //update the received customer
        AddressDTO address = AddressDTO.builder().street("Kerkstraat").number("13 B").postalCode("1111 AA")
                .city("Heemskerk").build();
        savedCustomer.setPhoneNumber("0612345678");
        savedCustomer.setAddress(address);

        mockMvc.perform(post("/customer/update")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("0612345678"))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.address.street").value("Kerkstraat"))
                .andExpect(jsonPath("$.address.number").value("13 B"))
                .andExpect(jsonPath("$.address.postalCode").value("1111 AA"))
                .andExpect(jsonPath("$.address.city").value("Heemskerk"))
                .andExpect(jsonPath("$.onboarded").value("true"));

    }
}
