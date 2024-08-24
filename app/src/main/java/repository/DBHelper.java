package repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    public static final String TABLE_GENRE = "genres";
    public static final String COLUMN_GENRE_ID = "id";
    public static final String COLUMN_GENRE_NAME = "name";

    public static final String TABLE_ARTIST = "artists";
    public static final String COLUMN_ARTIST_ID = "id";
    public static final String COLUMN_ARTIST_NAME = "name";

    public static final String TABLE_SONG = "songs";
    public static final String COLUMN_SONG_ID = "id";
    public static final String COLUMN_SONG_NAME = "name";


    public static final String TABLE_PLAYLIST = "playlists";
    public static final String COLUMN_PLAYLIST_ID = "id";
    public static final String COLUMN_PLAYLIST_NAME = "name";
    public static final String COLUMN_PLAYLIST_USER_ID = "user_id";

    public static final String TABLE_ARTIST_SONG = "artist_song";
    public static final String COLUMN_ARTIST_SONG_ARTIST_ID = "artist_id";
    public static final String COLUMN_ARTIST_SONG_SONG_ID = "song_id";

    public static final String TABLE_GENRE_ARTIST = "genre_artist";
    public static final String COLUMN_GENRE_ARTIST_GENRE_ID = "genre_id";
    public static final String COLUMN_GENRE_ARTIST_ARTIST_ID = "artist_id";

    public static final String TABLE_GENRE_SONG = "genre_song";
    public static final String COLUMN_GENRE_SONG_GENRE_ID = "genre_id";
    public static final String COLUMN_GENRE_SONG_SONG_ID = "song_id";

    public static final String TABLE_PLAYLIST_SONG = "playlist_song";
    public static final String COLUMN_PLAYLIST_SONG_PLAYLIST_ID = "playlist_id";
    public static final String COLUMN_PLAYLIST_SONG_SONG_ID = "song_id";


    private static final String DATABASE_NAME = "my_music_library_db";
    private static final int DATABASE_VERSION = 23122;
    private static DBHelper instance = null;


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static void CreateTableGenre(SQLiteDatabase db) {
        String CREATE_GENRE_TABLE = "CREATE TABLE " + TABLE_GENRE + "("
                + COLUMN_GENRE_ID + " TEXT PRIMARY KEY,"
                + COLUMN_GENRE_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_GENRE_TABLE);
    }

    private static void CreateTableUsers(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY,"
                + COLUMN_USER_USERNAME + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    private static void CreateTableArtists(SQLiteDatabase db) {
        String CREATE_ARTIST_TABLE = "CREATE TABLE " + TABLE_ARTIST + "("
                + COLUMN_ARTIST_ID + " TEXT PRIMARY KEY,"
                + COLUMN_ARTIST_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_ARTIST_TABLE);
    }

    private static void CreateTableSongs(SQLiteDatabase db) {
        String CREATE_SONG_TABLE = "CREATE TABLE " + TABLE_SONG + "("
                + COLUMN_SONG_ID + " TEXT PRIMARY KEY,"
                + COLUMN_SONG_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_SONG_TABLE);
    }

    private static void CreateTablePlaylists(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + COLUMN_PLAYLIST_ID + " TEXT PRIMARY KEY,"
                + COLUMN_PLAYLIST_NAME + " TEXT,"
                + COLUMN_PLAYLIST_USER_ID + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_PLAYLIST_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }

    private static void CreateTableArtistSong(SQLiteDatabase db) {
        String CREATE_ARTIST_SONG_TABLE = "CREATE TABLE " + TABLE_ARTIST_SONG + "("
                + COLUMN_ARTIST_SONG_ARTIST_ID + " TEXT,"
                + COLUMN_ARTIST_SONG_SONG_ID + " TEXT,"
                + "PRIMARY KEY(" + COLUMN_ARTIST_SONG_ARTIST_ID + ", " + COLUMN_ARTIST_SONG_SONG_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ARTIST_SONG_ARTIST_ID + ") REFERENCES " + TABLE_ARTIST + "(" + COLUMN_ARTIST_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ARTIST_SONG_SONG_ID + ") REFERENCES " + TABLE_SONG + "(" + COLUMN_SONG_ID + ")"
                + ")";
        db.execSQL(CREATE_ARTIST_SONG_TABLE);
    }

    private static void CreateTableGenreArtist(SQLiteDatabase db) {
        String CREATE_GENRE_ARTIST_TABLE = "CREATE TABLE " + TABLE_GENRE_ARTIST + "("
                + COLUMN_GENRE_ARTIST_GENRE_ID + " TEXT,"
                + COLUMN_GENRE_ARTIST_ARTIST_ID + " TEXT,"
                + "PRIMARY KEY(" + COLUMN_GENRE_ARTIST_GENRE_ID + ", " + COLUMN_GENRE_ARTIST_ARTIST_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GENRE_ARTIST_GENRE_ID + ") REFERENCES " + TABLE_GENRE + "(" + COLUMN_GENRE_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GENRE_ARTIST_ARTIST_ID + ") REFERENCES " + TABLE_ARTIST + "(" + COLUMN_ARTIST_ID + ")"
                + ")";
        db.execSQL(CREATE_GENRE_ARTIST_TABLE);
    }

    private static void CreateTableGenreSong(SQLiteDatabase db) {
        String CREATE_GENRE_SONG_TABLE = "CREATE TABLE " + TABLE_GENRE_SONG + "("
                + COLUMN_GENRE_SONG_GENRE_ID + " TEXT,"
                + COLUMN_GENRE_SONG_SONG_ID + " TEXT,"
                + "PRIMARY KEY(" + COLUMN_GENRE_SONG_GENRE_ID + ", " + COLUMN_GENRE_SONG_SONG_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GENRE_SONG_GENRE_ID + ") REFERENCES " + TABLE_GENRE + "(" + COLUMN_GENRE_ID + "),"
                + "FOREIGN KEY(" + COLUMN_GENRE_SONG_SONG_ID + ") REFERENCES " + TABLE_SONG + "(" + COLUMN_SONG_ID + ")"
                + ")";
        db.execSQL(CREATE_GENRE_SONG_TABLE);
    }

    private static void CreateTablePlaylistSong(SQLiteDatabase db) {
        String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE " + TABLE_PLAYLIST_SONG + "("
                + COLUMN_PLAYLIST_SONG_PLAYLIST_ID + " TEXT,"
                + COLUMN_PLAYLIST_SONG_SONG_ID + " TEXT,"
                + "PRIMARY KEY(" + COLUMN_PLAYLIST_SONG_PLAYLIST_ID + ", " + COLUMN_PLAYLIST_SONG_SONG_ID + "),"
                + "FOREIGN KEY(" + COLUMN_PLAYLIST_SONG_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLIST + "(" + COLUMN_PLAYLIST_ID + "),"
                + "FOREIGN KEY(" + COLUMN_PLAYLIST_SONG_SONG_ID + ") REFERENCES " + TABLE_SONG + "(" + COLUMN_SONG_ID + ")"
                + ")";
        db.execSQL(CREATE_PLAYLIST_SONG_TABLE);
    }


    private static void dropDatabases(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE_ARTIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONG);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateTableUsers(db);
        CreateTableGenre(db);
        CreateTableArtists(db);
        CreateTableSongs(db);
        CreateTablePlaylists(db);
        CreateTableArtistSong(db);
        CreateTableGenreArtist(db);
        CreateTableGenreSong(db);
        CreateTablePlaylistSong(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropDatabases(db);
        onCreate(db);
    }

    public void resetWholeDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        dropDatabases(db);
        onCreate(db);
    }

}
