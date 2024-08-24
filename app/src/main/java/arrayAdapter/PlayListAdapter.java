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

import activity.PlaylistActivity;
import model.PlayList;
import repository.PlayListRepository;

public class PlayListAdapter extends ArrayAdapter<PlayList> {

    public PlayListAdapter(Context context, List<PlayList> playLists) {
        super(context, 0, playLists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_playlist, parent, false);
        }
        PlayList playList = getItem(position);
        if (playList == null) {
            return convertView;
        }

        TextView title = convertView.findViewById(R.id.tv_playlist_title);
        TextView id = convertView.findViewById(R.id.tv_playlist_id);
        TextView count = convertView.findViewById(R.id.tv_playlist_count);
        Button deleteButton = convertView.findViewById(R.id.btn_delete);
        Button editButton = convertView.findViewById(R.id.btn_edit);

        title.setText(playList.getName());
        id.setText(playList.getId().toString());
        count.setText(playList.getSongs().size() + " songs");

        deleteButton.setOnClickListener(v -> {
            PlayListRepository.getInstance().delete(playList.getId());
            ((PlaylistActivity) getContext()).filterBySearch();

        });

        editButton.setOnClickListener(v -> {
            ((PlaylistActivity) getContext()).showEditPlaylistDialog(playList);
        });

        return convertView;
    }

}
