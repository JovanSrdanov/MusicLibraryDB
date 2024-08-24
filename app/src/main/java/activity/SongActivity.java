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

import arrayAdapter.SongAdapter;
import model.Artist;
import model.Genre;
import model.Song;
import repository.ArtistRepository;
import repository.ArtistSongRepository;
import repository.GenreRepository;
import repository.GenreSongRepository;
import repository.SongRepository;


public class SongActivity extends BaseDrawerActivity {

    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_song);

        ListView listView = findViewById(R.id.list_view);
        Button addButton = findViewById(R.id.button);
        EditText searchText = findViewById(R.id.et_search);

        adapter = new SongAdapter(this, SongRepository.getInstance().getAll());
        listView.setAdapter(adapter);


        addButton.setOnClickListener(v -> showAddSongDialog());

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

    private void showAddSongDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_song, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Add Song and Genres");
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

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showArtistSelectionDialog(editName.getText().toString(), selectedGenres);
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

    private void showArtistSelectionDialog(String songName, ArrayList<Genre> selectedGenres) {
        ArrayList<Artist> artistList = ArtistRepository.getInstance().getAll();
        boolean[] checkedArtists = new boolean[artistList.size()];
        ArrayList<Artist> selectedArtists = new ArrayList<>();

        String[] artistNames = new String[artistList.size()];
        for (int i = 0; i < artistList.size(); i++) {
            artistNames[i] = artistList.get(i).getName();
        }

        AlertDialog.Builder artistBuilder = new AlertDialog.Builder(this);
        artistBuilder.setTitle("Select Artists");
        artistBuilder.setMultiChoiceItems(artistNames, checkedArtists, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedArtists.add(artistList.get(which));
                } else {
                    selectedArtists.remove(artistList.get(which));
                }
            }
        });

        artistBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Song newSong = new Song(UUID.randomUUID(), songName, selectedArtists, selectedGenres);
                SongRepository.getInstance().create(newSong);
                filterBySearch();
                dialog.dismiss();
            }
        });

        artistBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        artistBuilder.create().show();
    }


    public void showEditSongDialog(Song song) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_song, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Edit Song");
        EditText editName = dialogView.findViewById(R.id.editName);
        editName.setText(song.getName());

        ArrayList<Genre> genreList = GenreRepository.getInstance().getAll();
        boolean[] checkedGenres = new boolean[genreList.size()];
        ArrayList<Genre> selectedGenres = new ArrayList<>();
        ArrayList<Genre> selectedGenreList = song.getGenres();

        String[] genreNames = new String[genreList.size()];
        for (int i = 0; i < genreList.size(); i++) {
            genreNames[i] = genreList.get(i).getName();
        }
        for (int i = 0; i < genreList.size(); i++) {
            for (int j = 0; j < selectedGenreList.size(); j++) {
                if (genreList.get(i).getId().equals(selectedGenreList.get(j).getId())) {
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


        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showEditArtistSelectionDialog(song, editName.getText().toString(), selectedGenres);
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

    private void showEditArtistSelectionDialog(Song song, String songName, ArrayList<Genre> selectedGenres) {
        ArrayList<Artist> artistList = ArtistRepository.getInstance().getAll();
        boolean[] checkedArtists = new boolean[artistList.size()];
        ArrayList<Artist> selectedArtists = new ArrayList<>();
        ArrayList<Artist> alreadySelectedArtists = song.getArtists();

        String[] artistNames = new String[artistList.size()];
        for (int i = 0; i < artistList.size(); i++) {
            artistNames[i] = artistList.get(i).getName();
        }


        for (int i = 0; i < artistList.size(); i++) {
            for (int j = 0; j < alreadySelectedArtists.size(); j++) {
                if (artistList.get(i).getId().equals(alreadySelectedArtists.get(j).getId())) {
                    checkedArtists[i] = true;
                    selectedArtists.add(artistList.get(i));
                }
            }
        }

        AlertDialog.Builder artistBuilder = new AlertDialog.Builder(this);
        artistBuilder.setTitle("Select Artists");
        artistBuilder.setMultiChoiceItems(artistNames, checkedArtists, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedArtists.add(artistList.get(which));
                } else {
                    selectedArtists.remove(artistList.get(which));
                }
            }
        });

        artistBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SongRepository.getInstance().updateName(song.getId(), songName);
                GenreSongRepository.getInstance().replaceGenresInSong(song.getId(), selectedGenres);
                ArtistSongRepository.getInstance().replaceArtistsInSong(song.getId(), selectedArtists);

                filterBySearch();
                dialog.dismiss();
            }
        });

        artistBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        artistBuilder.create().show();
    }

    public void filterBySearch() {
        EditText searchText = findViewById(R.id.et_search);
        adapter.clear();
        adapter.addAll(SongRepository.getInstance().searchByNameOrGenreOrArtist(searchText.getText().toString()));
        adapter.notifyDataSetChanged();
    }
}


