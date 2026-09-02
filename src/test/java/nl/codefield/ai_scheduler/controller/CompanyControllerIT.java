package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles({"test", "ollama"})
@AutoConfigureMockMvc
@Sql(scripts = {
        "/sql/insert-addresses.sql",              // 1. Instantiates ALL addresses safely
        "/sql/insert-customer-dependencies.sql", // 2. Instantiates profiles
        "/sql/insert-customers.sql",             // 3. Instantiates customers
        "/sql/insert-companies.sql"              // 4. Instantiates companies
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CompanyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /company/companies - Should return all companies as DTOs")
    void shouldReturnAllCompanies() throws Exception {
        mockMvc.perform(get("/company/companies")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].kvkNumber").exists())
                .andExpect(jsonPath("$[0].barberType.value").exists())
                .andExpect(jsonPath("$[0].barberType.name").exists());
    }

    @Test
    @DisplayName("GET /company/type/SHOP - Should filter and return only SHOP companies")
    void shouldReturnOnlyShopCompanies() throws Exception {
        mockMvc.perform(get("/company/type/SHOP")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].barberType.value", everyItem(is("SHOP"))))
                .andExpect(jsonPath("$[*].barberType.name", everyItem(is("In-Shop Barber"))));
    }

    @Test
    @DisplayName("GET /company/type/house_call - Should work seamlessly with lowercase path variables")
    void shouldReturnHouseCallCompaniesWithLowercasePath() throws Exception {
        mockMvc.perform(get("/company/type/house_call")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].barberType.value", everyItem(is("HOUSE_CALL"))))
                .andExpect(jsonPath("$[*].barberType.name", everyItem(is("House Call Barber"))));
    }

    @Test
    @DisplayName("GET /company/type/INVALID_TYPE - Should return 400 Bad Request for unknown types")
    void shouldReturnBadRequestWhenTypeIsUnknown() throws Exception {
        mockMvc.perform(get("/company/type/INVALID_TYPE")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /company/find - Should return sorted SHOP companies within 10km for Sven")
    void shouldReturnCompanies_WhenCustomerEmailIsValid() throws Exception {
        // Arrange
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail("sven@heemskerk.nl");

        // Act & Assert
        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // 15 out of 20 local shops match Sven's profile type ('SHOP')
                .andExpect(jsonPath("$.length()").value(15))
                // Closest shop (Sultan Kapsalon at 0.0 km) must be first
                .andExpect(jsonPath("$[0].name").value("Sultan Kapsalon"))
                .andExpect(jsonPath("$[0].address.city").value("Heemskerk"))
                // Second closest shop (Barber Bekker at 0.16 km) must be second
                .andExpect(jsonPath("$[1].name").value("Barber Bekker"))
                // The furthest local SHOP (index 14) will be at the end of the filtered list
                .andExpect(jsonPath("$[14].address.city").value("Castricum"));
    }

    @Test
    @DisplayName("POST /company/find - Should exclude distant cities and mismatching barber types")
    void shouldExcludeCompaniesOutside10Km_WhenCustomerInHeemskerk() throws Exception {
        // Arrange: Sven is in Heemskerk. Haarlem/Alkmaar shops are > 10km away and excluded.
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail("sven@heemskerk.nl");

        // Act & Assert
        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Total rows in DB is 23, but only 15 match both the 10km radius AND 'SHOP' type!
                .andExpect(jsonPath("$.length()").value(15));
    }


    @Test
    void shouldOnlyReturnAlkmaarCompany_WhenCustomerInAlkmaar() throws Exception {
        // Arrange: Daan lives in Alkmaar. The 20 cluster shops are ~20km+ away and must be hidden.
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail("daan@alkmaar.nl");

        // Act & Assert
        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Only 'Cheese City Cuts Alkmaar' is within 10km of Daan
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cheese City Cuts Alkmaar"))
                .andExpect(jsonPath("$[0].address.city").value("Alkmaar"));
    }


    @Test
    void shouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail("");

        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /company/find - Should return HOUSE_CALL companies matching their work radius for Bram")
    void shouldReturnHouseCallCompanies_WhenCustomerHasHouseCallProfile() throws Exception {
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail("bram@castricum.nl");

        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Only 3 out of 5 HOUSE_CALL companies cover Castricum now!
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Your Rockabilly Barber"))
                .andExpect(jsonPath("$[0].address.city").value("Castricum"));
    }

    @Test
    void shouldReturnBadRequest_WhenEmailIsNull() throws Exception {
        CustomerDTO requestDto = new CustomerDTO();
        requestDto.setEmail(null);

        mockMvc.perform(post("/company/find")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
