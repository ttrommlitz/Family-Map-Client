package com.ttrommlitz.family_map_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {
    private static final int SEARCH_EVENT_ITEM = 1;
    private static final int SEARCH_PERSON_ITEM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataCache dataCache = DataCache.getInstance();

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ArrayList<Event> events = dataCache.getSearchedEvents(s);
                ArrayList<Person> persons = dataCache.getSearchedPersons(s);
                SearchActivityAdapter adapter = new SearchActivityAdapter(events, persons);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Event> events = dataCache.getSearchedEvents(s);
                ArrayList<Person> persons = dataCache.getSearchedPersons(s);
                SearchActivityAdapter adapter = new SearchActivityAdapter(events, persons);
                recyclerView.setAdapter(adapter);
                return true;
            }
        });
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

    private class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityViewHolder> {
        private final ArrayList<Event> events;
        private final ArrayList<Person> persons;

        public SearchActivityAdapter(ArrayList<Event> events, ArrayList<Person> persons) {
            this.events = events;
            this.persons = persons;
        }

        @NonNull
        @Override
        public SearchActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(
                    viewType == SEARCH_EVENT_ITEM ? R.layout.search_event_item : R.layout.search_person_item,
                    parent, false);

            return new SearchActivityViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchActivityViewHolder holder, int position) {
            if (position < persons.size()) {
                holder.bind(persons.get(position));
            } else {
                holder.bind(events.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + persons.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position < persons.size() ? SEARCH_PERSON_ITEM : SEARCH_EVENT_ITEM;
        }
    }

    private class SearchActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView title;
        private final TextView name;

        private final int viewType;
        private Event event;
        private Person person;
        private final DataCache dataCache = DataCache.getInstance();

        public SearchActivityViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            itemView.setOnClickListener(this);

            if (viewType == SEARCH_EVENT_ITEM) {
                icon = itemView.findViewById(R.id.search_event_icon);
                title = itemView.findViewById(R.id.search_event_title);
                name = itemView.findViewById(R.id.search_event_person);
            } else {
                icon = itemView.findViewById(R.id.search_person_icon);
                name = itemView.findViewById(R.id.search_person_person);
                title = null;
            }
        }

        private void bind(Event event) {
            this.event = event;
            icon.setImageResource(R.drawable.location_on_48px);
            title.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " ("
            + event.getYear() + ")");
            this.person = dataCache.getPersonById(this.event.getPersonID());
            name.setText(this.person.getFirstName() + " " + this.person.getLastName());
        }

        private void bind(Person person) {
            this.person = person;
            boolean isFemale = Objects.equals(this.person.getGender(), "f");
            icon.setImageResource(
                    isFemale ? R.drawable.woman_48px : R.drawable.man_48px
            );
            icon.setColorFilter(ContextCompat.getColor(SearchActivity.this,
                    isFemale ? R.color.pink : R.color.blue));
            name.setText(this.person.getFirstName() + " " + this.person.getLastName());
        }

        @Override
        public void onClick(View view) {
            // pulls up either event or person activity
            Intent intent;
            if (viewType == SEARCH_PERSON_ITEM) {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personId", person.getPersonID());
            } else {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("eventId", event.getEventID());
            }
            startActivity(intent);
        }
    }
}