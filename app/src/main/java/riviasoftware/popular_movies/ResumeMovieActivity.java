package riviasoftware.popular_movies;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import riviasoftware.popular_movies.data.Movie;
import riviasoftware.popular_movies.data.Trailer;
import riviasoftware.popular_movies.data.TrailerResponse;


public class ResumeMovieActivity extends AppCompatActivity {

    public List<Trailer> trailerResponse;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (trailerResponse != null){
            outState.putParcelableArrayList("trailers",(ArrayList)trailerResponse);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        if (savedInstanceState != null && savedInstanceState.containsKey("trailers")){
            trailerResponse =  savedInstanceState.getParcelableArrayList("trailers");
        }
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                selectedFragment = ResumeActivityFragment.newInstance();
                                break;
                            case R.id.navigation_review:
                                selectedFragment = ResumeMovieReviewsFragment.newInstance();
                                break;
                            case R.id.navigation_resources:
                                selectedFragment = ResumeMovieTrailersFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frame_layout, ResumeActivityFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }



}
