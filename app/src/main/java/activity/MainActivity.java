package activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import java.util.UUID;

import model.User;
import repository.ArtistRepository;
import repository.ArtistSongRepository;
import repository.DBHelper;
import repository.GenreArtistRepository;
import repository.GenreRepository;
import repository.GenreSongRepository;
import repository.PlayListRepository;
import repository.PlayListSongRepository;
import repository.SongRepository;
import repository.UserRepository;
import service.AuthorizationAndAuthentication;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private TextView credentialsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setupDataBaseAndLoadData();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        credentialsLabel = findViewById(R.id.credentials_label);

        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        Button cleanDatabase = findViewById(R.id.cleanDatabase);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton.setOnClickListener(v -> logCredentials());
        registerButton.setOnClickListener(v -> register());

        cleanDatabase.setOnClickListener(v -> {
            resetDataBase();
        });

    }

    private void resetDataBase() {
        DBHelper.getInstance(this).resetWholeDatabase();
        Toast.makeText(MainActivity.this, "Database cleaned!", Toast.LENGTH_SHORT).show();
    }

    private void logCredentials() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        if (AuthorizationAndAuthentication.getInstance().login(username, password)) {
            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            moveToNewPage();
            return;
        }
        credentialsLabel.setText("Username or password incorrect, try again!");
    }

    private void register() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (!UserRepository.getInstance().isUsernameUnique(username)) {
            credentialsLabel.setText("User with this username already exists");
            return;
        }
        UserRepository.getInstance().create(new User(UUID.randomUUID(), username, password));
        AuthorizationAndAuthentication.getInstance().login(username, password);
        Toast.makeText(MainActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
        moveToNewPage();
    }

    private void moveToNewPage() {
        Intent intent = new Intent(MainActivity.this, GenreActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupDataBaseAndLoadData() {
        DBHelper.getInstance(this);
        UserRepository.setInstance(this);
        GenreRepository.setInstance(this);
        ArtistRepository.setInstance(this);
        PlayListRepository.setInstance(this);
        SongRepository.setInstance(this);
        ArtistSongRepository.setInstance(this);
        GenreArtistRepository.setInstance(this);
        GenreSongRepository.setInstance(this);
        PlayListSongRepository.setInstance(this);

    }
}