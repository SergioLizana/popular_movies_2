package riviasoftware.popular_movies;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import riviasoftware.popular_movies.constants.PopularMoviesConstants;
import riviasoftware.popular_movies.data.Movie;
import riviasoftware.popular_movies.data.Review;
import riviasoftware.popular_movies.data.ReviewsResponse;
import riviasoftware.popular_movies.retrofit.services.TMVDatabaseService;
import riviasoftware.popular_movies.retrofit.utils.ApiUtils;

/**
 * Created by sergiolizanamontero on 17/4/17.
 */

public class ResumeMovieReviewsFragment extends Fragment {


    private LayoutInflater inflaterLayout;
    @BindView(R.id.recyclerviewresume)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListReviewAlert)
    TextView emptyListReviewAlert;
    private Unbinder unbinder;
    private List<Review> reviews;
    private TMVDatabaseService tmvDatabaseService;
    private ReviewsAdapter adapter;
    private int movieId;
    private List<Review> reviewResponse;


    public ResumeMovieReviewsFragment() {

    }
    public static ResumeMovieReviewsFragment newInstance() {
        ResumeMovieReviewsFragment fragment = new ResumeMovieReviewsFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey("reviews")){
            reviews = new ArrayList<Review>();
        }else{
            reviews = savedInstanceState.getParcelableArrayList("reviews");
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("reviews",(ArrayList)reviews);
        super.onSaveInstanceState(outState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_review_list, container, false);
        movieId = getActivity().getIntent().getIntExtra("movieId", 0);
        getActivity().setTitle(R.string.title_reviews);
        inflaterLayout = LayoutInflater.from(getContext());
        unbinder = ButterKnife.bind(this,view);
        tmvDatabaseService = ApiUtils.getTMVDataService();
        adapter = new ReviewsAdapter(getActivity(), (ArrayList)reviews);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review rw = (Review)v.getTag();
                Log.d("Click","Clicked: "+rw.getAuthor());
            }
        });
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayout.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(reviews.isEmpty()) {
            loadReviews();
        }else{
            adapter.updateReview(reviews);
        }
        changeVisibilityAlert();

        return view;
    }


    public void loadReviews(){

        tmvDatabaseService.getReviewById(movieId, PopularMoviesConstants.apiKEY).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if(response.isSuccessful()){
                    reviewResponse = response.body().getResults();
                    adapter.updateReview(reviewResponse);


                }else{
                    int statusCode = response.code();
                    Log.d("ResumeActivity","error loading from api "+statusCode);
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Log.d("ResumeActivity","error loading api");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void changeVisibilityAlert(){
        if (reviews !=null && !reviews.isEmpty()){
            emptyListReviewAlert.setVisibility(View.INVISIBLE);
        }else{
            emptyListReviewAlert.setVisibility(View.VISIBLE);
        }
    }

}
