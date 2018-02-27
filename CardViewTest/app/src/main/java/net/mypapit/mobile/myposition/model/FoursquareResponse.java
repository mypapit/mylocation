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
 * FourSquareResponse.java
 * For storing Venue and Location gson data object from FourSquare API.
 * My GPS Location Tool
 */
package net.mypapit.mobile.myposition.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MohammadHafiz on 19/6/2016.
 */
public class FoursquareResponse {

    // @SerializedName("venues")
    public Venue[] venues;

    @SerializedName("confident")
    boolean confident;


}
