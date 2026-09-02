package nl.codefield.ai_scheduler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "appointment_start", nullable = false)
    private LocalDateTime appointmentStart;

    @Column(name = "appointment_end", nullable = false)
    private LocalDateTime appointmentEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public String getSummary() {
        return (service != null ? service.getName() : "Unknown Service") + ": " +
                (customer != null ? customer.getFirstName() : "Unknown Customer");
    }
}
