package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Genre;

public class GenreSongRepository {

    private static GenreSongRepository instance = null;
    private final DBHelper dbHelper;

    private GenreSongRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized GenreSongRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new GenreSongRepository(context);
        }
    }

    public ArrayList<Genre> getAllGenresForSong(UUID songId) {
        ArrayList<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE_SONG, new String[]{DBHelper.COLUMN_GENRE_SONG_GENRE_ID},
                DBHelper.COLUMN_GENRE_SONG_SONG_ID + "=?", new String[]{songId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID genreId = UUID.fromString(cursor.getString(0));
                Genre genre = GenreRepository.getInstance().getById(genreId);
                genres.add(genre);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return genres;
    }

    public void deleteAllBySongId(UUID songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DBHelper.TABLE_GENRE_SONG, DBHelper.COLUMN_GENRE_SONG_SONG_ID + "=?", new String[]{songId.toString()});
        db.close();
    }

    public void replaceGenresInSong(UUID songId, ArrayList<Genre> genres) {
        deleteAllBySongId(songId);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (Genre genre : genres) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_GENRE_SONG_SONG_ID, songId.toString());
            values.put(DBHelper.COLUMN_GENRE_SONG_GENRE_ID, genre.getId().toString());
            db.insert(DBHelper.TABLE_GENRE_SONG, null, values);
        }
        db.close();
    }


    public boolean anySongHasGenre(UUID genreId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE_SONG, new String[]{DBHelper.COLUMN_GENRE_SONG_SONG_ID},
                DBHelper.COLUMN_GENRE_SONG_GENRE_ID + "=?", new String[]{genreId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();

        return false;
    }

    public ArrayList<Genre> getAllGenresBySongId(UUID songId) {
        ArrayList<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE_SONG, new String[]{DBHelper.COLUMN_GENRE_SONG_GENRE_ID},
                DBHelper.COLUMN_GENRE_SONG_SONG_ID + "=?", new String[]{songId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID genreId = UUID.fromString(cursor.getString(0));
                Genre genre = GenreRepository.getInstance().getById(genreId);
                genres.add(genre);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return genres;
    }
}
