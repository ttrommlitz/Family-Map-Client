package com.ttrommlitz.family_map_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {
    private String personId;
    private Person currPerson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        // set up linear layout
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            personId = extras.getString("personId");
        }
        currPerson = DataCache.getInstance().getPersonById(personId);

        TextView firstNameField = findViewById(R.id.person_activity_first_name);
        firstNameField.setText(currPerson.getFirstName());

        TextView lastNameField = findViewById(R.id.person_activity_last_name);
        lastNameField.setText(currPerson.getLastName());

        boolean isFemale = Objects.equals(currPerson.getGender(), "f");
        TextView genderField = findViewById(R.id.person_activity_gender);
        genderField.setText(isFemale ? "Female" : "Male");

        ExpandableListView expandableListView = findViewById(R.id.expandable_list_view);
        DataCache dataCache = DataCache.getInstance();

        // change
        ArrayList<Event> events = dataCache.getPersonEvents(personId);
        ArrayList<Person> persons = dataCache.getPersonFamilyMembers(personId);

        expandableListView.setAdapter(new ExpandableListAdapter(events, persons));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;
        private final ArrayList<Event> events;
        private final ArrayList<Person> persons;

        public ExpandableListAdapter(ArrayList<Event> events, ArrayList<Person> persons) {
            if (events == null) {
                this.events = new ArrayList<>();
            } else {
                this.events = events;
            }
            this.persons = persons;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case PERSON_GROUP_POSITION:
                    return persons.size();
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position");
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    return "Life Events";
                case PERSON_GROUP_POSITION:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position");
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                case PERSON_GROUP_POSITION:
                    return persons.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position");
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.person_list_headings, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.person_list_title);

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.person_events_title);
                    break;
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.person_family_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position");
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.search_event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.search_person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position");
            }
            return itemView;
        }

        private void initializeEventView(View eventItemView, int childPosition) {
            Event currEvent = events.get(childPosition);
            Person person = DataCache.getInstance().getPersonById(currEvent.getPersonID());

//            ImageView eventIcon = eventItemView.findViewById(R.id.search_event_icon);
//            eventIcon.setColorFilter(ContextCompat.getColor(PersonActivity.this,
//                    chooseColor(currEvent.getEventType())));

            TextView eventDetailsView = eventItemView.findViewById(R.id.search_event_title);
            eventDetailsView.setText(currEvent.getEventType() + ": " + currEvent.getCity() + ", " +
                    currEvent.getCountry() + " (" + currEvent.getYear() + ")");

            TextView eventPersonView = eventItemView.findViewById(R.id.search_event_person);
            eventPersonView.setText(person.getFirstName() + " " + person.getLastName());

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra("eventId", currEvent.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializePersonView(View personItemView, int childPosition) {
            Person person = persons.get(childPosition);

            ImageView personIcon = personItemView.findViewById(R.id.search_person_icon);
            boolean isFemale = Objects.equals(person.getGender(), "f");
            personIcon.setImageResource(
                    isFemale ? R.drawable.woman_48px : R.drawable.man_48px
            );
            personIcon.setColorFilter(ContextCompat.getColor(PersonActivity.this,
                    isFemale ? R.color.pink : R.color.blue));

            String relationship = getRelationship(person);
            TextView personDetailsView = personItemView.findViewById(R.id.search_person_person);
            personDetailsView.setText(person.getFirstName() + " " + person.getLastName()
            + " (" + relationship + ")");

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // start new PersonActivity
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personId", person.getPersonID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private String getRelationship(Person familyMember) {
            if (Objects.equals(currPerson.getFatherID(), familyMember.getPersonID())) {
                return "Father";
            } else if (Objects.equals(currPerson.getMotherID(), familyMember.getPersonID())) {
                return "Mother";
            } else if ((Objects.equals(currPerson.getPersonID(), familyMember.getMotherID()))
                    || (Objects.equals(currPerson.getPersonID(), familyMember.getFatherID()))) {
                return "Child";
            } else {
                return "Spouse";
            }
        }
    }
}