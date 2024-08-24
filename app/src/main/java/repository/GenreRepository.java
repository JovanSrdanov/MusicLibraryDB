package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Genre;
import repository.repositoryInterface.ICRUDRepository;

public class GenreRepository implements ICRUDRepository<Genre> {

    private static GenreRepository instance = null;
    private final DBHelper dbHelper;

    private GenreRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized GenreRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new GenreRepository(context);
        }
    }

    @Override
    public void create(Genre genre) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_GENRE_ID, genre.getId().toString());
        values.put(DBHelper.COLUMN_GENRE_NAME, genre.getName());
        db.insert(DBHelper.TABLE_GENRE, null, values);
        db.close();
    }

    @Override
    public ArrayList<Genre> getAll() {
        ArrayList<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_GENRE, null);

        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_NAME));
                genres.add(new Genre(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return genres;
    }

    @Override
    public Genre getById(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE, new String[]{DBHelper.COLUMN_GENRE_ID, DBHelper.COLUMN_GENRE_NAME},
                DBHelper.COLUMN_GENRE_ID + "=?", new String[]{id.toString()}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Genre genre = new Genre(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_NAME)));
            cursor.close();
            db.close();
            return genre;
        }
        db.close();
        return null;
    }

    @Override
    public void updateName(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_GENRE_NAME, name);
        db.update(DBHelper.TABLE_GENRE, values, DBHelper.COLUMN_GENRE_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    @Override
    public void delete(UUID id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper
                .TABLE_GENRE, DBHelper
                .COLUMN_GENRE_ID + "=?", new String[]{id.toString()});
    }

    public ArrayList<Genre> searchByName(String name) {
        ArrayList<Genre> genres = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_GENRE, new String[]{DBHelper.COLUMN_GENRE_ID, DBHelper.COLUMN_GENRE_NAME},
                DBHelper.COLUMN_GENRE_NAME + " LIKE ?", new String[]{"%" + name + "%"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_ID)));
                String nameGenre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE_NAME));
                genres.add(new Genre(id, nameGenre));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return genres;
    }
}