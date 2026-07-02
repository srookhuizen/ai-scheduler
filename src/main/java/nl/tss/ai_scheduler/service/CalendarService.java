package nl.tss.ai_scheduler.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Scheduler API";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String calendarId = "primary";

    @Value("${google.calendar.api.credentials}")
    private String googleCredentialsJson;

    private static DateTime getDateTime(LocalDateTime localDateTime) {
        return new DateTime(
                localDateTime.atZone(ZoneId.of("Europe/Amsterdam")).toInstant().toEpochMilli());
    }

    @Tool(name = "getEvents", description = "Get the events from Google calendar for a given start and end date")
    public List<Event> getEvents(LocalDateTime start, LocalDateTime end) throws IOException {
        log.info("Getting events from Google calendar for start {} end {}", start, end);
        List<Event> events = getCalendar().events().list(calendarId).setMaxResults(100)
                .setTimeMin(getDateTime(start))
                .setTimeMax(getDateTime(end)).setOrderBy("startTime").setSingleEvents(true).execute()
                .getItems();

        log.info("Found events [{}], from [{}], till [{}]", events.size(), start, end);
        return events;
    }

    private Calendar getCalendar() throws IOException {

        final HttpTransport httpTransport = new NetHttpTransport();

        // 2. Convert the raw JSON string into a memory-based InputStream
        try (InputStream credentialsStream = new ByteArrayInputStream(
                googleCredentialsJson.getBytes(StandardCharsets.UTF_8))) {

            // 3. Load the credentials directly from memory
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(credentialsStream)
                    .createScoped(SCOPES);

            return new Calendar.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
}
