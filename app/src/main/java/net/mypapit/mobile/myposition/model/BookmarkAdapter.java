package net.mypapit.mobile.myposition.model;

/**
 * Created by Admin on 19/2/2018.
 */

/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * MyLocation 3.0 for Android <mypapit@gmail.com> (9w2wtf)
 * Copyright 2018 Mohammad Hafiz bin Ismail. All rights reserved.
 *
 * Info url :
 * https://github.com/mypapit/mylocation/
 * http://kirostudio.com
 * http://blog.mypapit.net/
 *
 *
 * BookmarkAdapter.java
 * ListView Adapter for handling Sugar CRM database Bookmark POJO object
 * My GPS Location Tool
 */

import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.mypapit.mobile.myposition.R;
import net.mypapit.mobile.myposition.db.VenueBookmark;

import java.text.DecimalFormat;
import java.util.ArrayList;

import garin.artemiy.compassview.library.CompassView;


/**
 * Created by Admin on 10/2/2018.
 */

public class BookmarkAdapter extends ArrayAdapter<Venue> {

    private ArrayList<Venue> list;
    private Activity context;
    private Location current;
//    private CompassSensorManager manager;


    public BookmarkAdapter(Activity context, ArrayList<Venue> list, Location current) {
        super(context, R.layout.activity_share, list);


        this.context = context;
        this.list = list;
        this.current = current;
        //this.manager = manager;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {

            listItem = LayoutInflater.from(context).inflate(R.layout.bookmarklocationlayout, parent, false);


        }

        Venue venue = list.get(position);


        //set image;

        TextView tvLocation = listItem.findViewById(R.id.slBookmarkLocation);
        TextView tvAddress = listItem.findViewById(R.id.slAddress);
        TextView tvDistance = listItem.findViewById(R.id.slDistance);


        Location bookmark = new Location("bookmark");
        bookmark.setLatitude(venue.location.lat);
        bookmark.setLongitude(venue.location.lng);




        CompassView image =  listItem.findViewById(R.id.icon);

        image.initializeCompass(current, bookmark, R.drawable.ic_arrowx);


        tvAddress.setText(venue.location.address);
        tvLocation.setText(venue.name);


        double distance;
        String sDistance;


        distance = venue.location.distance;
        sDistance = new DecimalFormat("#.##").format(distance) + " km";


        tvDistance.setText(sDistance);
        return listItem;


    }

    public void remove(int position) {

        VenueBookmark vb = VenueBookmark.findById(VenueBookmark.class, list.get(position).vid);
        Log.d("mypapit.fakap", "Delete id: " + list.get(position).vid + " " + list.get(position).name);
        vb.delete();

        list.remove(position);


    }


}

