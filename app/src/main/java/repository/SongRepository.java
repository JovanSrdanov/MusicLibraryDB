package repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import model.Artist;
import model.Genre;
import model.Song;
import repository.repositoryInterface.ICRUDRepository;

public class SongRepository implements ICRUDRepository<Song> {

    private static SongRepository instance = null;
    private final DBHelper dbHelper;

    private SongRepository(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static synchronized SongRepository getInstance() {
        return instance;
    }

    public static synchronized void setInstance(Context context) {
        if (instance == null) {
            instance = new SongRepository(context);
        }
    }

    @Override
    public void create(Song song) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SONG_ID, song.getId().toString());
        values.put(DBHelper.COLUMN_SONG_NAME, song.getName());
        db.insert(DBHelper.TABLE_SONG, null, values);
        db.close();
        ArtistSongRepository.getInstance().replaceArtistsInSong(song.getId(), song.getArtists());
        GenreSongRepository.getInstance().replaceGenresInSong(song.getId(), song.getGenres());

    }

    @Override
    public ArrayList<Song> getAll() {
        ArrayList<Song> songs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_SONG, null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_NAME));
                songs.add(new Song(id, name, ArtistSongRepository.getInstance().getAllArtistsBySongId(id), GenreSongRepository.getInstance().getAllGenresForSong(id)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    @Override
    public Song getById(UUID id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_SONG, new String[]{DBHelper.COLUMN_SONG_ID, DBHelper.COLUMN_SONG_NAME},
                DBHelper.COLUMN_SONG_ID + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Song song = new Song(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_NAME)), ArtistSongRepository.getInstance().getAllArtistsBySongId(id), GenreSongRepository.getInstance().getAllGenresForSong(id));
            cursor.close();
            db.close();
            return song;
        }
        db.close();
        return null;

    }

    @Override
    public void updateName(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SONG_NAME, name);
        db.update(DBHelper.TABLE_SONG, values, DBHelper.COLUMN_SONG_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    @Override
    public void delete(UUID id) {
        ArtistSongRepository.getInstance().deleteAllBySongId(id);
        GenreSongRepository.getInstance().deleteAllBySongId(id);
        PlayListSongRepository.getInstance().deleteAllBySongId(id);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_SONG, DBHelper.COLUMN_SONG_ID + "=?", new String[]{id.toString()});
        db.close();
    }


    public ArrayList<Song> searchByNameOrGenreOrArtist(String search) {
        ArrayList<Song> songs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchQuery = "%" + search + "%";

        String sqlQuery = "SELECT s." + DBHelper.COLUMN_SONG_ID + ", s." + DBHelper.COLUMN_SONG_NAME +
                " FROM " + DBHelper.TABLE_SONG + " s " +
                " LEFT JOIN " + DBHelper.TABLE_ARTIST_SONG + " asg ON s." + DBHelper.COLUMN_SONG_ID + " = asg." + DBHelper.COLUMN_ARTIST_SONG_SONG_ID +
                " LEFT JOIN " + DBHelper.TABLE_ARTIST + " a ON asg." + DBHelper.COLUMN_ARTIST_SONG_ARTIST_ID + " = a." + DBHelper.COLUMN_ARTIST_ID +
                " LEFT JOIN " + DBHelper.TABLE_GENRE_SONG + " gsg ON s." + DBHelper.COLUMN_SONG_ID + " = gsg." + DBHelper.COLUMN_GENRE_SONG_SONG_ID +
                " LEFT JOIN " + DBHelper.TABLE_GENRE + " g ON gsg." + DBHelper.COLUMN_GENRE_SONG_GENRE_ID + " = g." + DBHelper.COLUMN_GENRE_ID +
                " WHERE s." + DBHelper.COLUMN_SONG_NAME + " LIKE ? " +
                " OR a." + DBHelper.COLUMN_ARTIST_NAME + " LIKE ? " +
                " OR g." + DBHelper.COLUMN_GENRE_NAME + " LIKE ?" +
                " GROUP BY s." + DBHelper.COLUMN_SONG_ID;

        Cursor cursor = db.rawQuery(sqlQuery, new String[]{searchQuery, searchQuery, searchQuery});

        if (cursor.moveToFirst()) {
            do {
                UUID songId = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_ID)));
                String songName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SONG_NAME));
                ArrayList<Artist> artists = ArtistSongRepository.getInstance().getAllArtistsBySongId(songId);
                ArrayList<Genre> genres = GenreSongRepository.getInstance().getAllGenresBySongId(songId);
                songs.add(new Song(songId, songName, artists, genres));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }


}
