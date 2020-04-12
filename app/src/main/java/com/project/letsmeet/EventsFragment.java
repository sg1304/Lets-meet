package com.project.letsmeet;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    private static final String TAG = "EventsFragment";
    private View eventsFragment;
    private ListView eventsList;
    private DatabaseReference allEventsRef;
    private DatabaseReference allUsersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private List<Event> eventList;
    private TextView message;
    private int size;
    private DatabaseReference userRef;
    private EventAdapter adapter;


    public EventsFragment() {
        // Required empty public constructorEventsFragment
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        eventsFragment =  inflater.inflate(R.layout.fragment_events, container, false);
        eventsList = (ListView) eventsFragment.findViewById(R.id.upcoming_event_listview);
        allEventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        allUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //message = (TextView) eventsFragment.findViewById(R.id.No_events_message);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        eventList = new ArrayList<>();
//        adapter = new EventAdapter(getActivity(),R.layout.event_display, eventList);
//        eventsList.setAdapter(adapter);
        populateList();
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event temp = eventList.get(position);
                Intent intent= new Intent(getActivity(), DisplayEvents.class);
                intent.putExtra("Event", temp);
                startActivity(intent);
            }
        });

        return eventsFragment;
    }

    private void populateList(){
        final DatabaseReference currentUserEvents = allUsersRef.child(currentUserId).child("events");
        currentUserEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG,"Inside populatelist");
                    GenericTypeIndicator<HashMap<String, Object>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    Map<String, Object> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                    ArrayList<Object> objectArrayList = new ArrayList<Object>(objectHashMap.values());
                    size = objectArrayList.size();
                    //String eventId = String.valueOf(objectArrayList.get(1));
                    for(Object item: objectArrayList){
                        String evenId = String.valueOf(item);
                        Log.d(TAG,"Event id = " + evenId);
                        fetchEvents(evenId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void fetchEvents(String eventId){
        Log.d(TAG,"Inside fetch events");
        DatabaseReference currEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId);
        final DatabaseReference currAttendes = currEventRef.child("attendees");
        currEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event1 = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
                if(event1 != null ){
                    final Event event = new Event();
                    event.setEventName(event1.getEventName());
                    event.setEventDes(event1.getEventDes());
                    event.setEventCreatorId(event1.getEventCreatorId());
                    event.setStartDate(event1.getStartDate());
                    event.setEndDate(event1.getEndDate());
                    event.setEventId(key);
                    event.setChatId(event1.getChatId());
                    event.setLatitude(event1.getLatitude());
                    event.setLogitude(event1.getLogitude());
                    event.setLocation(event1.getLocation());
                    currAttendes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Attendes> list = new ArrayList<>();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Attendes attendes = postSnapshot.getValue(Attendes.class);
                                Attendes at = new Attendes();
                                String userId = attendes.getUserId();
                                String status = attendes.getResponse();
                                at.setResponse(status);
                                at.setUserId(userId);
                                list.add(at);
                            }
                            event.setAttendesList(list);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "Start date = " + event1.getStartDate());
                    eventList.add(event);
                    Collections.sort(eventList);
                    if(eventList.size() == size){
                        EventAdapter adapter = new EventAdapter(getActivity(),R.layout.event_display, eventList);
                        eventsList.setAdapter(adapter);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
