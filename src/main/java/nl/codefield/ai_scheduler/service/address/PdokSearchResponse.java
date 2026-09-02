package nl.codefield.ai_scheduler.service.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PdokSearchResponse {
    private ResponseData response;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        private List<PdokAddressDoc> docs;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PdokAddressDoc {
        @JsonProperty("straatnaam")
        private String street;

        @JsonProperty("huisnummer")
        private String number;

        @JsonProperty("postcode")
        private String postalCode;

        @JsonProperty("woonplaatsnaam")
        private String city;

        private double latitude;
        private double longitude;

        /**
         * Custom Jackson setter that intercepts the "POINT(lon lat)" string
         * and parses it directly into separate double values.
         */
        @JsonProperty("centroide_ll")
        public void setCentroideLl(String centroideLl) {
            if (centroideLl != null && centroideLl.startsWith("POINT(") && centroideLl.endsWith(")")) {
                try {
                    // Extract the values between "POINT(" and ")"
                    String cleanCoordinates = centroideLl.substring(6, centroideLl.length() - 1);
                    String[] parts = cleanCoordinates.split(" ");

                    if (parts.length == 2) {
                        // CRITICAL: PDOK puts Longitude (X) first, then Latitude (Y)
                        this.longitude = Double.parseDouble(parts[0]);
                        this.latitude = Double.parseDouble(parts[1]);
                    }
                } catch (NumberFormatException e) {
                    // Fallback or log if coordinates are malformed
                    this.longitude = 0.0;
                    this.latitude = 0.0;
                }
            }
        }
    }
}
