package nl.tss.ai_scheduler.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
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

    @Tool(name = "getEvents", description = "Get a list of existing events from Google calendar for a specific timeframe to check availability.")
    public List<Event> getEvents(
            @ToolParam(description = "The lower date window limit.") LocalDateTime start,
            @ToolParam(description = "The upper data window limit.") LocalDateTime end) throws IOException {

        log.info("Getting events from Google calendar for start text {} end text {}", start, end);

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

    @Tool(name = "addEvent", description = "Add and book a brand new appointment event to Google calendar after confirming no conflicts exist.")
    public void addEvent(
            @ToolParam(description = "The main title of the appointment.") String summary,
            @ToolParam(description = "The brief context detailing the booking.") String description,
            @ToolParam(description = "The exact start moment text structure.") LocalDateTime start,
            @ToolParam(description = "The exact end moment text structure.") LocalDateTime end) throws IOException {

        log.info("Adding event via AI parameters: summary [{}], start [{}], end [{}]", summary, start, end);
        Event event = new Event().setSummary(summary).setDescription(description);

        event.setStart(getEventDateTime(start));
        event.setEnd(getEventDateTime(end));

        //EventAttendee attendee = new EventAttendee().setEmail(email).setDisplayName(customer.getName());
        //event.setAttendees(Collections.singletonList(attendee));

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
