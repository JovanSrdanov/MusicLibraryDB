package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Artist;

public class ArtistSongRepository {
    private static ArtistSongRepository instance = null;
    private final DBHelper dbHelper;

    private ArtistSongRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized ArtistSongRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new ArtistSongRepository(context);
        }
    }


    public void deleteAllBySongId(UUID songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DBHelper.TABLE_ARTIST_SONG, DBHelper.COLUMN_ARTIST_SONG_SONG_ID + "=?", new String[]{songId.toString()});
        db.close();
    }

    public void replaceArtistsInSong(UUID id, ArrayList<Artist> artists) {
        deleteAllBySongId(id);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (Artist artist : artists) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_ARTIST_SONG_SONG_ID, id.toString());
            values.put(DBHelper.COLUMN_ARTIST_SONG_ARTIST_ID, artist.getId().toString());
            db.insert(DBHelper.TABLE_ARTIST_SONG, null, values);
        }
        db.close();
    }

    public ArrayList<Artist> getAllArtistsBySongId(UUID id) {
        ArrayList<Artist> artists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_ARTIST_SONG, new String[]{DBHelper.COLUMN_ARTIST_SONG_ARTIST_ID},
                DBHelper.COLUMN_ARTIST_SONG_SONG_ID + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID artistId = UUID.fromString(cursor.getString(0));
                Artist artist = ArtistRepository.getInstance().getById(artistId);
                artists.add(artist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artists;
    }

    public boolean anyArtistHasSong(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_ARTIST_SONG, new String[]{DBHelper.COLUMN_ARTIST_SONG_SONG_ID},
                DBHelper.COLUMN_ARTIST_SONG_ARTIST_ID + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
}
