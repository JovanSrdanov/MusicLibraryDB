package activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.myapplication.R;

import java.util.UUID;

import arrayAdapter.GenreAdapter;
import model.Genre;
import repository.GenreRepository;

public class GenreActivity extends BaseDrawerActivity {

    private GenreAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_genre);

        ListView listView = findViewById(R.id.list_view);
        Button addButton = findViewById(R.id.button);
        EditText searchText = findViewById(R.id.et_search);

        adapter = new GenreAdapter(this, GenreRepository.getInstance().getAll());
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddGenreDialog());

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showAddGenreDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_genre, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Genre");
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String genreName = editName.getText().toString();
                Genre newGenre = new Genre(UUID.randomUUID(), genreName);
                GenreRepository.getInstance().create(newGenre);
                filterBySearch();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showEditGenreDialog(Genre genre) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_genre, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Genre");
        builder.setView(dialogView);

        EditText genreNameEditText = dialogView.findViewById(R.id.editName);
        genreNameEditText.setText(genre.getName());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = genreNameEditText.getText().toString();
                GenreRepository.getInstance().updateName(genre.getId(), newName);
                filterBySearch();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void filterBySearch() {
        EditText searchText = findViewById(R.id.et_search);
        adapter.clear();
        adapter.addAll(GenreRepository.getInstance().searchByName(searchText.getText().toString()));
        adapter.notifyDataSetChanged();
    }
}
