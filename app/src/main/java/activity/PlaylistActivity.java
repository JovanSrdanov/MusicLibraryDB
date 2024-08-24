package activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.UUID;

import arrayAdapter.PlayListAdapter;
import model.PlayList;
import model.Song;
import repository.PlayListRepository;
import repository.PlayListSongRepository;
import repository.SongRepository;
import service.AuthorizationAndAuthentication;

public class PlaylistActivity extends BaseDrawerActivity {
    private PlayListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_playlist);
        ListView listView = findViewById(R.id.list_view);
        Button addButton = findViewById(R.id.button);
        EditText searchText = findViewById(R.id.et_search);
        UUID userId = AuthorizationAndAuthentication.getInstance().getLoggedInUser().getId();
        adapter = new PlayListAdapter(this, PlayListRepository.getInstance().getAllByUserId(userId));
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddPlaylistDialog());

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

    private void showAddPlaylistDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_playlist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Playlist");
        builder.setView(dialogView);
        EditText editName = dialogView.findViewById(R.id.editName);
        ArrayList<Song> songs = SongRepository.getInstance().getAll();
        boolean[] selectedSongs = new boolean[songs.size()];
        ArrayList<Song> selectedSongList = new ArrayList<>();

        StringBuilder[] songNames = new StringBuilder[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            songNames[i] = new StringBuilder(songs.get(i).getName() + ": ");
            for (int j = 0; j < songs.get(i).getArtists().size(); j++) {
                songNames[i].append(songs.get(i).getArtists().get(j).getName()).append(" ");
                if (j != songs.get(i).getArtists().size() - 1) {
                    songNames[i].append(", ");
                }
            }
        }

        builder.setMultiChoiceItems(songNames, selectedSongs, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedSongList.add(songs.get(which));
                } else {
                    selectedSongList.remove(songs.get(which));
                }
            }
        });


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editName.getText().toString();
                PlayListRepository.getInstance().create(new PlayList(UUID.randomUUID(), name, AuthorizationAndAuthentication.getInstance().getLoggedInUser(), selectedSongList));
                filterBySearch();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    public void showEditPlaylistDialog(PlayList playlist) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form_playlist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Playlist");
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);
        editName.setText(playlist.getName());

        ArrayList<Song> songs = SongRepository.getInstance().getAll();
        boolean[] selectedSongs = new boolean[songs.size()];
        ArrayList<Song> selectedSongList = new ArrayList<>();
        ArrayList<Song> alreadySelected = playlist.getSongs();


        StringBuilder[] songNames = new StringBuilder[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            songNames[i] = new StringBuilder(songs.get(i).getName() + ": ");
            for (int j = 0; j < songs.get(i).getArtists().size(); j++) {
                songNames[i].append(songs.get(i).getArtists().get(j).getName()).append(" ");
                if (j != songs.get(i).getArtists().size() - 1) {
                    songNames[i].append(", ");
                }
            }
        }

        for (int i = 0; i < songs.size(); i++) {
            for (int j = 0; j < alreadySelected.size(); j++) {
                if (songs.get(i).getId().equals(alreadySelected.get(j).getId())) {
                    selectedSongs[i] = true;
                    selectedSongList.add(songs.get(i));
                }
            }
        }
        builder.setMultiChoiceItems(songNames, selectedSongs, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedSongList.add(songs.get(which));
                } else {
                    selectedSongList.remove(songs.get(which));
                }
            }
        });


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editName.getText().toString();
                PlayListRepository.getInstance().updateName(playlist.getId(), name);
                PlayListSongRepository.getInstance().replaceAllSongs(playlist.getId(), selectedSongList);
                filterBySearch();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }


    public void filterBySearch() {
        EditText searchText = findViewById(R.id.et_search);
        adapter.clear();
        adapter.addAll(PlayListRepository.getInstance().searchByName(searchText.getText().toString(), AuthorizationAndAuthentication.getInstance().getLoggedInUser().getId()));
        adapter.notifyDataSetChanged();
    }
}