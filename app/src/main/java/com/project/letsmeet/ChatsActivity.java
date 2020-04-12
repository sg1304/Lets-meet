package com.project.letsmeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.Image;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatsActivity extends AppCompatActivity {
    private DatabaseReference currentChatRef;
    private List<Messages> chatList;
    private ListView listofmessages;
    private ImageButton sendMessage;
    private Timer timer;
    private ChatAdapter adapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Bundle extras = getIntent().getExtras();
        final String chatId = extras.getString("CHAT_ID");
        Log.d("CHATTEST", "Inside chat activity " + chatId);
        sendMessage = (ImageButton) findViewById(R.id.fab);
        listofmessages = (ListView) findViewById(R.id.list_of_messages);
        mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Meet");
        chatList = new ArrayList<>();
        currentChatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);
        adapter = new ChatAdapter(ChatsActivity.this, R.layout.chat_list_item, chatList);
        listofmessages.setAdapter(adapter);
        //displayChatMessage();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input=findViewById(R.id.message_input);
                Messages msg = new Messages(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getEmail());
                currentChatRef.push().setValue(msg);
                input.setText("");
                chatList.add(msg);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                displayChatMessage();

            }
        }, 1000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
    }

    private void displayChatMessage(){
            currentChatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatList.clear();
                    Log.d("CHATTEST", dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Messages tempMsg = data.getValue(Messages.class);
                            String key = data.getKey();
                            if (key != null) {
                                Log.d("CHATTESTB", tempMsg.toString());
                                chatList.add(tempMsg);
                            }

                        }
                    }
//                    listofmessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }