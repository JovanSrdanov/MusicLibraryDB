package arrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.List;

import activity.ArtistActivity;
import model.Artist;
import model.Genre;
import repository.ArtistRepository;
import repository.ArtistSongRepository;

public class ArtistAdapter extends ArrayAdapter<Artist> {

    public ArtistAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        Artist artist = getItem(position);
        if (artist == null) {
            return convertView;
        }

        TextView title = convertView.findViewById(R.id.tv_artist_title);
        TextView id = convertView.findViewById(R.id.tv_artist_id);
        TextView genres = convertView.findViewById(R.id.tv_artist_genres);
        Button deleteButton = convertView.findViewById(R.id.btn_delete);
        Button editButton = convertView.findViewById(R.id.btn_edit);

        title.setText(artist.getName());
        id.setText(artist.getId().toString());

        StringBuilder genresSB = new StringBuilder("Genres:");
        for (Genre genre : artist.getGenres()) {
            genresSB.append(" ").append(genre.getName());
            if (genre != artist.getGenres().get(artist.getGenres().size() - 1)) {
                genresSB.append(", ");
            }
        }
        genres.setText(genresSB.toString());

        deleteButton.setOnClickListener(v -> {
            if (ArtistSongRepository.getInstance().anyArtistHasSong(artist.getId())) {
                Toast.makeText(getContext(), "Can't delete artist with songs", Toast.LENGTH_SHORT).show();
                return;
            }
            ArtistRepository.getInstance().delete(artist.getId());
            ((ArtistActivity) getContext()).filterBySearch();
        });

        editButton.setOnClickListener(v -> {
            ((ArtistActivity) getContext()).showEditArtistDialog(artist);
        });

        return convertView;
    }


}
