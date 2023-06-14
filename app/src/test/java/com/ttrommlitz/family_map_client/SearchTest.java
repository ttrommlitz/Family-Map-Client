package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import model.Event;
import model.Person;

public class SearchTest {
    private static DataCache dataCache;
    private static Person child;
    private static Person currPerson;
    private static Person spouse;
    private static Person mother;
    private static Person father;
    private static Person randomPerson;
    private static Event birth;
    private static Event naming;
    private static Event marriage;
    private static Event death;

    @BeforeAll
    static void setUp() {
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
        child = new Person("childId", "username", "childName",
                "lastName", "m", null, currPerson.getPersonID(), null);
        randomPerson = new Person("randomPersonId", "username", "random",
                "person", "f", null, null, null);

        HashMap<String, Person> persons = new HashMap<>();
        persons.put(mother.getPersonID(), mother);
        persons.put(father.getPersonID(), father);
        persons.put(currPerson.getPersonID(), currPerson);
        persons.put(spouse.getPersonID(), spouse);
        persons.put(child.getPersonID(), child);
        persons.put(randomPerson.getPersonID(), randomPerson);

        birth = new Event("eventId1", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "birth", 1970);
        naming = new Event("eventId2", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "naming", 1971);
        marriage = new Event("eventId3", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "marriage", 1995);
        death = new Event("eventId4", "username",
                "personId", 35.9f, 140.1f , "US", "SLC", "death", 2020);

        HashMap<String, Event> events = new HashMap<>();
        events.put(birth.getEventID(), birth);
        events.put(naming.getEventID(), naming);
        events.put(marriage.getEventID(), marriage);
        events.put(death.getEventID(), death);

        dataCache = DataCache.getInstance();
        dataCache.setPersons(persons);
        dataCache.setEvents(events);
    }

    @Test
    public void searchPass() {
        ArrayList<Person> persons = dataCache.getSearchedPersons("nam");
        ArrayList<Event> events = dataCache.getSearchedEvents("nam");
        assertEquals(1, events.size());
        assertEquals(5, persons.size());
        assertTrue(events.contains(naming));
        assertTrue(persons.contains(currPerson));

    }

    @Test
    public void searchPassTwo() {
        ArrayList<Person> persons = dataCache.getSearchedPersons("SomeCrazyPattern");
        ArrayList<Event> events = dataCache.getSearchedEvents("SomeCrazyPattern");
        assertEquals(0, events.size());
        assertEquals(0, persons.size());
    }
}
