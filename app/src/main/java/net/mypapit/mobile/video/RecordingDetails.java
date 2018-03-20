
package net.mypapit.mobile.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordingDetails {

    @SerializedName("locationDescription")
    @Expose
    private String locationDescription;
    @SerializedName("recordingDate")
    @Expose
    private Object recordingDate;
    @SerializedName("location")
    @Expose
    private Location location;

    public RecordingDetails() {
        this.locationDescription = new String("Unknown");


    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Object getRecordingDate() {
        return recordingDate;
    }

    public void setRecordingDate(Object recordingDate) {
        this.recordingDate = recordingDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
