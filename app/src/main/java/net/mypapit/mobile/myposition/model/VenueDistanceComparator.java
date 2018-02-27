package net.mypapit.mobile.myposition.model;

import java.util.Comparator;

/**
 * Created by Admin on 11/2/2018.
 */

public class VenueDistanceComparator implements Comparator<Venue> {


    @Override
    public int compare(Venue o1, Venue o2) {
        if (o1.location.distance < o2.location.distance)
            return -1;

        return 1;


    }
}
