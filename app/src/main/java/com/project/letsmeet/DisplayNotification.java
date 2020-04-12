package com.project.letsmeet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DisplayNotification extends AppCompatActivity {
    private static final String TAG = "FCMExampleMsgService";
    private TextView eventName, eventDes, startDate, endDate, location;
    private ListView participants;
    private Button accept, reject;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference currentEventRef;
    private DatabaseReference userRef;
    private String eventId, regId;
    List<User> userList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notification);

        mToolbar = (Toolbar) findViewById(R.id.display_event_toolbar);
        eventName = (TextView) findViewById(R.id.txtEventName);
        eventDes = (TextView) findViewById(R.id.txtEventDes);
        startDate = (TextView) findViewById(R.id.txtStartDate);
        endDate = (TextView) findViewById(R.id.txtEndDate);
        participants = (ListView) findViewById(R.id.txtParticipants);
        location = (TextView)  findViewById(R.id.display_txtLocation);
        accept = (Button)  findViewById(R.id.btnAccept);
        reject = (Button)  findViewById(R.id.btnReject);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        participants.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        userList = new ArrayList<>();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Meet");

        if(getIntent().getExtras() != null){
            eventId = getIntent().getExtras().getString("eventId");
            regId = getIntent().getExtras().getString("regId");
            final DatabaseReference currEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId);
            currEventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if(event != null ){
                        eventName.setText(event.getEventName());
                        eventDes.setText(event.getEventDes());
                        startDate.setText(event.getStartDate());
                        endDate.setText(event.getEndDate());
                        location.setText(event.getLocation());
                        DatabaseReference currAttendes = currEventRef.child("attendees");
                        currAttendes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Attendes attendes = postSnapshot.getValue(Attendes.class);
                                    String userId = attendes.getUserId();
                                    Log.d(TAG,userId);
                                    DatabaseReference currUserRef = userRef.child(userId);
                                    currUserRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            User user1 = new User();
                                            user1.setEmail(user.getEmail());
                                            user1.setFirstName(user.getFirstName());
                                            user1.setLastName(user.getLastName());
                                            Log.d(TAG,user.getLastName());
                                            user1.setImage(user.getImage());
                                            userList.add(user1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
                                participants.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else{
            SharedPreferences mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
            eventId = mySharedPreferences.getString("eventId", "");
            regId = mySharedPreferences.getString("regId", "");
            final DatabaseReference currEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId);
            currEventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if(event != null ){
                        eventName.setText(event.getEventName());
                        eventDes.setText(event.getEventDes());
                        startDate.setText(event.getStartDate());
                        endDate.setText(event.getEndDate());
                        location.setText(event.getLocation());
                        String count = event.getNumberOfAttendes();
                        final int attendeeCount = Integer.valueOf(count);
                        DatabaseReference currAttendes = currEventRef.child("attendees");
                        currAttendes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Attendes attendes = postSnapshot.getValue(Attendes.class);
                                    String userId = attendes.getUserId();
                                    DatabaseReference currUserRef = userRef.child(userId);
                                    currUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            User user1 = new User();
                                            user1.setEmail(user.getEmail());
                                            user1.setFirstName(user.getFirstName());
                                            user1.setLastName(user.getLastName());
                                            user1.setImage(user.getImage());
                                            userList.add(user1);
                                            if(userList.size() == attendeeCount){
                                                UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
                                                participants.setAdapter(adapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
//                                UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
//                                participants.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
//                    UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
////                    participants.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference currEventAttendeesRef = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees");
                DatabaseReference userEvents = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("events");
                String tempeventId = userEvents.push().getKey();
                userEvents.child(tempeventId).setValue(eventId);
                currEventAttendeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Attendes attendes = data.getValue(Attendes.class);
                                String key = data.getKey();
                                if (attendes != null) {
                                    if(attendes.getUserId().equals(currentUserId)){
                                        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees").child(key).child("response").setValue("Attending");
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

        reject.setOnClickListener(new View.OnClickListener() {
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
                                        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees").child(key).child("response").setValue("Rejected");
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

    }
//    private void getAttendes(){
//        userList.clear();
//        DatabaseReference currAttendes = FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("attendees");
//        currAttendes.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Attendes attendes = postSnapshot.getValue(Attendes.class);
//                    String userId = attendes.getUserId();
//                    Log.d(TAG,userId);
//                    DatabaseReference currUserRef = userRef.child(userId);
//                    currUserRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            User user = dataSnapshot.getValue(User.class);
//                            User user1 = new User();
//                            user1.setEmail(user.getEmail());
//                            user1.setFirstName(user.getFirstName());
//                            user1.setLastName(user.getLastName());
//                            Log.d(TAG,user.getLastName());
//                            user1.setImage(user.getImage());
//                            userList.add(user1);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
//                participants.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
////            @Override
////            public void onDataChange(DataSnapshot snapshot) {
////                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
////                    Attendes attendes = postSnapshot.getValue(Attendes.class);
////                    String userId = attendes.getUserId();
////                    Log.d(TAG,userId);
////                    DatabaseReference currUserRef = userRef.child(userId);
////                    currUserRef.addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                            User user = dataSnapshot.getValue(User.class);
////                            User user1 = new User();
////                            user1.setEmail(user.getEmail());
////                            user1.setFirstName(user.getFirstName());
////                            user1.setLastName(user.getLastName());
////                            Log.d(TAG,user.getLastName());
////                            user1.setImage(user.getImage());
////                            userList.add(user1);
////                        }
////
////                        @Override
////                        public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                        }
////                    });
////                }
////                UserAdapter adapter = new UserAdapter(DisplayNotification.this, R.layout.adapter_view_layout, userList);
////                participants.setAdapter(adapter);
////            }
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//    }
}

