package nl.codefield.ai_scheduler.service;

import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Ignore
public class CalendarServiceIT {

  @Autowired
  private CalendarService calendarService;

  @Test
  void freeBusy() throws IOException {
    List<Event> events = calendarService.getEvents(LocalDateTime.now(),
        LocalDateTime.now().plusHours(5));
    events.forEach(System.out::println);
  }
}
