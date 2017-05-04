package riviasoftware.popular_movies;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import riviasoftware.popular_movies.constants.PopularMoviesConstants;
import riviasoftware.popular_movies.data.Movie;
import riviasoftware.popular_movies.data.MoviesResponse;
import riviasoftware.popular_movies.database.DBContract;
import riviasoftware.popular_movies.database.DBProvider;
import riviasoftware.popular_movies.retrofit.services.TMVDatabaseService;
import riviasoftware.popular_movies.retrofit.utils.ApiUtils;


public class MainActivityFragment extends Fragment {


    @BindView(R.id.recyclerviewmain)
    RecyclerView mRecyclerView;
    private GridAdapter adapter;
    private TMVDatabaseService tmvDatabaseService;
    private Unbinder unbinder;
    private List<Movie> movies;
    private static final int TYPE_TOP_RATED = 1;
    private static final int TYPE_POPULAR = 2;
    private String toolbarName;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movies = new ArrayList<Movie>();
        } else {
            movies = savedInstanceState.getParcelableArrayList("movies");
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList) movies);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        tmvDatabaseService = ApiUtils.getTMVDataService();
        adapter = new GridAdapter(getActivity(), (ArrayList) movies);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie item = (Movie) v.getTag();
                Intent intent = new Intent(getActivity().getApplicationContext(), ResumeMovieActivity.class);
                intent.putExtra("movieId", item.getId());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(adapter);
        GridLayoutManager mGridVerticalLayoutManager;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridVerticalLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4, GridLayoutManager.VERTICAL, false);
        } else {
            mGridVerticalLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mGridVerticalLayoutManager);


        if (movies.isEmpty()) {
            toolbarName = getString(R.string.top_rated);
            loadMovies(TYPE_TOP_RATED);
        } else {
            adapter.updateMovies(movies);
        }


        return view;
    }

    public void changeToolBarName(){
        getActivity().setTitle(toolbarName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case R.id.top_rated:
                toolbarName = getString(R.string.top_rated);
                loadMovies(TYPE_TOP_RATED);
                break;
            case R.id.shorting_popular:
                toolbarName = getString(R.string.popular);
                loadMovies(TYPE_POPULAR);
                break;
            case R.id.favorites:
                toolbarName = getString(R.string.favorites);
                loadFavorites();
                break;
            default:
                toolbarName = getString(R.string.top_rated);
                loadMovies(TYPE_TOP_RATED);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void loadMovies(int type) {

        Call<MoviesResponse> response = null;
        if (type == TYPE_TOP_RATED) {
            response = tmvDatabaseService.getTopRatedMovies(PopularMoviesConstants.apiKEY);
        } else if (type == TYPE_POPULAR) {
            response = tmvDatabaseService.getPopularMovies(PopularMoviesConstants.apiKEY);
        }

            response.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        movies = response.body().getResults();
                        adapter.updateMovies(movies);
                        Log.d("MainActivity", "posts loaded from API");
                    } else {
                        int statusCode = response.code();
                        Log.d("MainActivity", "error loading from API status code: " + statusCode);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("MainActivity", t.getMessage());
                }
            });
        changeToolBarName();
        }



    public void loadFavorites() {

        String[] projection = new String[]{
                DBContract.DBEntry._ID,
                DBContract.DBEntry.COLUMN_MOVIE_ID,
                DBContract.DBEntry.COLUMN_MOVIE_NAME,
                DBContract.DBEntry.COLUMN_MOVIE_URL_IMAGE
        };

        Uri favUri = DBProvider.CONTENT_URI;
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(favUri, projection, null, null, null);

        movies = new ArrayList<Movie>();
        if (cur.moveToFirst()) {

            do {
                Movie movie = new Movie();
                int colMovieID = cur.getColumnIndex(DBContract.DBEntry.COLUMN_MOVIE_ID);
                int colMovieName = cur.getColumnIndex(DBContract.DBEntry.COLUMN_MOVIE_NAME);
                int colURLImage = cur.getColumnIndex(DBContract.DBEntry.COLUMN_MOVIE_URL_IMAGE);
                movie.setId(cur.getInt(colMovieID));
                movie.setTitle(cur.getString(colMovieName));
                movie.setPosterPath(cur.getString(colURLImage));
                movies.add(movie);

            } while (cur.moveToNext());

        }
        adapter.updateMovies(movies);
        changeToolBarName();
    }


}
