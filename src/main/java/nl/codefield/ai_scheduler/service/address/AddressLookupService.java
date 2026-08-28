package nl.codefield.ai_scheduler.service.address;

import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.exception.AddressNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class AddressLookupService {

    private final RestClient restClient;

    public AddressLookupService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://api.pdok.nl").build();
    }

    public PdokSearchResponse.PdokAddressDoc findAndBuildAddress(String rawPostcode, String houseNumber) {
        String cleanPostcode = rawPostcode.replace(" ", "").toUpperCase();

        PdokSearchResponse pdokResponse = this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/bzk/locatieserver/search/v3_1/free")
                        .queryParam("q", "*")
                        .queryParam("fq", "postcode:" + cleanPostcode)
                        .queryParam("fq", "huisnummer:" + houseNumber)
                        .queryParam("fq", "type:adres")
                        .queryParam("rows", 1)
                        .queryParam("fl", "straatnaam,huisnummer,postcode,woonplaatsnaam,centroide_ll")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PdokSearchResponse.class);

        if (pdokResponse == null || pdokResponse.getResponse() == null) {
            log.warn("Could not get PDOK response. pdokResponse={}", pdokResponse);
            throw new AddressNotFoundException("No verified address entry matches this search query.");
        }

        var docs = pdokResponse.getResponse().getDocs();
        if (docs == null || docs.isEmpty()) {
            log.warn("Could not get PDOK response. docs is empty.");
            throw new AddressNotFoundException("No verified address entry matches this search query.");
        }
        return docs.getFirst();
    }
}
