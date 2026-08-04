package nl.codefield.ai_scheduler.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer duration;

    public static Service createDefault() {
        return Service.builder()
                .name("Unknown")
                .duration(60)
                .build();
    }
}
