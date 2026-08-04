package nl.codefield.ai_scheduler.event;

import lombok.Getter;
import nl.codefield.ai_scheduler.model.Appointment;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentSavedEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentSavedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }
}
