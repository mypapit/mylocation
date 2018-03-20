package net.mypapit.mobile.myposition.model;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.mypapit.mobile.myposition.R;
import net.mypapit.mobile.video.RecordingDetails;
import net.mypapit.mobile.video.VideoInfo;

/**
 * Created by Admin on 12/3/2018.
 */
public class NearbyVideoAdapter extends
        RecyclerView.Adapter<NearbyVideoAdapter.ViewHolder> {

    private Context context;
    private VideoInfo[] videoInfo;
    private OnItemClickListener listener;


    public NearbyVideoAdapter(Context context, final VideoInfo[] videoInfo) {
        this.videoInfo = videoInfo;
        this.context = context;
    }

    private Context getContext() {
        return this.context;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoInfo info = videoInfo[position];
        AppCompatImageView image = holder.imgPreview;
        AppCompatTextView title = holder.txtTitle;
        AppCompatTextView location = holder.txtLocation;


        title.setText(info.getSnippet().getTitle());

        RecordingDetails details = new RecordingDetails();

        if (info.getRecordingDetails() != null) {
            details = info.getRecordingDetails();
        }

        String imageURL = info.getSnippet().getThumbnails().getStandard().getUrl();

        location.setText(details.getLocationDescription());


        Picasso.with(context).load(imageURL).into(image);


    }

    public NearbyVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.video_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return videoInfo.length;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        AppCompatTextView txtTitle;
        AppCompatTextView txtLocation;
        AppCompatImageView imgPreview;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (listener != null) {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);


                        }


                    }
                }
            });

            txtTitle = itemView.findViewById(R.id.video_title);
            txtLocation = itemView.findViewById(R.id.video_location);
            imgPreview = itemView.findViewById(R.id.video_preview);
        }


    }
}
