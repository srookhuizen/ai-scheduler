package nl.codefield.ai_scheduler.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Scheduler API";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    // Injecteer de ServiceService om de duur dynamisch in Java te kunnen berekenen
    private final ServiceService serviceService;

    @Value("${google.calendar.api.credentials}")
    private String googleCredentialsJson;
    @Value("${google.calendar.api.calendar.id}")
    private String calendarId;

    private static DateTime getDateTime(LocalDateTime localDateTime) {
        return new DateTime(
                localDateTime.atZone(ZoneId.of("Europe/Amsterdam")).toInstant().toEpochMilli());
    }

    private EventDateTime getEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime().setDateTime(getDateTime(localDateTime)).setTimeZone("Europe/Amsterdam");
    }

    @Tool(name = "getEvents", description = "Check availability in Google Calendar. Provide the requested service and the desired start time.")
    public List<Event> getEvents(
            @ToolParam(description = "The specific name of the service requested by the user.") String service,
            @ToolParam(description = "The exact start date and time string in ISO-8601 format (YYYY-MM-DDTHH:mm:ss).") LocalDateTime start) throws IOException {

        Integer duration = serviceService.findByName(service).getDuration();
        LocalDateTime end = start.plusMinutes(duration);

        log.info("Getting events from Google calendar for service [{}] from start {} to calculated end {}", service, start, end);

        List<Event> events = getCalendar().events().list(calendarId).setMaxResults(100)
                .setTimeMin(getDateTime(start))
                .setTimeMax(getDateTime(end)).setOrderBy("startTime").setSingleEvents(true).execute()
                .getItems();

        log.info("Found events [{}], from [{}], till [{}]", events.size(), start, end);
        return events;
    }

    @Tool(name = "deleteEvent", description = "Delete a specific event from Google calendar using its unique alphanumeric event identifier string.")
    public void deleteEvent(
            @ToolParam(description = "The unique alphanumeric ID string of the Google Calendar event to delete.") String eventId) throws IOException {
        log.info("Deleting event with ID: [{}]", eventId);
        getCalendar().events().delete(calendarId, eventId).execute();
    }

    public void addEvent(String summary, String description, LocalDateTime start, LocalDateTime end) throws IOException {
        log.info("Adding event via Java calculated parameters: summary [{}], start [{}], calculated end [{}]", summary, start, end);
        Event event = new Event().setSummary(summary).setDescription(description);

        event.setStart(getEventDateTime(start));
        event.setEnd(getEventDateTime(end));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(60 * 24 * 7)
        };
        Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        event = getCalendar().events().insert(calendarId, event).execute();
        log.info("Event successfully registered layout: summary [{}] date [{}]", event.getSummary(), event.getStart());
    }

    private Calendar getCalendar() throws IOException {
        final HttpTransport httpTransport = new NetHttpTransport();

        try (InputStream credentialsStream = new ByteArrayInputStream(
                googleCredentialsJson.getBytes(StandardCharsets.UTF_8))) {

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(credentialsStream)
                    .createScoped(SCOPES);

            return new Calendar.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .setCalendarRequestInitializer(new CalendarRequestInitializer())
                    .build();
        }
    }
}
