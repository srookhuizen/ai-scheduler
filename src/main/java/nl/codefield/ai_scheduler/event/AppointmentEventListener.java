package nl.codefield.ai_scheduler.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.codefield.ai_scheduler.model.Appointment;
import nl.codefield.ai_scheduler.service.CalendarService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final CalendarService calendarService;

    @Async
    @EventListener
    public void handleAppointmentSaved(AppointmentSavedEvent event) {
        Appointment appointment = event.getAppointment();
        log.info("Received AppointmentSavedEvent. Synchronizing with Google Calendar for customer: {}",
                appointment.getCustomer().getEmail());

        String summary = appointment.getSummary();
        String description = appointment.getSummary();

        try {
            calendarService.addEvent(
                    summary,
                    description,
                    appointment.getAppointmentStart(),
                    appointment.getAppointmentEnd()
            );
            log.info("Google Calendar sync successful for appointment ID: {}", appointment.getId());
        } catch (IOException e) {
            log.error("CRITICAL: Failed to sync appointment to Google Calendar for ID: {}", appointment.getId(), e);
        }
    }
}
