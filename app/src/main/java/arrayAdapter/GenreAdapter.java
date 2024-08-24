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

import activity.GenreActivity;
import model.Genre;
import repository.GenreArtistRepository;
import repository.GenreRepository;
import repository.GenreSongRepository;

public class GenreAdapter extends ArrayAdapter<Genre> {

    public GenreAdapter(Context context, List<Genre> genres) {
        super(context, 0, genres);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_genre, parent, false);
        }

        Genre genre = getItem(position);
        if (genre == null) {
            return convertView;
        }

        TextView title = convertView.findViewById(R.id.tv_genre_title);
        TextView id = convertView.findViewById(R.id.tv_genre_id);
        Button deleteButton = convertView.findViewById(R.id.btn_delete);
        Button editButton = convertView.findViewById(R.id.btn_edit);

        title.setText(genre.getName());
        id.setText(genre.getId().toString());

        deleteButton.setOnClickListener(v -> {
            if (GenreArtistRepository.getInstance().anyArtistHasGenre(genre.getId())) {
                Toast.makeText(this.getContext(), "Genre is used in an artist, can't delete!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (GenreSongRepository.getInstance().anySongHasGenre(genre.getId())) {
                Toast.makeText(this.getContext(), "Genre is used in a song, can't delete!", Toast.LENGTH_SHORT).show();
                return;
            }

            GenreRepository.getInstance().delete(genre.getId());
            ((GenreActivity) getContext()).filterBySearch();
        });

        editButton.setOnClickListener(v -> {


            ((GenreActivity) getContext()).showEditGenreDialog(genre);
        });

        return convertView;
    }


}
