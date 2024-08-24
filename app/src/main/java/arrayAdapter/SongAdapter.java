package arrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

import activity.SongActivity;
import model.Artist;
import model.Genre;
import model.Song;
import repository.SongRepository;

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(Context context, List<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_song, parent, false);
        }

        Song song = getItem(position);
        if (song == null) {
            return convertView;
        }

        TextView title = convertView.findViewById(R.id.tv_song_title);
        TextView id = convertView.findViewById(R.id.tv_song_id);
        TextView genres = convertView.findViewById(R.id.tv_song_genres);
        TextView artists = convertView.findViewById(R.id.tv_song_artists);
        Button deleteButton = convertView.findViewById(R.id.btn_delete);
        Button editButton = convertView.findViewById(R.id.btn_edit);

        title.setText(song.getName());
        id.setText(song.getId().toString());
        StringBuilder genresSB = new StringBuilder("Genres:");
        for (Genre genre : song.getGenres()) {
            genresSB.append(" ").append(genre.getName());
            if (genre != song.getGenres().get(song.getGenres().size() - 1)) {
                genresSB.append(", ");
            }
        }
        genres.setText(genresSB.toString());
        StringBuilder artistsSB = new StringBuilder("Artists:");
        for (Artist artist : song.getArtists()) {
            artistsSB.append(" ").append(artist.getName());
            if (artist != song.getArtists().get(song.getArtists().size() - 1)) {
                artistsSB.append(", ");
            }
        }
        artists.setText(artistsSB.toString());

        deleteButton.setOnClickListener(v -> {
            SongRepository.getInstance().delete(song.getId());
            ((SongActivity) getContext()).filterBySearch();
        });

        editButton.setOnClickListener(v -> {
            ((SongActivity) getContext()).showEditSongDialog(song);
        });

        return convertView;
    }


}
