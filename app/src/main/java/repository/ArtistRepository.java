package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Artist;
import repository.repositoryInterface.ICRUDRepository;

public class ArtistRepository implements ICRUDRepository<Artist> {

    private static ArtistRepository instance = null;
    private final DBHelper dbHelper;

    private ArtistRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized ArtistRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new ArtistRepository(context);
        }
    }

    @Override
    public void create(Artist artist) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ARTIST_ID, artist.getId().toString());
        values.put(DBHelper.COLUMN_ARTIST_NAME, artist.getName());
        db.insert(DBHelper.TABLE_ARTIST, null, values);
        db.close();
        GenreArtistRepository.getInstance().replaceAllGenres(artist.getId(), artist.getGenres());
    }

    @Override
    public ArrayList<Artist> getAll() {
        ArrayList<Artist> artists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_ARTIST, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ARTIST_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ARTIST_NAME));
                artists.add(new Artist(id, name, GenreArtistRepository.getInstance().getAllGenresByArtistId(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artists;
    }

    @Override
    public Artist getById(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_ARTIST, new String[]{DBHelper.COLUMN_ARTIST_ID, DBHelper.COLUMN_ARTIST_NAME},
                DBHelper.COLUMN_ARTIST_ID + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ARTIST_NAME));
            cursor.close();
            db.close();
            return new Artist(id, name, GenreArtistRepository.getInstance().getAllGenresByArtistId(id));
        }
        cursor.close();
        db.close();
        return null;
    }

    @Override
    public void updateName(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ARTIST_NAME, name);
        db.update(DBHelper.TABLE_ARTIST, values, DBHelper.COLUMN_ARTIST_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    @Override
    public void delete(UUID id) {
        GenreArtistRepository.getInstance().deleteAllByArtistId(id);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_ARTIST, DBHelper.COLUMN_ARTIST_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    public List<Artist> searchByNameOrGenre(String search) {
        List<Artist> filteredArtists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT DISTINCT a." + DBHelper.COLUMN_ARTIST_ID + ", a." + DBHelper.COLUMN_ARTIST_NAME +
                " FROM " + DBHelper.TABLE_ARTIST + " a" +
                " LEFT JOIN " + DBHelper.TABLE_GENRE_ARTIST + " ga" +
                " ON a." + DBHelper.COLUMN_ARTIST_ID + " = ga." + DBHelper.COLUMN_GENRE_ARTIST_ARTIST_ID +
                " LEFT JOIN " + DBHelper.TABLE_GENRE + " g" +
                " ON ga." + DBHelper.COLUMN_GENRE_ARTIST_GENRE_ID + " = g." + DBHelper.COLUMN_GENRE_ID +
                " WHERE a." + DBHelper.COLUMN_ARTIST_NAME + " LIKE ? OR g." + DBHelper.COLUMN_GENRE_NAME + " LIKE ?";


        String[] args = new String[]{"%" + search + "%", "%" + search + "%"};
        Cursor cursor = db.rawQuery(query, args);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ARTIST_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ARTIST_NAME));
                filteredArtists.add(new Artist(id, name, GenreArtistRepository.getInstance().getAllGenresByArtistId(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return filteredArtists;
    }
}
