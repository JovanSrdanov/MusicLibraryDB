package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.User;
import repository.repositoryInterface.ICRUDRepository;

public class UserRepository implements ICRUDRepository<User> {

    private static UserRepository instance = null;
    private final DBHelper dbHelper;

    private UserRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized UserRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
    }

    @Override
    public void create(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_ID, user.getId().toString());
        values.put(DBHelper.COLUMN_USER_USERNAME, user.getUsername());
        values.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
        db.insert(DBHelper.TABLE_USER, null, values);
        db.close();
    }

    @Override
    public ArrayList<User> getAll() {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_USER, null);

        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_PASSWORD));
                userList.add(new User(id, name, password));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }

    @Override
    public User getById(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER, new String[]{DBHelper.COLUMN_USER_ID, DBHelper.COLUMN_USER_USERNAME, DBHelper.COLUMN_USER_PASSWORD},
                DBHelper.COLUMN_USER_ID + "=?", new String[]{id.toString()}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_PASSWORD)));
            cursor.close();
            db.close();
            return user;
        }
        db.close();
        return null;
    }

    @Override
    public void updateName(UUID id, String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_USERNAME, username);
        db.update(DBHelper.TABLE_USER, values, DBHelper.COLUMN_USER_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    @Override
    public void delete(UUID id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_USER, DBHelper.COLUMN_USER_ID + "=?", new String[]{id.toString()});
        db.close();
        PlayListRepository.getInstance().deleteAllByUserId(id);
    }

    public User existsByUsernameAndPassword(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER,
                new String[]{DBHelper.COLUMN_USER_ID, DBHelper.COLUMN_USER_USERNAME, DBHelper.COLUMN_USER_PASSWORD},
                DBHelper.COLUMN_USER_USERNAME + "=? AND " + DBHelper.COLUMN_USER_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_PASSWORD)));
            cursor.close();
            db.close();
            return user;
        }
        db.close();
        return null;

    }

    public boolean isUsernameUnique(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USER,
                new String[]{DBHelper.COLUMN_USER_USERNAME},
                DBHelper.COLUMN_USER_USERNAME + "=?",
                new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        db.close();
        return true;
    }
}
