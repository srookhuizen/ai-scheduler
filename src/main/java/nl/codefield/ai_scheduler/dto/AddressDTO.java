package nl.codefield.ai_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static org.springframework.util.StringUtils.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private Double latitude;
    private Double longitude;

    public boolean isOnboarded() {
        return hasText(street) && hasText(number) && hasText(postalCode) && hasText(city);
    }
}
