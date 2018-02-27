package net.mypapit.mobile.myposition.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;


import net.mypapit.mobile.myposition.R;

import java.util.ArrayList;

/**
 * Created by Admin on 10/2/2018.
 */

public class ShareLocationAdapter extends ArrayAdapter<Venue> {

    ArrayList<Venue> list;
    Activity context;


    public ShareLocationAdapter(Activity context, ArrayList<Venue> list) {
        super(context, R.layout.activity_share, list);


        this.context = context;
        this.list = list;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {

            listItem = LayoutInflater.from(context).inflate(R.layout.sharelocationlayout, parent, false);


        }

        Venue venue = list.get(position);


        //set image;

        TextView tvlocation = listItem.findViewById(R.id.slLocation);
        TextView tvAddress = listItem.findViewById(R.id.slAddress);
        TextView tvDistance = listItem.findViewById(R.id.slDistance);

        ImageView image = listItem.findViewById(R.id.icon);

        String letter = venue.name.charAt(0) + "";
        tvlocation.setText(venue.name);
        tvAddress.setText(venue.location.address);


        double distance;
        String sDistance;

        if (venue.location.distance > 1000) {

            distance = venue.location.distance / 1000.0;
            sDistance = distance + " km";

        } else {

            distance = venue.location.distance;
            sDistance = distance + " m";

        }

        tvDistance.setText(sDistance);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(list.get(position));


        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter.toUpperCase(), color);

        image.setImageDrawable(drawable);

        return listItem;


    }


}
