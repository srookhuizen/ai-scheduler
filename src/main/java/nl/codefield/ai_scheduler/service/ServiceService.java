package nl.codefield.ai_scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.entity.Service;
import nl.codefield.ai_scheduler.repository.ServiceRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public static List<Service> services() {
        return Arrays.asList(
                new Service(null, "Haircut", "Standard haircut finished with a hot towel wipe", 30.00, 30),
                new Service(null, "Eyebrows", "Eyebrow shaping and trimming using thread or wax", 12.00, 15),
                new Service(null, "Colouring", "Full hair dye or gray coverage tailored to your style", 45.00, 60),
                new Service(null, "Shaving", "Traditional straight razor hot towel shave", 25.00, 25)
        );
    }

    @Tool(name = "getAvailableServices", description = "Get the complete catalog of offered barber services, including their exact names, descriptions, prices, and durations.")
    public List<Service> getAvailableServices(String service) {
        log.info("LLM tool execution: getAvailableServices called by AI: {}", service);
        return services();
    }


    public Service findByName(String name) {
        if (!StringUtils.hasText(name)) {
            return Service.createDefault();
        }

        log.info("Searching database/catalog for service name: [{}]", name);

        Service service = services().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("Service [{}] not found! Falling back to Unknown default.", name);
                    return Service.createDefault();
                });
        return serviceRepository.save(service);
    }
}
