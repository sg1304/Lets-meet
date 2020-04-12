package com.project.letsmeet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout mytabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    public static final String CHANNEL_ID = "lets_meet";
    private static final String CHANNEL_NAME = "Lets Meet";
    private static final String CHANNEL_DESC = "Lets Meet Notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        if(!loggedIn()) {
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.home_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Meet");
        myViewPager = (ViewPager) findViewById(R.id.home_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        mytabLayout = (TabLayout) findViewById(R.id.home_tabs);
        mytabLayout.setupWithViewPager(myViewPager);
        setUpNotification();
        if(getIntent().getExtras() != null){
            String event = getIntent().getExtras().getString("eventId");
            String reg = getIntent().getExtras().getString("regId");
            Intent intent = new Intent(this, DisplayNotification.class);
            Bundle bundle = new Bundle();
            bundle.putString("eventId", event);
            bundle.putString("regId", reg);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }



        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser.isEmailVerified()) {
//            findViewById(R.id.email_verify_button).setVisibility(View.INVISIBLE);
//        }
    }

    protected void onStart() {
        if(!loggedIn()) {
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
        FirebaseApp.initializeApp(this);

    }


//    public void emailVerify(View view) {
//        // Disable button
//        findViewById(R.id.email_verify_button).setEnabled(false);
//
//        // Send verification email
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Re-enable button
//                        findViewById(R.id.email_verify_button).setEnabled(true);
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(HomeActivity.this,
//                                    "Verification email sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                            findViewById(R.id.email_verify_button).setVisibility(View.INVISIBLE);
//                        } else {
//                            Log.e("Home_Page", "sendEmailVerification", task.getException());
//                            Toast.makeText(HomeActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.home_logout_option){
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public boolean loggedIn() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.print("Current user: "+currentUser);
        if(currentUser==null) {
            return false;
        }
        return true;
    }
    public void setUpNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}