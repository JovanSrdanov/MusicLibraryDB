package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Genre;


public class GenreArtistRepository {

    private static GenreArtistRepository instance = null;
    private final DBHelper dbHelper;

    private GenreArtistRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static GenreArtistRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new GenreArtistRepository(context);
        }
    }

    public boolean anyArtistHasGenre(UUID genreId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE_ARTIST, new String[]{DBHelper.COLUMN_GENRE_ARTIST_GENRE_ID},
                DBHelper.COLUMN_GENRE_ARTIST_GENRE_ID + "=?", new String[]{genreId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }


    public ArrayList<Genre> getAllGenresByArtistId(UUID artistId) {

        ArrayList<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE_ARTIST, new String[]{DBHelper.COLUMN_GENRE_ARTIST_GENRE_ID},
                DBHelper.COLUMN_GENRE_ARTIST_ARTIST_ID + "=?", new String[]{artistId.toString()}, null, null, null);
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

    public void deleteAllByArtistId(UUID artistId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DBHelper.TABLE_GENRE_ARTIST, DBHelper.COLUMN_GENRE_ARTIST_ARTIST_ID + "=?", new String[]{artistId.toString()});
        db.close();
    }

    public void replaceAllGenres(UUID artistId, ArrayList<Genre> genres) {
        deleteAllByArtistId(artistId);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (Genre genre : genres) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_GENRE_ARTIST_ARTIST_ID, artistId.toString());
            values.put(DBHelper.COLUMN_GENRE_ARTIST_GENRE_ID, genre.getId().toString());
            db.insert(DBHelper.TABLE_GENRE_ARTIST, null, values);
        }
        db.close();
    }


}
