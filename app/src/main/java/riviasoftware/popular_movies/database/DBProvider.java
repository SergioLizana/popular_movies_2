package riviasoftware.popular_movies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by sergiolizanamontero on 19/4/17.
 */

public class DBProvider extends ContentProvider {
    public static final String uri = "content://riviasoftware.popular_movies.database/dbentry";
    public static final  Uri CONTENT_URI = Uri.parse(uri);

    private static final int FAVORITOS = 1;
    private static final int FAVORITOS_ID = 2;
    private static  UriMatcher uriMatcher = null;

    private DBHelper dbHelper;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("riviasoftware.popular_movies.database","dbentry",FAVORITOS);
        uriMatcher.addURI("riviasoftware.popular_movies.database","dbentry/#",FAVORITOS_ID);
    }




    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String where = selection;
        if(uriMatcher.match(uri) == FAVORITOS_ID){
            where = DBContract.DBEntry.COLUMN_MOVIE_ID+"=" + uri.getLastPathSegment();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBContract.DBEntry.TABLE_NAME,projection,where,selectionArgs,null,null,sortOrder);
        return c;


    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long regId = 1;


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        regId = db.insert(DBContract.DBEntry.TABLE_NAME,null,values);
        Uri newUri = ContentUris.withAppendedId(CONTENT_URI,regId);
        return newUri;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int cont;
        String where = selection;
        if(uriMatcher.match(uri) == FAVORITOS_ID){
            where = DBContract.DBEntry.COLUMN_MOVIE_ID+"=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cont = db.update(DBContract.DBEntry.TABLE_NAME,values,where,selectionArgs);
        return cont;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {


        int cont;
        String where = selection;
        if(uriMatcher.match(uri) == FAVORITOS_ID){
            where = DBContract.DBEntry.COLUMN_MOVIE_ID+"=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cont = db.delete(DBContract.DBEntry.TABLE_NAME,where,selectionArgs);


        return cont;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match){
            case FAVORITOS:
                return "vnd.android.cursor.dir/vnd.rivia_software.dbentry";
            case FAVORITOS_ID:
                return "vnd.android.cursor.item/vnd.rivia_software.dbentry";
            default:
                return null;
        }

    }
}
