package riviasoftware.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import riviasoftware.popular_movies.data.Trailer;
import riviasoftware.popular_movies.retrofit.utils.ApiUtils;

/**
 * Created by sergiolizanamontero on 6/5/17.
 */

public class ResumeMovieTrailersFragment extends Fragment {


    private LayoutInflater inflaterLayout;
    @BindView(R.id.recyclerviewtrailers)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListTrailerAlert)
    TextView emptyListTrailerAlert;
    private Unbinder unbinder;
    private List<Trailer> trailers;
    private TrailersAdapter adapter;


    public ResumeMovieTrailersFragment() {

    }
    public static ResumeMovieTrailersFragment newInstance() {
        ResumeMovieTrailersFragment fragment = new ResumeMovieTrailersFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey("trailers")){
            trailers = new ArrayList<Trailer>();
        }else{
            trailers = savedInstanceState.getParcelableArrayList("trailers");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers",(ArrayList)trailers);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_trailer_list, container, false);
        unbinder = ButterKnife.bind(this,view);
        trailers =  ((ResumeMovieActivity)getActivity()).trailerResponse;
        changeVisibilityAlert();
        inflaterLayout = LayoutInflater.from(getContext());
        getActivity().setTitle(R.string.title_trailers);
        adapter = new TrailersAdapter(getActivity(), (ArrayList) trailers);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer item = (Trailer) v.getTag();
                String URL = ApiUtils.YOUTUBE_URL + item.getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayout.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void changeVisibilityAlert(){
        if (trailers !=null && !trailers.isEmpty()){
            emptyListTrailerAlert.setVisibility(View.INVISIBLE);
        }else{
            emptyListTrailerAlert.setVisibility(View.VISIBLE);
        }
    }


}


