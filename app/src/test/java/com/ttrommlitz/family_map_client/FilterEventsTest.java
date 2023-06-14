package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import model.Event;
import model.Person;

public class FilterEventsTest {
    private static DataCache dataCache;
    private static Filter filter;
    private static Person currPerson;
    private static Person spouse;
    private static Person mother;
    private static Person father;
    private static Person randomMale;
    private static Person randomFemale;
    private static Event birth;
    private static Event naming;
    private static Event marriage;
    private static Event death;

    @BeforeAll
    static void setUp() {
        dataCache = DataCache.getInstance();
        mother = new Person("motherId", "username", "motherName",
                "lastName", "f", null, null, null);
        father = new Person("fatherId", "username", "fatherName",
                "lastName", "m", null, null, null);
        currPerson = new Person("currPersonId", "username", "myName",
                "lastName", "f", father.getPersonID(), mother.getPersonID(), null);
        spouse = new Person("spouseId", "username", "spouse",
                "lastName", "m", null, null, null);
        currPerson.setSpouseID(spouse.getPersonID());
        spouse.setSpouseID(currPerson.getPersonID());
        randomMale = new Person("randomMaleId", "username", "childName",
                "lastName", "m", null, currPerson.getPersonID(), null);
        randomFemale = new Person("randomFemaleId", "username", "random",
                "person", "f", null, null, null);

        HashMap<String, Person> persons = new HashMap<>();
        persons.put(mother.getPersonID(), mother);
        persons.put(father.getPersonID(), father);
        persons.put(currPerson.getPersonID(), currPerson);
        persons.put(spouse.getPersonID(), spouse);
        persons.put(randomMale.getPersonID(), randomMale);
        persons.put(randomFemale.getPersonID(), randomFemale);

        HashSet<String> maternalMale = new HashSet<>();
        maternalMale.add(spouse.getPersonID());
        dataCache.setMaternalMaleAncestors(maternalMale);

        HashSet<String> maternalFemale = new HashSet<>();
        maternalMale.add(mother.getPersonID());
        dataCache.setMaternalFemaleAncestors(maternalFemale);

        HashSet<String> paternalMale = new HashSet<>();
        maternalMale.add(father.getPersonID());
        dataCache.setPaternalMaleAncestors(paternalMale);

        HashSet<String> paternalFemale = new HashSet<>();
        maternalMale.add(currPerson.getPersonID());
        dataCache.setPaternalFemaleAncestors(paternalFemale);


        birth = new Event("eventId1", "username",
                "motherId", 35.9f, 140.1f , "US", "SLC", "birth", 1970);
        naming = new Event("eventId2", "username",
                "fatherId", 35.9f, 140.1f , "US", "SLC", "naming", 1971);
        marriage = new Event("eventId3", "username",
                "randomMaleId", 35.9f, 140.1f , "US", "SLC", "marriage", 1995);
        death = new Event("eventId4", "username",
                "randomFemaleId", 35.9f, 140.1f , "US", "SLC", "death", 2020);

        HashMap<String, Event> events = new HashMap<>();
        events.put(birth.getEventID(), birth);
        events.put(naming.getEventID(), naming);
        events.put(marriage.getEventID(), marriage);
        events.put(death.getEventID(), death);

        dataCache.setPersons(persons);
        dataCache.setEvents(events);

        filter = new Filter();
    }

    @Test
    public void filterEventsPass() {
        filter.filterFemale(false);
        for (Map.Entry<String, Event> currEntry : dataCache.getFilteredEvents().entrySet()) {
            assertEquals("m", dataCache.getPersonById(currEntry.getValue().getPersonID()).getGender());
        }
        filter.filterFemale(true);
        filter.filterMale(false);
        for (Map.Entry<String, Event> currEntry : dataCache.getFilteredEvents().entrySet()) {
            assertEquals("f", dataCache.getPersonById(currEntry.getValue().getPersonID()).getGender());
        }
        filter.filterMale(true);
        filter.filterMotherSide(false);
        filter.filterFatherSide(false);
        assertFalse(dataCache.getFilteredEvents().containsValue(birth));
        assertFalse(dataCache.getFilteredEvents().containsValue(naming));
        assertTrue(dataCache.getFilteredEvents().containsValue(marriage));

        filter.filterFatherSide(true);
        filter.filterMotherSide(true);
    }

    @Test
    public void filterEventsPassTwo() {
        filter.filterMale(false);
        filter.filterFemale(false);
        assertDoesNotThrow(() -> filter.filterMotherSide(false));
        assertDoesNotThrow(() -> filter.filterFatherSide(false));
        assertTrue(dataCache.getFilteredEvents().isEmpty());
        filter.filterFatherSide(true);
        filter.filterMotherSide(true);
        filter.filterFemale(true);
        filter.filterMale(true);
        assertEquals(dataCache.getFilteredEvents().size(), dataCache.getEvents().size());
    }
}
