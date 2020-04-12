package com.project.letsmeet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private int resource;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUserRef;
    private String currentUserId;

    public EventAdapter(Context context, int resource, List<Event> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String eventTitle = getItem(position).getEventName();
        String eventDes = getItem(position).getEventDes();
        String startDate = getItem(position).getStartDate();
        String eventCreator = getItem(position).getEventCreatorId();
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(Calendar.getInstance().getTime());
        //String[] times = startDate.split(" ");
        //Log.d("EventsFragment", times[0]);
        Log.d("EventsFragment", "Sys time = " + timeStamp);
        //String newTime = times[0];

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm a");
        Log.d("EventsFragment", "Sys time = " + startDate);
        LocalDate myDate = formatter.parseLocalDate(startDate);
        LocalDate sysTime = formatter.parseLocalDate(timeStamp);
        int status = 0;
        try {
            Date date1=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(startDate);
            Date date2=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(timeStamp);
            status = date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("SYSTIME", "Start Date: "+startDate);
        Log.d("SYSTIME", "timestamp "+timeStamp);

        final int is1After2 = status;

        Log.d("SYSTIME", "isAfter2 "+String.valueOf(is1After2));

        Event event = new Event(eventCreator,eventTitle,eventDes,startDate);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource,parent,false);
             TextView date = (TextView) convertView.findViewById(R.id.event_start_date);
        if(is1After2 < 0){
            date.setTextColor(Color.DKGRAY);
        }
        date.setText(event.getStartDate());
        TextView title = (TextView) convertView.findViewById(R.id.event_title);
        if(is1After2 < 0){
            title.setTextColor(Color.DKGRAY);
        }
        title.setText(event.getEventName());
        TextView des = (TextView) convertView.findViewById(R.id.event_description);
        if(is1After2 < 0){
            des.setTextColor(Color.DKGRAY);
        }
        des.setText(event.getEventDes());

        final TextView creator = (TextView) convertView.findViewById(R.id.event_creator);
        String userId = event.getEventCreatorId();
        if(userId.equals(currentUserId)){
            if(is1After2 < 0){
                creator.setTextColor(Color.DKGRAY);
            }
            creator.setText("Me");
        }
        else{
            DatabaseReference dbRef = currentUserRef.child(userId);
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(is1After2 < 0){
                        creator.setTextColor(Color.DKGRAY);
                    }
                    creator.setText(user.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return convertView;
    }
}
