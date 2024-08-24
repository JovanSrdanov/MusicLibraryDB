package activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;

import repository.UserRepository;
import service.AuthorizationAndAuthentication;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        TextView tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        String welcomeMessage = "Welcome " + AuthorizationAndAuthentication.getInstance().getLoggedInUser().getUsername() + "!";
        tvWelcomeMessage.setText(welcomeMessage);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupNavigationDrawer();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        Button deleteAccountButton = findViewById(R.id.btn_delete_account);
        Button logoutButton = findViewById(R.id.btn_logout);
        deleteAccountButton.setOnClickListener(v -> {
            deleteAccount();
        });
        logoutButton.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void logoutUser() {
        AuthorizationAndAuthentication.getInstance().logout();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteAccount() {
        UserRepository.getInstance().delete(AuthorizationAndAuthentication.getInstance().getLoggedInUser().getId());
        AuthorizationAndAuthentication.getInstance().logout();
        Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            Intent activityIntent;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_genre) {
                activityIntent = new Intent(this, GenreActivity.class);
                startActivity(activityIntent);
            } else if (itemId == R.id.nav_artist) {
                activityIntent = new Intent(this, ArtistActivity.class);
                startActivity(activityIntent);
            } else if (itemId == R.id.nav_song) {
                activityIntent = new Intent(this, SongActivity.class);
                startActivity(activityIntent);
            } else if (itemId == R.id.nav_playlists) {
                activityIntent = new Intent(this, PlaylistActivity.class);
                startActivity(activityIntent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setContentLayout(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
    }
}
