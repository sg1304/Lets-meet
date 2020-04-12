package com.project.letsmeet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment {

    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;
    Button btnStartDate, btnEndDate, addPart;
    private static final String TAG = "CreateEvent";
    Button txtLocation;
    private String lat;
    private String longt;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    ArrayList<String> email_group;
    ArrayList<String> token_group;
    ArrayList<String> id_group;

    private View addEventFragment;
    private DatabaseReference eventsRef;
    private DatabaseReference userRef;
    private DatabaseReference chatRef;
    private Button add;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUserRef;
    String currentUserId;
    int count;
    private TextView event_title;
    private TextView event_desc;

    private ListView group_lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addEventFragment = inflater.inflate(R.layout.fragment_add_event, container, false);

        TinyDB tinyDB = new TinyDB(getActivity());
        tinyDB.remove("email_group");
        tinyDB.remove("token_group");
        tinyDB.remove("id_group");

        btnStartDate = (Button) addEventFragment.findViewById(R.id.btnStartDate);
        btnEndDate = (Button) addEventFragment.findViewById(R.id.btnEndDate);
        txtLocation = (Button) addEventFragment.findViewById(R.id.txtLocation);
        addPart = (Button)addEventFragment.findViewById(R.id.addParticipant);
        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault());
        group_lv = (ListView)addEventFragment.findViewById(R.id.particips_listview);
        add = (Button) addEventFragment.findViewById(R.id.btnDone);
        event_title = (TextView)addEventFragment.findViewById(R.id.txtEventName);
        event_desc = (TextView)addEventFragment.findViewById(R.id.txtEventDescription);

        group_lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, mDateDataSet, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, mDateDataSet2, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isPlayServicesOK()){
                    return;
                }
                else {
                    Intent intent = new Intent(getActivity(),MapsActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        addPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindUserActivity.class);
                //intent.putExtra("email_group", email_group);
                //intent.putExtra("token_group", token_group);
                startActivityForResult(intent, 2);
            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), FindUserActivity.class);
