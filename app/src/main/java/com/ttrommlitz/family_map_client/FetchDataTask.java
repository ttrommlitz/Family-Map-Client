package com.ttrommlitz.family_map_client;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import model.Event;
import model.Person;
import request.EventRequest;
import request.PersonRequest;
import result.EventResult;
import result.PersonResult;

public class FetchDataTask {
    private static final String LOG_TAG = "FetchData";
    private DataCache dataCache = DataCache.getInstance();
    private HashSet<String> maternalFemaleAncestors = new HashSet<>();
    private HashSet<String> paternalFemaleAncestors = new HashSet<>();
    private HashSet<String> maternalMaleAncestors = new HashSet<>();
    private HashSet<String> paternalMaleAncestors = new HashSet<>();
    public void fetch(ServerProxy server,String authtoken, String personID) {
        PersonRequest personRequest = new PersonRequest();
        EventRequest eventRequest = new EventRequest();

        personRequest.setAuthtoken(authtoken);
        eventRequest.setAuthtoken(authtoken);

        PersonResult personResult = server.getAllPersons(personRequest);
        EventResult eventResult = server.getAllEvents(eventRequest);

        if (!personResult.isSuccess() || !eventResult.isSuccess()) {
            Log.d(LOG_TAG, personResult.getMessage());
            Log.d(LOG_TAG, eventResult.getMessage());
            return;
        }

        // sets up map containing all Persons associated with current User
        HashMap<String, Person> persons = new HashMap<>();
        for (int i = 0; i < personResult.getData().length; i++) {
            persons.put(personResult.getData()[i].getPersonID(), personResult.getData()[i]);

            if (Objects.equals(personResult.getData()[i].getPersonID(), personID)) {
                dataCache.setUserFirstName(personResult.getData()[i].getFirstName());
                dataCache.setUserLastName(personResult.getData()[i].getLastName());
            }
        }
        dataCache.setPersons(persons);

        // sets up map containing all Events associated with current User
        HashMap<String, Event> events = new HashMap<>();
        for (int i = 0; i < eventResult.getData().length; i++) {
            events.put(eventResult.getData()[i].getEventID(), eventResult.getData()[i]);
        }
        dataCache.setEvents(events);

        // sets up map containing the Events associated with each Person
        HashMap<String, ArrayList<Event>> personEvents = new HashMap<>();
        for (Person person : personResult.getData()) {
            for (Event event : eventResult.getData()) {
                if (Objects.equals(person.getPersonID(), event.getPersonID())) {
                    if (personEvents.containsKey(person.getPersonID())) {
                        personEvents.get(person.getPersonID()).add(event);
                    } else {
                        ArrayList<Event> eventList = new ArrayList<>();
                        eventList.add(event);
                        personEvents.put(person.getPersonID(), eventList);
                    }
                }
            }
        }
        dataCache.setPersonEvents(personEvents);

        // sets up sets containing the ancestor ids associated with each User
        for (Person person : personResult.getData()) {
            // check if the current Person is the User
            if ((Objects.equals(person.getFirstName(), dataCache.userFirstName))
                    && (Objects.equals(person.getLastName(), dataCache.userLastName))) {
                addAncestors(person.getMotherID(), true);
                addAncestors(person.getFatherID(), false);
            }
        }

        dataCache.setMaternalFemaleAncestors(maternalFemaleAncestors);
        dataCache.setMaternalMaleAncestors(maternalMaleAncestors);
        dataCache.setPaternalFemaleAncestors(paternalFemaleAncestors);
        dataCache.setPaternalMaleAncestors(paternalMaleAncestors);
    }

    private void addAncestors(String personId, boolean isMothersSide) {
        if (personId == null) { return; }
        Person currPerson = dataCache.getPersonById(personId);
        if (Objects.equals(currPerson.getGender(), "f")) {
            if (isMothersSide) {
                maternalFemaleAncestors.add(personId);
            } else {
                paternalFemaleAncestors.add(personId);
            }
        } else {
            if (isMothersSide) {
                maternalMaleAncestors.add(personId);
            } else {
                paternalMaleAncestors.add(personId);
            }
        }

        addAncestors(currPerson.getMotherID(), isMothersSide);
        addAncestors(currPerson.getFatherID(), isMothersSide);
    }
}
