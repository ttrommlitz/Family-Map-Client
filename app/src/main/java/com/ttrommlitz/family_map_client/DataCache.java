package com.ttrommlitz.family_map_client;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import model.Event;
import model.Person;

// singleton data cache
public class DataCache {
    private static DataCache instance = new DataCache();

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {}

    // key is id
    HashMap<String, Person> persons;
    HashMap<String, Event> events;

    HashMap<String, Event> filteredEvents;
    HashMap<String, ArrayList<Event>> personEvents;
    HashSet<String> maternalFemaleAncestors;
    HashSet<String> maternalMaleAncestors;
    HashSet<String> paternalFemaleAncestors;
    HashSet<String> paternalMaleAncestors;
    String userFirstName;
    String userLastName;

    //settings
    public HashMap<String, Float> eventColors = new HashMap<>();

    public ArrayList<Float> colors = new ArrayList<>(Arrays.asList(
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET
    ));
    public int colorIndex = 0;
    public boolean showLifeStoryLines = true;
    public boolean showFamilyTreeLines = true;
    public boolean showSpouseLines = true;
    public boolean showFatherSide = true;
    public boolean showMotherSide = true;
    public boolean showMaleEvents = true;
    public boolean showFemaleEvents = true;
    private ArrayList<Polyline> currentPolylines = new ArrayList<>();
    public void addPolyline(Polyline line) {
        currentPolylines.add(line);
    }
    public void removeAllPolylines() {
        for (Polyline line : currentPolylines) {
            line.remove();
        }
        currentPolylines = new ArrayList<>();
    }


    // getters and setters
    public void setPersons(HashMap<String, Person> persons) {
        this.persons = persons;
    }

    public HashMap<String, Event> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, Event> events) {
        this.events = events;
        // At the beginnings, no events are filtered out
        filteredEvents = events;
    }

    public HashMap<String, Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(HashMap<String, Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }

    public void setPersonEvents(HashMap<String, ArrayList<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public HashSet<String> getMaternalFemaleAncestors() {
        return maternalFemaleAncestors;
    }

    public void setMaternalFemaleAncestors(HashSet<String> maternalFemaleAncestors) {
        this.maternalFemaleAncestors = maternalFemaleAncestors;
    }

    public HashSet<String> getMaternalMaleAncestors() {
        return maternalMaleAncestors;
    }

    public void setMaternalMaleAncestors(HashSet<String> maternalMaleAncestors) {
        this.maternalMaleAncestors = maternalMaleAncestors;
    }

    public HashSet<String> getPaternalFemaleAncestors() {
        return paternalFemaleAncestors;
    }

    public void setPaternalFemaleAncestors(HashSet<String> paternalFemaleAncestors) {
        this.paternalFemaleAncestors = paternalFemaleAncestors;
    }

    public HashSet<String> getPaternalMaleAncestors() {
        return paternalMaleAncestors;
    }

    public void setPaternalMaleAncestors(HashSet<String> paternalMaleAncestors) {
        this.paternalMaleAncestors = paternalMaleAncestors;
    }

    // methods
    public Person getPersonById(String personId) {
        if (personId == null) { return null; }
        return persons.get(personId);
    }

    public Event getEventById(String eventId) {
        return events.get(eventId);
    }

    public ArrayList<Event> getPersonEvents(String personId) {
        ArrayList<Event> unsortedEvents = personEvents.get(personId);
        ArrayList<Event> resultEvents = new ArrayList<>();
        if (unsortedEvents == null) { return null; }

        for (Event event : unsortedEvents) {
            if (filteredEvents.containsKey(event.getEventID())) {
                resultEvents.add(event);
            }
        }
        if (resultEvents.size() == 0) { return null; }
        sortEvents(resultEvents);
        return resultEvents;
    }

    public ArrayList<Person> getPersonFamilyMembers(String personId) {
        Person currPerson = getPersonById(personId);
        Person spouse = getPersonById(currPerson.getSpouseID());
        Person father = getPersonById(currPerson.getFatherID());
        Person mother = getPersonById(currPerson.getMotherID());

        ArrayList<Person> familyMembers = new ArrayList<>();
        if (spouse != null) { familyMembers.add(spouse); }
        if (mother != null) { familyMembers.add(mother); }
        if (father != null) { familyMembers.add(father); }

        for (Map.Entry<String, Person> entry : persons.entrySet()) {
            // if there is a mother, there will also be a father
            if (entry.getValue().getMotherID() != null) {
                if ((Objects.equals(entry.getValue().getMotherID(), personId)) || (Objects.equals(entry.getValue().getFatherID(), personId))) {
                    familyMembers.add(entry.getValue());
                }
            }
        }
        return familyMembers;
    }

    public ArrayList<Event> getSearchedEvents(String searched) {
        searched = searched.toLowerCase();
        ArrayList<Event> resultEvents = new ArrayList<>();
        for (Map.Entry<String, Event> entry : filteredEvents.entrySet()) {
            Event event = entry.getValue();
            if (event.getEventType().toLowerCase().contains(searched)
                    || event.getCity().toLowerCase().contains(searched)
                    || event.getCountry().toLowerCase().contains(searched)
                    || event.getYear().toString().contains(searched)) {
                resultEvents.add(event);
            }
        }
        return resultEvents;
    }

    public ArrayList<Person> getSearchedPersons(String searched) {
        searched = searched.toLowerCase();
        ArrayList<Person> resultPersons = new ArrayList<>();
        for (Map.Entry<String, Person> entry : persons.entrySet()) {
            Person person = entry.getValue();
            if (person.getFirstName().toLowerCase().contains(searched)
                    || person.getLastName().toLowerCase().contains(searched)) {
                resultPersons.add(person);
            }
        }
        return resultPersons;
    }

    // made public for testing
    public void sortEvents(ArrayList<Event> unsortedEvents) {
         class eventComparator implements Comparator<Event> {
            @Override
            public int compare(Event o, Event t1) {
                if (o.getYear() > t1.getYear()) { return 1; }
                else if (o.getYear() < t1.getYear()) { return -1; }
                else {
                    return o.getEventType().compareToIgnoreCase(t1.getEventType());
                }
            }
        }

        unsortedEvents.sort(new eventComparator());
    }
}
