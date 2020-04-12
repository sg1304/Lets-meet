package com.project.letsmeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
//import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
//import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
//import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
//import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
//import com.seatgeek.placesautocomplete.model.Place;

import java.util.Arrays;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    final String TAG = "Location Services";
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView textLocation;
    //PlacesAutocompleteTextView placesAutocomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
//        placesAutocomplete = (PlacesAutocompleteTextView) findViewById(R.id.autocomplete_fragment);

        // Initialize the AutocompleteSupportFragment.
        //textLocation = (TextView)findViewById(R.id.textlocation);
//        textLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                    @Override
//                    public void onPlaceSelected(Place place) {
//                        // TODO: Get info about the selected place.
//                        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//                    }
//
//                    @Override
//                    public void onError(Status status) {
//                        // TODO: Handle the error.
//                        Log.i(TAG, "An error occurred: " + status);
//                    }
//                });
//            }
//        });

//        placesAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                System.out.println(place.description);
//            }
//        });

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}

