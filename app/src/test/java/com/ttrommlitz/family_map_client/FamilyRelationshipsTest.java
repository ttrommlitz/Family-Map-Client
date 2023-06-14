package com.ttrommlitz.family_map_client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import model.Person;

public class FamilyRelationshipsTest {
    private static DataCache dataCache;
    private static Person child;
    private static Person currPerson;
    private static Person spouse;
    private static Person mother;
    private static Person father;
    private static Person randomPerson;

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

        dataCache = DataCache.getInstance();
        dataCache.setPersons(persons);
    }

    @Test
    public void familyRelationshipsPass() {
        ArrayList<Person> familyMembers = dataCache.getPersonFamilyMembers(currPerson.getPersonID());
        assertTrue(familyMembers.contains(father));
        assertTrue(familyMembers.contains(father));
        assertTrue(familyMembers.contains(spouse));
        assertTrue(familyMembers.contains(child));
        assertFalse(familyMembers.contains(randomPerson));
    }

    @Test
    public void familyRelationshipsPassTwo() {
        ArrayList<Person> randomFamilyMembers = dataCache.getPersonFamilyMembers(randomPerson.getPersonID());
        assertTrue(randomFamilyMembers.isEmpty());

        child = null;
        spouse = null;

        ArrayList<Person> familyMembers = dataCache.getPersonFamilyMembers(currPerson.getPersonID());
        assertTrue(familyMembers.contains(father));
        assertTrue(familyMembers.contains(father));
        assertFalse(familyMembers.contains(spouse));
        assertFalse(familyMembers.contains(child));
        assertFalse(familyMembers.contains(randomPerson));
    }
}
