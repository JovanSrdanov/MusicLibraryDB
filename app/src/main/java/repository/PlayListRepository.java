package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.PlayList;
import repository.repositoryInterface.ICRUDRepository;

public class PlayListRepository implements ICRUDRepository<PlayList> {

    private static PlayListRepository instance = null;
    private final DBHelper dbHelper;

    private PlayListRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized PlayListRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new PlayListRepository(context);
        }
    }

    @Override
    public void create(PlayList t) {
        PlayListSongRepository.getInstance().replaceAllSongs(t.getId(), t.getSongs());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PLAYLIST_ID, t.getId().toString());
        values.put(DBHelper.COLUMN_PLAYLIST_NAME, t.getName());
        values.put(DBHelper.COLUMN_PLAYLIST_USER_ID, t.getUser().getId().toString());
        db.insert(DBHelper.TABLE_PLAYLIST, null, values);
        db.close();
    }

    @Override
    public ArrayList<PlayList> getAll() {
        ArrayList<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PLAYLIST, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_NAME));
                UUID userId = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_USER_ID)));
                playLists.add(new PlayList(id, name, UserRepository.getInstance().getById(userId), PlayListSongRepository.getInstance().getAllSongsForPlayList(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playLists;
    }

    @Override
    public PlayList getById(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_PLAYLIST, new String[]{DBHelper.COLUMN_PLAYLIST_ID, DBHelper.COLUMN_PLAYLIST_NAME, DBHelper.COLUMN_PLAYLIST_USER_ID},
                DBHelper.COLUMN_PLAYLIST_ID + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            PlayList playList = new PlayList(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_NAME)),
                    UserRepository.getInstance().getById(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_USER_ID)))), PlayListSongRepository.getInstance().getAllSongsForPlayList(id));
            cursor.close();
            db.close();
            return playList;
        }
        db.close();
        return null;
    }

    @Override
    public void updateName(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PLAYLIST_NAME, name);
        db.update(DBHelper.TABLE_PLAYLIST, values, DBHelper.COLUMN_PLAYLIST_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    @Override
    public void delete(UUID id) {
        PlayListSongRepository.getInstance().deleteAllByPlayListId(id);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_PLAYLIST, DBHelper.COLUMN_PLAYLIST_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    public ArrayList<PlayList> getAllByUserId(UUID userId) {
        ArrayList<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_PLAYLIST, new String[]{DBHelper.COLUMN_PLAYLIST_ID, DBHelper.COLUMN_PLAYLIST_NAME, DBHelper.COLUMN_PLAYLIST_USER_ID},
                DBHelper.COLUMN_PLAYLIST_USER_ID + "=?", new String[]{userId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_NAME));
                playLists.add(new PlayList(id, name, UserRepository.getInstance().getById(userId), PlayListSongRepository.getInstance().getAllSongsForPlayList(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playLists;
    }

    public void deleteAllByUserId(UUID userId) {
        ArrayList<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_PLAYLIST, new String[]{DBHelper.COLUMN_PLAYLIST_ID, DBHelper.COLUMN_PLAYLIST_NAME, DBHelper.COLUMN_PLAYLIST_USER_ID},
                DBHelper.COLUMN_PLAYLIST_USER_ID + "=?", new String[]{userId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_ID)));
                PlayListSongRepository.getInstance().deleteAllByPlayListId(id);
                db.delete(DBHelper.TABLE_PLAYLIST, DBHelper.COLUMN_PLAYLIST_ID + "=?", new String[]{id.toString()});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public ArrayList<PlayList> searchByName(String string, UUID userId) {
        ArrayList<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_PLAYLIST, new String[]{DBHelper.COLUMN_PLAYLIST_ID, DBHelper.COLUMN_PLAYLIST_NAME, DBHelper.COLUMN_PLAYLIST_USER_ID},
                DBHelper.COLUMN_PLAYLIST_NAME + " LIKE ? AND " + DBHelper.COLUMN_PLAYLIST_USER_ID + "=?", new String[]{"%" + string + "%", userId.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PLAYLIST_NAME));
                playLists.add(new PlayList(id, name, UserRepository.getInstance().getById(userId), PlayListSongRepository.getInstance().getAllSongsForPlayList(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playLists;
    }
}
