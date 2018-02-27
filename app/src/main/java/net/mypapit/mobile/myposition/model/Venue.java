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
 * Venue.java
 * For storing Venue and Location gson data object from FourSquare API.
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MohammadHafiz on 19/6/2016.
 */
public class Venue implements java.io.Serializable {

    @SerializedName("name")
    public String name;

    public long vid=-1;

    @SerializedName("location")
    public RLocation location;

    //  @SerializedName("categories")
    //public    RCategory category;


    public Venue(String name, String address, double lat, double lng, double distance) {

        this(name, address,lat,lng,distance,-1);

    }

    public Venue(String name, String address, double lat, double lng, double distance, long vid) {
        this.name = name;
        this.vid = vid;
        this.location = new RLocation(address, lat, lng, distance);


    }


    public Venue (Venue xVenue){

        this.name = xVenue.name;
        this.location = new RLocation(xVenue.location.address,xVenue.location.lat,xVenue.location.lng,xVenue.location.distance);


    }

    public Venue distanceTo(Venue venue){

        double distance=this.distanceTo(venue.location.lat,venue.location.lng,'K');

        this.location.distance = distance;

        return new Venue(this.name,this.location.address,this.location.lat,this.location.lng,this.location.distance);


    }

    public Venue calculateDistance(double lat, double lng){

        this.location.distance=distanceTo(lat,lng,'K');

        return new Venue(this.name,this.location.address,this.location.lat,this.location.lng,this.location.distance,this.vid);

    }


    private double distanceTo(double lat, double lng, char unit) {

        double theta = lng - this.location.lng;
        double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(this.location.lat)) + Math.cos(deg2rad(lat)) * Math.cos(deg2rad(this.location.lat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);


    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}


