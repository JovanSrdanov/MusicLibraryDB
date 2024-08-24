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

import java.util.ArrayList;
import java.util.UUID;

import arrayAdapter.ArtistAdapter;
import model.Artist;
import model.Genre;
import repository.ArtistRepository;
import repository.GenreArtistRepository;
import repository.GenreRepository;


public class ArtistActivity extends BaseDrawerActivity {
    private ArtistAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_artist);

        ListView listView = findViewById(R.id.list_view);
        Button addButton = findViewById(R.id.button);
        EditText searchEditText = findViewById(R.id.et_search);
        adapter = new ArtistAdapter(this, ArtistRepository.getInstance().getAll());
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddArtistDialog());

        searchEditText.addTextChangedListener(new TextWatcher() {
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

    private void showAddArtistDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_artist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Artist");
        builder.setView(dialogView);
        EditText editName = dialogView.findViewById(R.id.editName);


        ArrayList<Genre> genreList = GenreRepository.getInstance().getAll();
        boolean[] checkedGenres = new boolean[genreList.size()];
        ArrayList<Genre> selectedGenres = new ArrayList<>();

        String[] genreNames = new String[genreList.size()];
        for (int i = 0; i < genreList.size(); i++) {
            genreNames[i] = genreList.get(i).getName();
        }

        builder.setMultiChoiceItems(genreNames, checkedGenres, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedGenres.add(genreList.get(which));
                } else {
                    selectedGenres.remove(genreList.get(which));
                }
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Artist artist = new Artist(UUID.randomUUID(), editName.getText().toString(), selectedGenres);
                ArtistRepository.getInstance().create(artist);
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

    public void showEditArtistDialog(Artist artist) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_artist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Artist");
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.editName);
        nameEditText.setText(artist.getName());

        ArrayList<Genre> genreList = GenreRepository.getInstance().getAll();
        boolean[] checkedGenres = new boolean[genreList.size()];
        ArrayList<Genre> selectedGenres = new ArrayList<>();
        ArrayList<Genre> alreadySelectedGenres = artist.getGenres();

        String[] genreNames = new String[genreList.size()];
        for (int i = 0; i < genreList.size(); i++) {
            genreNames[i] = genreList.get(i).getName();
        }

        for (int i = 0; i < genreList.size(); i++) {
            for (int j = 0; j < alreadySelectedGenres.size(); j++) {
                if (genreNames[i].equals(alreadySelectedGenres.get(j).getName())) {
                    checkedGenres[i] = true;
                    selectedGenres.add(genreList.get(i));
                }
            }
        }

        builder.setMultiChoiceItems(genreNames, checkedGenres, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedGenres.add(genreList.get(which));
                } else {
                    selectedGenres.remove(genreList.get(which));
                }
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameEditText.getText().toString();
                ArtistRepository.getInstance().updateName(artist.getId(), newName);
                GenreArtistRepository.getInstance().replaceAllGenres(artist.getId(), selectedGenres);
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
        adapter.addAll(ArtistRepository.getInstance().searchByNameOrGenre(searchText.getText().toString()));
        adapter.notifyDataSetChanged();
    }
}