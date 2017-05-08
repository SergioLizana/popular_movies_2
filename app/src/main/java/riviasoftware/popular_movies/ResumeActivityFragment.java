package riviasoftware.popular_movies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import riviasoftware.popular_movies.animation.LikeButtonView;
import riviasoftware.popular_movies.constants.PopularMoviesConstants;
import riviasoftware.popular_movies.data.Movie;
import riviasoftware.popular_movies.data.Trailer;
import riviasoftware.popular_movies.data.TrailerResponse;
import riviasoftware.popular_movies.data.Review;
import riviasoftware.popular_movies.database.DBContract;
import riviasoftware.popular_movies.database.DBProvider;
import riviasoftware.popular_movies.retrofit.services.TMVDatabaseService;
import riviasoftware.popular_movies.retrofit.utils.ApiUtils;



public class ResumeActivityFragment extends Fragment {


    @BindView(R.id.image_photo_poster)
    ImageView poster;
    @BindView(R.id.trailer)
    ImageView trailer;
    @BindView(R.id.play_trailer)
    ImageButton playTrailer;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.puntuacion)
    TextView puntuacion;
    @BindView(R.id.overview)
    TextView overview;

    @BindView(R.id.fav_button)
    LikeButtonView likeButtonView;


    private TMVDatabaseService tmvDatabaseService;
    private int movieId;
    private Movie movie;
    private Unbinder unbinder;
    private LayoutInflater inflaterLayout;


    public ResumeActivityFragment() {

    }
    public static ResumeActivityFragment newInstance() {
        ResumeActivityFragment fragment = new ResumeActivityFragment();
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_resume_movie, container, false);
        unbinder = ButterKnife.bind(this, view);
        inflaterLayout = LayoutInflater.from(getContext());
        movieId = getActivity().getIntent().getIntExtra("movieId", 0);
        tmvDatabaseService = ApiUtils.getTMVDataService();
        getMovie();


        playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((ResumeMovieActivity)getActivity()).trailerResponse.isEmpty()) {
                    String URL = ApiUtils.YOUTUBE_URL + ((ResumeMovieActivity) getActivity()).trailerResponse.get(0).getKey();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }else{

                }
            }
        });




        return view;

    }

    public void getMovie(){
        tmvDatabaseService.getMovieById(movieId,PopularMoviesConstants.apiKEY).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.isSuccessful()){
                    movie = response.body();
                    loadTrailers();
                    likeButtonView.setFragment(ResumeActivityFragment.this);
                    likeButtonView.setChecked(isFavorite());
                    String imageURL = ApiUtils.IMAGE_URL_780 + movie.getPosterPath();
                    Glide.with(getActivity().getApplicationContext()).load(imageURL).error(R.drawable.defaultmovie).into(poster);
                    String imageURL2 = ApiUtils.IMAGE_URL_780 + movie.getBackdropPath();
                    Glide.with(getActivity().getApplicationContext()).load(imageURL2).error(R.drawable.defaultmovie).into(trailer);


                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = fmt.parse(movie.getReleaseDate());


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMMM yyyy");
                    releaseDate.setText(fmtOut.format(date).toUpperCase());
                    puntuacion.setText(String.valueOf(movie.getVoteAverage()));
                    overview.setText(movie.getOverview());
                    getActivity().setTitle(movie.getTitle());

                }else{
                    int statusCode = response.code();
                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("MainActivity", "error loading from API");
            }
        });
    }


    public void loadTrailers(){
        tmvDatabaseService.getTrailerById(movie.getId(), PopularMoviesConstants.apiKEY).enqueue(new Callback<TrailerResponse>() {

            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {

                if (response.isSuccessful()){
                    ((ResumeMovieActivity)getActivity()).trailerResponse = response.body().getResults();

                    if (!response.body().getResults().isEmpty()){
                        playTrailer.setVisibility(View.VISIBLE);
                    }else{
                        playTrailer.setVisibility(View.INVISIBLE);
                    }
                }else{
                    int statusCode  = response.code();
                    Log.d("MainActivity", "error loading from API status code: "+statusCode);
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.d("MainActivity", "error loading from API");
            }

        });
    }

    public void updateFavorite(){
        boolean fav = isFavorite();

        if (fav){
            removeMovieFromFav();
        }else{
            addMovieToFav();
        }

    }

    public boolean isFavorite() {
        String[] projection = new String[]{
                DBContract.DBEntry._ID,
                DBContract.DBEntry.COLUMN_MOVIE_ID,
                DBContract.DBEntry.COLUMN_MOVIE_NAME,
                DBContract.DBEntry.COLUMN_MOVIE_URL_IMAGE
        };

        Uri favUri = Uri.parse(DBProvider.uri + "/" + movie.getId());
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(favUri, projection, null, null, null);


        return cur.getCount() > 0 ? true : false;

    }

    public void addMovieToFav(){
        Uri favUri  = DBProvider.CONTENT_URI;
        ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_MOVIE_ID,movie.getId());
        values.put(DBContract.DBEntry.COLUMN_MOVIE_NAME,movie.getTitle());
        values.put(DBContract.DBEntry.COLUMN_MOVIE_URL_IMAGE,movie.getPosterPath());

        cr.insert(favUri,values);
    }

    public int removeMovieFromFav(){
        ContentResolver cr = getContext().getContentResolver();
        Uri favUri = Uri.parse(DBProvider.uri + "/" + movie.getId());

        int response = cr.delete(favUri,null,null);
        return  response;

    }



    @Override
    public void onDestroyView() {
        likeButtonView.setFragment(null);
        super.onDestroyView();
        unbinder.unbind();
    }
}
