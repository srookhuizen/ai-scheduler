package nl.codefield.ai_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDTO {
    private String name;
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private String kvkNumber;
    private Integer workRadius;
}
