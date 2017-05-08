package riviasoftware.popular_movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import riviasoftware.popular_movies.data.Trailer;



public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> implements View.OnClickListener {

    private Context context;

    private List<Trailer> data;

    private LayoutInflater inflater;

    private View.OnClickListener listener;


    public TrailersAdapter(Context context, ArrayList<Trailer> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public TrailersAdapter.TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.trailer_list,parent,false);
        TrailersAdapter.TrailersAdapterViewHolder holder = new TrailersAdapter.TrailersAdapterViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(TrailersAdapter.TrailersAdapterViewHolder holder, int position) {
          String trailerName = data.get(position).getName();
          holder.mTrailerItem.setTag(data.get(position));
          holder.nameTrailer.setText(trailerName);
          holder.mTrailerItem.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeItem(Trailer trailer) {

        int currPosition = data.indexOf(trailer);
        data.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, Trailer trailer) {

        data.add(position, trailer);
        notifyItemInserted(position);
    }

    public void updateTrailers(List<Trailer> trailers) {
        data = trailers;
        notifyDataSetChanged();
    }

    public  Trailer getTrailers(int adapterPosition) {

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

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mTrailerItem;
        TextView nameTrailer;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            nameTrailer = (TextView) itemView.findViewById(R.id.nameTrailer);
            mTrailerItem = (RelativeLayout) itemView.findViewById(R.id.trailerItem);

        }



    }
}
