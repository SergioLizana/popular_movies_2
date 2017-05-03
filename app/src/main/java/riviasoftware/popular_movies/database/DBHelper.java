package riviasoftware.popular_movies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.sql.Timestamp;

/**
 * Created by sergiolizanamontero on 19/4/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="movies.db";
    private static final int DATABASE_VERSION = 3;




    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIEDB_TABLE = "CREATE TABLE "+
                DBContract.DBEntry.TABLE_NAME + " ("+
                DBContract.DBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DBContract.DBEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"+
                DBContract.DBEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL,"+
                DBContract.DBEntry.COLUMN_MOVIE_URL_IMAGE + " TEXT NOT NULL,"+
                DBContract.DBEntry.COLUMN_MOVIE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+
                ");";
        Log.d("DBHELPER","CREATING DB");

        db.execSQL(SQL_CREATE_MOVIEDB_TABLE);
        Log.d("DBHELPER","DB CREATED!!!!");





    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ DBContract.DBEntry.TABLE_NAME);
        onCreate(db);


    }
}
