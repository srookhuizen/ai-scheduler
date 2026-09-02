package nl.codefield.ai_scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codefield.ai_scheduler.dto.AddressDTO;
import nl.codefield.ai_scheduler.dto.BarberTypeDTO;
import nl.codefield.ai_scheduler.dto.CompanyDTO;
import nl.codefield.ai_scheduler.dto.CustomerDTO;
import nl.codefield.ai_scheduler.dto.ProfileDTO;
import nl.codefield.ai_scheduler.mapper.CompanyMapper;
import nl.codefield.ai_scheduler.model.Address;
import nl.codefield.ai_scheduler.model.BarberType;
import nl.codefield.ai_scheduler.model.Company;
import nl.codefield.ai_scheduler.repository.CompanyRepository;
import nl.codefield.ai_scheduler.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"test", "ollama"})
@AutoConfigureMockMvc
class CustomerControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyMapper companyMapper;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /customer/register - Should create a baseline unregistered customer record")
    void save_ShouldReturnSavedCustomer_WhenValidRequest() throws Exception {
        CustomerDTO inputDto = CustomerDTO.builder()
                .firstName("Jan")
                .lastName("Janssen")
                .email("jan.janssen@email.nl")
                .registrationType("GOOGLE")
                .imageUrl("http://my.image.png")
                .build();

        mockMvc.perform(post("/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Janssen"))
                .andExpect(jsonPath("$.email").value("jan.janssen@email.nl"))
                .andExpect(jsonPath("$.registrationType").value("GOOGLE"))
                .andExpect(jsonPath("$.imageUrl").value("http://my.image.png"))
                .andExpect(jsonPath("$.onboarded").value(false));
    }

    @Test
    @DisplayName("POST /customer/update - Step-by-step onboarding flow validation")
    void update_ShouldReturnSavedCustomer_WhenValidRequest() throws Exception {
        CustomerDTO inputDto = CustomerDTO.builder()
                .firstName("Jan")
                .lastName("Janssen")
                .email("jan.janssen@email.nl")
                .registrationType("GOOGLE")
                .gender("Male")
                .build();

        MvcResult registerResult = mockMvc.perform(post("/customer/register")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andReturn();

        String body = registerResult.getResponse().getContentAsString();
        CustomerDTO savedCustomer = objectMapper.readValue(body, CustomerDTO.class);

        // --- STEP 1: Update customer address properties
        AddressDTO address = AddressDTO.builder()
                .street("Kerkstraat")
                .number("13 B")
                .postalCode("1111 AA")
                .city("Heemskerk")
                .build();
        savedCustomer.setPhoneNumber("0612345678");
        savedCustomer.setAddress(address);

        MvcResult addressUpdateResult = mockMvc.perform(post("/customer/update")
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
                .andExpect(jsonPath("$.onboarded").value(true))
                .andReturn();

        // --- STEP 2: Update user property selected barber type
        body = addressUpdateResult.getResponse().getContentAsString();
        savedCustomer = objectMapper.readValue(body, CustomerDTO.class);

        BarberTypeDTO barberType = BarberTypeDTO.builder().name("House Call Barber").value("HOUSE_CALL").build();
        ProfileDTO profile = ProfileDTO.builder().barberType(barberType).build();
        savedCustomer.setProfile(profile);

        MvcResult barberTypeUpdateResult = mockMvc.perform(post("/customer/update")
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
                .andExpect(jsonPath("$.onboarded").value(true))
                .andExpect(jsonPath("$.profile.barberType.value").value("HOUSE_CALL"))
                .andExpect(jsonPath("$.profile.barberType.name").value("House Call Barber"))
                .andReturn();

        // --- STEP 2: Update user property selected barber (company)
        Company savedCompany = companyRepository.save(
                Company.builder()
                        .name("JEFF ROOZE Barbers & Academy")
                        .publicId(UUID.randomUUID().toString())
                        .kvkNumber("74839201")
                        .workRadius(15)
                        .barberType(BarberType.SHOP)
                        .address(Address.builder()
                                .street("Jan van Scorelstraat")
                                .number("20")
                                .postalCode("1961 EZ")
                                .city("Heemskerk")
                                .latitude(52.51014)
                                .longitude(4.67389)
                                .build())
                        .build()
        );

        body = barberTypeUpdateResult.getResponse().getContentAsString();
        savedCustomer = objectMapper.readValue(body, CustomerDTO.class);

        CompanyDTO companyDto = companyMapper.map(savedCompany);
        savedCustomer.getProfile().setCompany(companyDto);

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
                .andExpect(jsonPath("$.onboarded").value(true))
                .andExpect(jsonPath("$.profile.barberType.value").value("HOUSE_CALL"))
                .andExpect(jsonPath("$.profile.barberType.name").value("House Call Barber"))
                .andExpect(jsonPath("$.profile.company.id").value(savedCompany.getPublicId()));
    }
}
