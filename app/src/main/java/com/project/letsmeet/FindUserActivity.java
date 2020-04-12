package com.project.letsmeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.common.collect.Sets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindUserActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton searchButton;
    private EditText searchInputText;
    final static String TAG = "FindUserLogMsg";
    private ListView lv;
    private FirebaseAuth mAuth;

    ArrayList<String> email_group;
    ArrayList<String> token_group;
    ArrayList<String> id_group;

    private DatabaseReference allUsersDatabaseRef;
    private List<User> userList;
    private ListView listView;
    private  RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "testing...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.find_recycle_image);

        lv = (ListView)findViewById(R.id.particips_listview_findUser);
        email_group = new ArrayList<String>();
        token_group = new ArrayList<String>();
        id_group = new ArrayList<String>();

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = (Toolbar) findViewById(R.id.find_user_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Meet");
        listView = (ListView) findViewById(R.id.listview);
        userList = new ArrayList<>();

        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchInputText = (EditText) findViewById(R.id.search_box);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String searchText = searchInputText.getText().toString();
                listView.clearChoices();
                userList.clear();
                if(searchText == null || searchText.equals("")) {
                    Toast.makeText(FindUserActivity.this, "Please enter something to search.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    searchUsers(searchText);
                }
            }
        });
    }


    private void searchUsers(String searchText) {
        Query query = allUsersDatabaseRef.orderByChild("firstName").startAt(searchText).endAt(searchText + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        String userID = data.getKey().toString();
                        User user1 = data.getValue(User.class);
                        if(user1 != null ){

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser.getEmail().equals(user1.getEmail())) {
                                continue;
                            }

                            User user = new User();
                            user.setId(userID);
                            user.setEmail(user1.getEmail());
                            user.setFirstName(user1.getFirstName());
                            user.setLastName(user1.getLastName());
                            user.setImage(user1.getImage());
                            user.setToken(user1.getToken());
                            userList.add(user);
                        }

                    }
                    UserAdapter adapter = new UserAdapter(FindUserActivity.this, R.layout.adapter_view_layout, userList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            User temp = userList.get(position);
                            String token = temp.getToken();
                            String email = temp.getEmail();
                            String idTemp = temp.getId();
                            if(email_group.contains(email)) {
                                Toast.makeText(FindUserActivity.this, "This person has been already added!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            email_group.add(email);
                            token_group.add(token);
                            id_group.add(idTemp);
                            updateListviewFind();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent data = new Intent();

        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        TinyDB tinyDB = new TinyDB(this);
        tinyDB.putListString("email_group", email_group);
        tinyDB.putListString("token_group", token_group);
        tinyDB.putListString("id_group", id_group);

        Log.d("sharedPref", "In Pause of activity2");
    }


    public void updateListviewFind() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.particips_list_item,
                email_group );

        lv.setAdapter(arrayAdapter);
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