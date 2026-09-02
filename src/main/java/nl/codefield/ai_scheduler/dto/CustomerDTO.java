package nl.codefield.ai_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String email;
    private String imageUrl;
    private String registrationType;
    private AddressDTO address;
    private ProfileDTO profile;

    @JsonProperty("onboarded")
    public boolean onboarded() {
        return Objects.nonNull(address) && address.isOnboarded();
    }
}