//                startActivity(intent);

                if(!validateForm()) {
                    Toast.makeText(getActivity(), "Please enter all the fields!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(id_group.size()==0) {
                    Toast.makeText(getActivity(), "There must be at least 1 partcipant to create an event!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                final List<Attendes> list = getAttendes();
                Event event = getEventDetails();
                final String eventId = eventsRef.push().getKey();
                eventsRef.child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            for(Attendes item : list){
                                String attendeeId = eventsRef.child(eventId).push().getKey();
                                eventsRef.child(eventId).child("attendees").child(attendeeId).setValue(item);
                                String attendeeuserEvent = userRef.child(item.getUserId()).child("events").push().getKey();
                                userRef.child(item.getUserId()).child("events").child(attendeeuserEvent).setValue(eventId);
                            }
                            String userEvent = currentUserRef.child("events").push().getKey();
                            currentUserRef.child("events").child(userEvent).setValue(eventId);
                            Toast.makeText(getActivity(), "Event created successfully!",
                                    Toast.LENGTH_SHORT).show();
                            resetItems();
                        }
                        else{
                            Toast.makeText(getActivity(), "Could not create event!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl("https://lets-meet-78307.firebaseapp.com/api/")
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//                    Api api = retrofit.create(Api.class);
//
//                    Call<ResponseBody> call = api.sendNotification("flEC_xvGjv0:APA91bGmgeThAvRr-JVP3yJj0g9a7GKeUrtoZ2b7eBgU8MRHPjK_-bGa9QZRGi7mfndHOPCHSf-5yloGYl76IIjqi1TgeL1AiGRDEavSg4uDnctnPVPbLSa0fpXsBEX05GzusrXxY0g6", "FCM event", "FCM body",eventId, "flEC_xvGjv0:APA91bGmgeThAvRr-JVP3yJj0g9a7GKeUrtoZ2b7eBgU8MRHPjK_-bGa9QZRGi7mfndHOPCHSf-5yloGYl76IIjqi1TgeL1AiGRDEavSg4uDnctnPVPbLSa0fpXsBEX05GzusrXxY0g6");
//
//                    call.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try {
//                                Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_LONG).show();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                        }
//                    });

            }


        });



        return addEventFragment;
    }

    public boolean validateForm() {
        if(event_title.getText().toString().equals("") || event_desc.getText().toString().equals("") || btnStartDate.getText().toString().toLowerCase().equals("start date") || btnEndDate.getText().toString().toLowerCase().equals("end date") || txtLocation.getText().toString().toLowerCase().equals("add location")) {
            return false;
        }
        return true;
    }

    public void resetItems() {
        btnEndDate.setText("end date");
        btnStartDate.setText("start date");
        txtLocation.setText("Add Location");
        event_desc.setText("");
        event_title.setText("");
        group_lv.clearChoices();
        updateListview();
        TinyDB tinyDB = new TinyDB(getActivity());
        tinyDB.remove("email_group");
        tinyDB.remove("token_group");
        tinyDB.remove("id_group");
        id_group.clear();
        token_group.clear();
        email_group.clear();
    }

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };
    private final DatePickerDialog.OnDateSetListener mDateDataSet2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,mTimeDataSet2, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            btnStartDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            btnEndDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("location")) {
                Log.d("MAP_RETURN", "The returning data  for map is: "+data.getExtras().getString("location"));
                txtLocation.setText(data.getExtras().getString("location"));
                lat = data.getExtras().getString("latitude");
                longt = data.getExtras().getString("longitude");

            }
        }
        else if(resultCode == RESULT_OK && requestCode == 2) {

//            if (data.hasExtra("token")) {
//                Log.d("PART_RETURN", "The returning data  for participants is: "+data.getExtras().getString("email"));
//                Toast.makeText(getActivity(), data.getExtras().getString("email"),
//                        Toast.LENGTH_LONG).show();
//                email_group = (ArrayList<String>)data.getStringArrayListExtra("dummy");
//                //token_group = (ArrayList<String>)data.getSerializableExtra("token_group");
//                for(String temp: email_group) {
//                    Log.d("FindUserLogMsg", "Frome activity1: "+temp);
//                }
//            }
            //updateListview();
        }
    }

    public void updateListview() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.particips_list_item,
                email_group );

        group_lv.setAdapter(arrayAdapter);
    }


    public String validate(int time) {
        if (time < 10)
            return "0" + String.valueOf(time);
        else
            return String.valueOf(time);
    }

    public boolean isPlayServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity()); // check whether play service is available or not
        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "Google play services working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){ // check whether updating play services will fix this issue
            Log.d(TAG, "Google play error occurred but can resolve");

            // Google will show an error dialog and solution for it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(getActivity(), "Play services unavailable", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        TinyDB tinyDB = new TinyDB(getActivity());
        email_group = tinyDB.getListString("email_group");
        token_group = tinyDB.getListString("token_group");
        id_group = tinyDB.getListString("id_group");
        updateListview();

    }

    @Override
    public void onDestroyView() {
        Log.d("Ondestroyview","removied tinydb");
        super.onDestroyView();
        group_lv.clearChoices();
        TinyDB tinyDB = new TinyDB(getActivity());
        tinyDB.remove("email_group");
        tinyDB.remove("token_group");
        tinyDB.remove("id_group");
    }

    private List<Attendes> getAttendes(){
        List<Attendes> temp = new ArrayList<>();
        for(String userId: id_group){
            Attendes newAttende = new Attendes(userId,"pending");
            temp.add(newAttende);
        }
        return  temp;
    }
    private Event getEventDetails(){
        String eventDesc = event_desc.getText().toString();
        String eventName = event_title.getText().toString();
        String startDate = btnStartDate.getText().toString();
        String endDate = btnEndDate.getText().toString();
        String chatId = chatRef.push().getKey();
        Messages adminMessage = new Messages("Welcome to the chat room", "Admin");
        String adminMsgId = chatRef.child(chatId).push().getKey();
        chatRef.child(chatId).child(adminMsgId).setValue(adminMessage);
        String latitude = lat;
        String logitude = longt;
        String location = txtLocation.getText().toString();
        count = id_group.size();
        Event event = new Event(currentUserId,eventName,eventDesc,startDate,endDate,latitude,logitude, location,String.valueOf(count), chatId);
        return  event;
    }


    //    public static void setDefaults(String key, List<String> value, Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        Set<String> foo = new HashSet<>(value);
//        editor.putStringSet(key, foo);
//        editor.commit();
//    }
//
//    public static Set<String> getDefaults(String key, Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getStringSet(key, null);
//    }
}
