package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Song;

public class PlayListSongRepository {

    private static PlayListSongRepository instance = null;
    private final DBHelper dbHelper;

    private PlayListSongRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized PlayListSongRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new PlayListSongRepository(context);
        }
    }

    public void replaceAllSongs(UUID playListId, ArrayList<Song> songs) {
        deleteAllByPlayListId(playListId);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (Song song : songs) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_PLAYLIST_SONG_PLAYLIST_ID, playListId.toString());
            values.put(DBHelper.COLUMN_PLAYLIST_SONG_SONG_ID, song.getId().toString());
            db.insert(DBHelper.TABLE_PLAYLIST_SONG, null, values);
        }
        db.close();
    }

    public ArrayList<Song> getAllSongsForPlayList(UUID playListId) {
        ArrayList<Song> songs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_PLAYLIST_SONG, new String[]{DBHelper.COLUMN_PLAYLIST_SONG_SONG_ID},
                DBHelper.COLUMN_PLAYLIST_SONG_PLAYLIST_ID + "=?", new String[]{playListId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID songId = UUID.fromString(cursor.getString(0));
                Song song = SongRepository.getInstance().getById(songId);
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public void deleteAllByPlayListId(UUID playListId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DBHelper.TABLE_PLAYLIST_SONG, DBHelper.COLUMN_PLAYLIST_SONG_PLAYLIST_ID + "=?", new String[]{playListId.toString()});
        db.close();
    }

    public void deleteAllBySongId(UUID songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DBHelper.TABLE_PLAYLIST_SONG, DBHelper.COLUMN_PLAYLIST_SONG_SONG_ID + "=?", new String[]{songId.toString()});
        db.close();
    }

}
