package riviasoftware.popular_movies.database;

import android.provider.BaseColumns;

/**
 * Created by sergiolizanamontero on 19/4/17.
 */

public class DBContract{


    public static final class DBEntry implements BaseColumns{


        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_NAME = "movie_name";
        public static final String COLUMN_MOVIE_URL_IMAGE = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_TIMESTAMP = "timestamp";




    }


}
