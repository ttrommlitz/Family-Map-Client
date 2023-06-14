package com.ttrommlitz.family_map_client.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ttrommlitz.family_map_client.DataCache;
import com.ttrommlitz.family_map_client.PersonActivity;
import com.ttrommlitz.family_map_client.R;
import com.ttrommlitz.family_map_client.SearchActivity;
import com.ttrommlitz.family_map_client.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int INITIAL_FAMILY_LINE_WIDTH = 16;
    private GoogleMap googleMap;
    private DataCache dataCache = DataCache.getInstance();
    private TextView textView;
    private ImageView imageView;
    private String eventContext;
    private Event currEvent;
    private Person currPerson;
    private boolean isFemale;
    private String eventId;
    private LinearLayout eventInformationDisplay;

    public MapFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        eventInformationDisplay = view.findViewById(R.id.eventInformationDisplay);
        textView = view.findViewById(R.id.mapTextView);
        imageView = view.findViewById(R.id.genderIcon);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        addEventMarkers();

        if (eventId != null) {
            currEvent = dataCache.getEventById(eventId);
            currPerson = dataCache.getPersonById(currEvent.getPersonID());

            setEventInformationDisplay();
            LatLng startLocation = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            drawRelationshipLines(startLocation, currEvent);
        } else {
            imageView.setImageResource(R.drawable.android_48px);
            imageView.setColorFilter(ContextCompat.getColor(getActivity(),
                    R.color.green));
            textView.setText("Select a Marker to display its information");
            LatLng location = new LatLng(0, 0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
        }
        eventInformationDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Only open a Person Activity if an event is selected
                if (!textView.getText().toString().startsWith("Select")) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra("personId", currPerson.getPersonID());
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_menu_item) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.settings_menu_item) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void addMarker(LatLng location, Event event) {
        float googleColor = chooseColor(event.getEventType());
        Marker marker = googleMap.addMarker(new MarkerOptions().position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(googleColor)));
        marker.setTag(event);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                dataCache.removeAllPolylines();
                currEvent = ((Event)(marker.getTag()));
                currPerson = dataCache.getPersonById(currEvent.getPersonID());

                drawRelationshipLines(marker.getPosition(), currEvent);
                setEventInformationDisplay();
                return true;
            }
        });
    }

    private void setEventInformationDisplay() {
        StringBuilder sb = new StringBuilder().append(currPerson.getFirstName()).append(" ").append(currPerson.getLastName())
                .append("\n").append(currEvent.getEventType()).append(": ").append(currEvent.getCity()).append(", ")
                .append(currEvent.getCountry()).append(" (").append(currEvent.getYear()).append(")");
        eventContext = sb.toString();

        textView.setText(eventContext);
        isFemale = Objects.equals(currPerson.getGender(), "f");
        imageView.setImageResource(
                isFemale ? R.drawable.woman_48px : R.drawable.man_48px
        );
        imageView.setColorFilter(ContextCompat.getColor(getActivity(),
                isFemale ? R.color.pink : R.color.blue));
        LatLng location = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void drawRelationshipLines(LatLng startLocation, Event startEvent) {
        Person startPerson = dataCache.getPersonById(startEvent.getPersonID());
        Person spouse;
        Event endEvent;
        if (dataCache.showLifeStoryLines) {
            ArrayList<Event> sortedLifeEvents = dataCache.getPersonEvents(startPerson.getPersonID());
            for (int i = 0; i < sortedLifeEvents.size() - 1; i++) {
                LatLng firstLocation = new LatLng(sortedLifeEvents.get(i).getLatitude(), sortedLifeEvents.get(i).getLongitude());
                LatLng nextLocation = new LatLng(sortedLifeEvents.get(i + 1).getLatitude(),sortedLifeEvents.get(i + 1).getLongitude());

                PolylineOptions options = new PolylineOptions()
                        .add(firstLocation)
                        .add(nextLocation)
                        .color(getContext().getColor(R.color.red))
                        .width(15);
                Polyline line = googleMap.addPolyline(options);
                dataCache.addPolyline(line);
            }
        }
        if (dataCache.showFamilyTreeLines) {
            drawFamilyTreeLines(startEvent, INITIAL_FAMILY_LINE_WIDTH);
        }
        if (dataCache.showSpouseLines) {
            if (startPerson.getSpouseID() != null) {
                spouse = dataCache.getPersonById(startPerson.getSpouseID());

                ArrayList<Event> spouseEvents = dataCache.getPersonEvents(spouse.getPersonID());
                if (spouseEvents != null) {
                    // get earliest chronological event of spouse
                    endEvent = dataCache.getPersonEvents(spouse.getPersonID()).get(0);
                    LatLng endLocation = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

                    PolylineOptions options = new PolylineOptions()
                            .add(startLocation)
                            .add(endLocation)
                            .color(getContext().getColor(R.color.green))
                            .width(15);
                    Polyline line = googleMap.addPolyline(options);
                    dataCache.addPolyline(line);
                }
            }
        }
    }

    private void drawFamilyTreeLines(Event firstEvent, int width) {
        Person firstPerson = dataCache.getPersonById(firstEvent.getPersonID());
        Person mother = dataCache.getPersonById(firstPerson.getMotherID());
        Person father = dataCache.getPersonById(firstPerson.getFatherID());
        LatLng currLocation = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());

        if (mother != null) {
            ArrayList<Event> motherEvents = dataCache.getPersonEvents(mother.getPersonID());
            if (motherEvents != null) {
                Event motherFirstEvent = motherEvents.get(0);
                if (motherFirstEvent != null) {
                    LatLng motherLocation = new LatLng(motherFirstEvent.getLatitude(), motherFirstEvent.getLongitude());
                    PolylineOptions options = new PolylineOptions()
                            .add(currLocation, motherLocation)
                            .color(getContext().getColor(R.color.violet))
                            .width(width);
                    Polyline line = googleMap.addPolyline(options);
                    dataCache.addPolyline(line);

                    drawFamilyTreeLines(motherFirstEvent, width / 2);
                }
            }
        }
        if (father != null) {
            ArrayList<Event> fatherEvents = dataCache.getPersonEvents(father.getPersonID());
            if (fatherEvents != null) {
                Event fatherFirstEvent = fatherEvents.get(0);
                if (fatherFirstEvent != null) {
                    LatLng fatherLocation = new LatLng(fatherFirstEvent.getLatitude(), fatherFirstEvent.getLongitude());
                    PolylineOptions options = new PolylineOptions()
                            .add(currLocation, fatherLocation)
                            .color(getContext().getColor(R.color.violet))
                            .width(width);
                    Polyline line = googleMap.addPolyline(options);
                    dataCache.addPolyline(line);

                    drawFamilyTreeLines(fatherFirstEvent, width / 2);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap == null) { return; }
        googleMap.clear();
        addEventMarkers();
        if (currEvent != null && dataCache.getFilteredEvents().containsKey(currEvent.getEventID())) {
            drawRelationshipLines(new LatLng(currEvent.getLatitude(), currEvent.getLongitude()), currEvent);
        } else {
            currEvent = null;
            imageView.setImageResource(R.drawable.android_48px);
            imageView.setColorFilter(ContextCompat.getColor(getActivity(),
                    R.color.green));
            textView.setText("Select a Marker to display its information");
            LatLng location = new LatLng(0, 0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    private void addEventMarkers() {
        HashMap<String,Event> events = dataCache.getFilteredEvents();
        for (Map.Entry<String, Event> event : events.entrySet()) {
            LatLng markerLocation = new LatLng(event.getValue().getLatitude(), event.getValue().getLongitude());
            addMarker(markerLocation, event.getValue());
        }
    }

    private float chooseColor(String eventType) {
        eventType = eventType.toLowerCase();
        if (!dataCache.eventColors.containsKey(eventType)) {
            if (dataCache.colorIndex == dataCache.colors.size()) {
                dataCache.colorIndex = 0;
            }
            dataCache.eventColors.put(eventType, dataCache.colors.get(dataCache.colorIndex));
            dataCache.colorIndex++;
        }
        return dataCache.eventColors.get(eventType);
    }
}