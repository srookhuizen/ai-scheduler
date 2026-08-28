package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.exception.AddressNotFoundException;
import nl.codefield.ai_scheduler.service.address.AddressLookupService;
import nl.codefield.ai_scheduler.service.address.PdokSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles({"test", "ollama"})
public class AddressControllerIT {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AddressLookupService addressLookupService;

    @Test
    void getAddress_ShouldReturnAddress_WhenFound() throws Exception {
        FindAddressRequest request = new FindAddressRequest("1234AB", "12");
        PdokSearchResponse.PdokAddressDoc mockDoc = new PdokSearchResponse.PdokAddressDoc();
        mockDoc.setStreet("Dorpstraat");
        mockDoc.setNumber("12");
        mockDoc.setPostalCode("1234AB");
        mockDoc.setCity("Amsterdam");

        when(addressLookupService.findAndBuildAddress("1234AB", "12"))
                .thenReturn(mockDoc);

        mockMvc.perform(post("/address/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode").value("1234AB"))
                .andExpect(jsonPath("$.number").value("12"))
                .andExpect(jsonPath("$.street").value("Dorpstraat"))
                .andExpect(jsonPath("$.city").value("Amsterdam"))
                .andExpect(jsonPath("$.onboarded").value("true"));
    }

    @Test
    void getAddress_ShouldReturn404_WhenNotFound() throws Exception {
        FindAddressRequest request = new FindAddressRequest("9999ZZ", "99");

        when(addressLookupService.findAndBuildAddress("9999ZZ", "9999ZZ"))
                .thenThrow(new AddressNotFoundException("No verified address entry matches this search query."));

        mockMvc.perform(post("/address/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
