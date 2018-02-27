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
 * VenueBookmark.java
 * Sugar CRM Record file model for bookmarked location
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition.db;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Admin on 18/2/2018.
 */

public class VenueBookmark extends SugarRecord<VenueBookmark> {

    public String name;
    public String address;
    public double distance, lat, lng;





    public VenueBookmark(){
    }

    public VenueBookmark(String name, String address, double lat, double lng, double distance){
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;



    }
}



