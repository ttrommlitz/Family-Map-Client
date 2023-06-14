package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import model.Event;

public class ChronologicalSortTest {
    private static DataCache dataCache;

    @BeforeAll
    public static void setUp() {
        dataCache = DataCache.getInstance();
    }

    @Test
    public void sortEventsPass() {
        Event birth = new Event("eventId1", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "birth", 1970);

        Event graduation = new Event("eventId2", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "graduation", 1988);

        Event marriage = new Event("eventId3", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "marriage", 1995);

        Event death = new Event("eventId4", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "death", 2020);

        ArrayList<Event> unsortedEvents = new ArrayList<>();
        unsortedEvents.add(death);
        unsortedEvents.add(marriage);
        unsortedEvents.add(graduation);
        unsortedEvents.add(birth);
        dataCache.sortEvents(unsortedEvents);

        assertEquals("birth", unsortedEvents.get(0).getEventType());
        assertEquals("graduation", unsortedEvents.get(1).getEventType());
        assertEquals("marriage", unsortedEvents.get(2).getEventType());
        assertEquals("death", unsortedEvents.get(unsortedEvents.size() - 1).getEventType());
    }

    @Test
    public void sortEventsPassTwo() {
        ArrayList<Event> unsortedEvents = new ArrayList<>();
        assertDoesNotThrow(() -> dataCache.sortEvents(unsortedEvents));

        Event firstAlphabetically = new Event("eventId5", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "first", 2000);

        Event secondAlphabetically = new Event("eventId6", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "second", 2000);

        unsortedEvents.add(secondAlphabetically);
        unsortedEvents.add(firstAlphabetically);
        dataCache.sortEvents(unsortedEvents);

        assertEquals("first", unsortedEvents.get(0).getEventType());
        assertEquals("second", unsortedEvents.get(unsortedEvents.size() - 1).getEventType());
    }
}
