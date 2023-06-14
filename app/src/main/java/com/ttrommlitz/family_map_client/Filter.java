package com.ttrommlitz.family_map_client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.Event;
import model.Person;

public class Filter {
    private DataCache dataCache = DataCache.getInstance();

    public void filterFatherSide(boolean checked) {
        dataCache.showFatherSide = checked;
        HashMap<String, Event> currentEvents = dataCache.getFilteredEvents();
        HashMap<String, Event> newFilteredEvents = new HashMap<>(currentEvents);
        if (checked) {
            // if any event is associated with a paternal ancestor, add it to the list of filtered events
            for (Map.Entry<String, Event> entry : dataCache.getEvents().entrySet()) {
                if (dataCache.getPaternalMaleAncestors().contains(entry.getValue().getPersonID())
                        || dataCache.getPaternalFemaleAncestors().contains(entry.getValue().getPersonID())) {
                    newFilteredEvents.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            // if any currently displayed event is associated with a paternal ancestor, remove it
            for (Map.Entry<String, Event> entry : dataCache.getFilteredEvents().entrySet()) {
                if (dataCache.getPaternalMaleAncestors().contains(entry.getValue().getPersonID())
                        || dataCache.getPaternalFemaleAncestors().contains(entry.getValue().getPersonID())) {
                    newFilteredEvents.remove(entry.getKey());
                }
            }
        }
        dataCache.setFilteredEvents(newFilteredEvents);
    }

    public void filterMotherSide(boolean checked) {
        dataCache.showMotherSide = checked;
        HashMap<String, Event> currentEvents = dataCache.getFilteredEvents();
        HashMap<String, Event> newFilteredEvents = new HashMap<>(currentEvents);
        if (checked) {
            // if any event is associated with a maternal ancestor, add it to the list of filtered events
            for (Map.Entry<String, Event> entry : dataCache.getEvents().entrySet()) {
                if (dataCache.getMaternalMaleAncestors().contains(entry.getValue().getPersonID())
                        || dataCache.getMaternalFemaleAncestors().contains(entry.getValue().getPersonID())) {
                    newFilteredEvents.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            // if any currently displayed event is associated with a maternal ancestor, remove it
            for (Map.Entry<String, Event> entry : dataCache.getFilteredEvents().entrySet()) {
                if (dataCache.getMaternalMaleAncestors().contains(entry.getValue().getPersonID())
                        || dataCache.getMaternalFemaleAncestors().contains(entry.getValue().getPersonID())) {
                    newFilteredEvents.remove(entry.getKey());
                }
            }
        }
        dataCache.setFilteredEvents(newFilteredEvents);
    }

    public void filterMale(boolean checked) {
        dataCache.showMaleEvents = checked;
        HashMap<String, Event> currentEvents = dataCache.getFilteredEvents();
        HashMap<String, Event> newFilteredEvents = new HashMap<>(currentEvents);
        if (checked) {
            for (Map.Entry<String, Event> entry : dataCache.getEvents().entrySet()) {
                Person currPerson = dataCache.getPersonById(entry.getValue().getPersonID());
                if (dataCache.showMotherSide && dataCache.showFatherSide) {
                    // add any events associated with a male
                    if (Objects.equals(currPerson.getGender(), "m")) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else if (dataCache.showMotherSide) {
                    // only add events associated with a male not a member of paternal male ancestors
                    if (Objects.equals(currPerson.getGender(), "m")
                            && !dataCache.getPaternalMaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else if (dataCache.showFatherSide) {
                    // only add events associated with a male not a member of maternal male ancestors
                    if (Objects.equals(currPerson.getGender(), "m")
                            && !dataCache.getMaternalMaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    // only add events associated with a male not a member of paternal or maternal male ancestors
                    if (Objects.equals(currPerson.getGender(), "m")
                            && !dataCache.getMaternalMaleAncestors().contains(currPerson.getPersonID())
                            && !dataCache.getPaternalMaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } else {
            // if any currently displayed event is associated with a male, remove it
            for (Map.Entry<String, Event> entry : dataCache.getFilteredEvents().entrySet()) {
                if (Objects.equals(dataCache.getPersonById(entry.getValue().getPersonID()).getGender(), "m")) {
                    newFilteredEvents.remove(entry.getKey());
                }
            }
        }
        dataCache.setFilteredEvents(newFilteredEvents);
    }

    public void filterFemale(boolean checked) {
        dataCache.showFemaleEvents = checked;
        HashMap<String, Event> currentEvents = dataCache.getFilteredEvents();
        HashMap<String, Event> newFilteredEvents = new HashMap<>(currentEvents);
        if (checked) {
            for (Map.Entry<String, Event> entry : dataCache.getEvents().entrySet()) {
                Person currPerson = dataCache.getPersonById(entry.getValue().getPersonID());
                if (dataCache.showMotherSide && dataCache.showFatherSide) {
                    // add any events associated with a female
                    if (Objects.equals(currPerson.getGender(), "f")) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else if (dataCache.showMotherSide) {
                    // only add events associated with a female not a member of paternal female ancestors
                    if (Objects.equals(currPerson.getGender(), "f")
                            && !dataCache.getPaternalFemaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else if (dataCache.showFatherSide) {
                    // only add events associated with a female not a member of maternal female ancestors
                    if (Objects.equals(currPerson.getGender(), "f")
                            && !dataCache.getMaternalFemaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    // only add events associated with a female not a member of paternal or maternal female ancestors
                    if (Objects.equals(currPerson.getGender(), "f")
                            && !dataCache.getMaternalFemaleAncestors().contains(currPerson.getPersonID())
                            && !dataCache.getPaternalFemaleAncestors().contains(currPerson.getPersonID())) {
                        newFilteredEvents.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } else {
            for (Map.Entry<String, Event> entry : dataCache.getFilteredEvents().entrySet()) {
                if (Objects.equals(dataCache.getPersonById(entry.getValue().getPersonID()).getGender(), "f")) {
                    newFilteredEvents.remove(entry.getKey());
                }
            }
        }
        dataCache.setFilteredEvents(newFilteredEvents);
    }
}
