package com.project.letsmeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayEvents extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "FCMExampleMsgService";
    private TextView eventName, eventDes, startDate, endDate, location;
    private ListView participants;
    private Button accept, reject,chat;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference currentEventRef;
    private DatabaseReference userRef;
    private String eventId, regId,chatId;
    List<User> userList;
    private String currentUserId;
    private GoogleMap mMap;
    private LatLng latLng;
    private double latitude;
    private double longitude;
    UserEventStatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_events);
        Event event = (Event) getIntent().getSerializableExtra("Event");
        mToolbar = (Toolbar) findViewById(R.id.display_event_2_toolbar);
        eventName = (TextView) findViewById(R.id.txtEventName_2);
        eventDes = (TextView) findViewById(R.id.txtEventDes_2);
        startDate = (TextView) findViewById(R.id.txtStartDate_2);
        endDate = (TextView) findViewById(R.id.txtEndDate_2);
        participants = (ListView) findViewById(R.id.txtParticipants_2);
        location = (TextView)  findViewById(R.id.display_txtLocation_2);
        accept = (Button)  findViewById(R.id.btnAccept_2);
        reject = (Button)  findViewById(R.id.btnReject_2);
        eventId = event.getEventId();
        chatId = event.getChatId();
        Log.d("CHATTEST", chatId);
        latitude = Double.parseDouble(event.getLatitude());
        longitude = Double.parseDouble(event.getLogitude());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
//        adapter = new UserEventStatusAdapter(DisplayEvents.this, R.layout.user_event_status_adapter, userList);
//        participants.setAdapter(adapter);
        chat = (Button) findViewById(R.id.chat_button);

        participants.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.display_events_map);
        mapFragment.getMapAsync(this);

        userList = new ArrayList<>();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Meet");
        if(event.getEventCreatorId().equals(currentUserId)){
            accept.setVisibility(View.INVISIBLE);
            reject.setVisibility(View.INVISIBLE);
        }

        eventName.setText(event.getEventName());
        eventDes.setText(event.getEventDes());
        startDate.setText(event.getStartDate());
        endDate.setText(event.getEndDate());
        location.setText(event.getLocation());

        String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(Calendar.getInstance().getTime());
        int flag = 0;
        try {
            Date date1=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(startDate.getText().toString());
            Date date2=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(timeStamp);
            flag = date1.compareTo(date2);
            if(flag < 0) {
                accept.setVisibility(View.INVISIBLE);
                reject.setVisibility(View.INVISIBLE);
                chat.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final List<Attendes> list = event.getAttendesList();
        for(Attendes item: list){
            DatabaseReference ref = userRef.child(item.getUserId());
            final String status = item.getResponse();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user1 = dataSnapshot.getValue(User.class);
                    User user = new User();
                    user.setEmail(user1.getEmail());
                    user.setStatus(status);
                    user.setFirstName(user1.getFirstName());
                    user.setLastName(user1.getLastName());
                    user.setImage(user1.getImage());
                    userList.add(user);
                    if(userList.size() == list.size()){
                        UserEventStatusAdapter adapter = new UserEventStatusAdapter(DisplayEvents.this, R.layout.user_event_status_adapter, userList);
                        participants.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.setVisibility(View.INVISIBLE);
                DatabaseReference currEventAttendeesRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees");
                final DatabaseReference userEvents = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("events");
                userEvents.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GenericTypeIndicator<HashMap<String, Object>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            Map<String, Object> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                            for(Map.Entry<String, Object> entry: objectHashMap.entrySet()){
                                String index = entry.getKey();
                                Object tempEventId = entry.getValue();
                                String tempId = String.valueOf(tempEventId);
                                if(eventId.equals(tempId)){
                                    userEvents.child(index).removeValue();
                                    break;
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                currEventAttendeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Attendes attendes = data.getValue(Attendes.class);
                                String key = data.getKey();
                                if (attendes != null) {
                                    if(attendes.getUserId().equals(currentUserId)){
                                        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees").child(key).child("response").setValue("Declined");
                                        reject.setVisibility(View.INVISIBLE);
                                        accept.setVisibility(View.INVISIBLE);
                                        Toast.makeText(DisplayEvents.this, eventName.getText()+" will be removed from your events list!",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference currEventAttendeesRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees");
                currEventAttendeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Attendes attendes = data.getValue(Attendes.class);
                                String key = data.getKey();
                                if (attendes != null) {
                                    if(attendes.getUserId().equals(currentUserId)){
                                        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees").child(key).child("response").setValue("Accepted");
                                        reject.setVisibility(View.INVISIBLE);
                                        accept.setVisibility(View.INVISIBLE);
                                        Toast.makeText(DisplayEvents.this, eventName.getText()+" will be addedd to your events list!",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToChatActivity();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        mMap.setMyLocationEnabled(true);

    }

    public void sendToChatActivity(){
        Intent intent = new Intent(DisplayEvents.this, ChatsActivity.class);
        intent.putExtra("CHAT_ID", chatId);
        startActivity(intent);

    }
}
