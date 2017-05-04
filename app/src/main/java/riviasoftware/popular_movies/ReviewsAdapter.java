package riviasoftware.popular_movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import riviasoftware.popular_movies.data.Review;

/**
 * Created by sergiolizanamontero on 17/4/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewAdapterViewHolder> implements View.OnClickListener {

    private Context context;

    private List<Review> data;

    private LayoutInflater inflater;

    private View.OnClickListener listener;


    public ReviewsAdapter(Context context, ArrayList<Review> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ReviewsAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.review_list,parent,false);
        ReviewsAdapter.ReviewAdapterViewHolder holder = new ReviewsAdapter.ReviewAdapterViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewAdapterViewHolder holder, int position) {
        
        String username = data.get(position).getAuthor();
        String desc = data.get(position).getContent();

        holder.mReviewItem.setTag(data.get(position));
        holder.userNameReview.setText(username);
        holder.descReview.setText(desc);




        holder.mReviewItem.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeItem(Review review) {

        int currPosition = data.indexOf(review);
        data.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, Review review) {

        data.add(position, review);
        notifyItemInserted(position);
    }

    public void updateReview(List<Review> reviews) {
        data = reviews;
        notifyDataSetChanged();
    }

    public  Review getReviews(int adapterPosition) {

        return data.get(adapterPosition);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mReviewItem;
        TextView userNameReview;
        TextView descReview;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            userNameReview = (TextView) itemView.findViewById(R.id.userNameReview);
            descReview = (TextView) itemView.findViewById(R.id.reviewDesc);
            mReviewItem = (RelativeLayout) itemView.findViewById(R.id.reviewItem);

        }



    }
}
