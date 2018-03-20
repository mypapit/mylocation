
package net.mypapit.mobile.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails {

    @SerializedName("default")
    @Expose
    private Default _default;
    @SerializedName("medium")
    @Expose
    private Medium medium;
    @SerializedName("high")
    @Expose
    private High high;
    @SerializedName("standard")
    @Expose
    private Standard standard;
    @SerializedName("maxres")
    @Expose
    private Maxres maxres;

    public Default getDefault() {
        return _default;
    }

    public void setDefault(Default _default) {
        this._default = _default;
    }

    public VideoQuality getMedium() {
        if (medium == null) {
            return getStandard();
        }


        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public VideoQuality getHigh() {
        if (high == null) {
            return getStandard();
        }

        return high;
    }

    public void setHigh(High high) {
        this.high = high;
    }

    public VideoQuality getStandard() {
        if (standard == null) {
            return getDefault();

        }
        return standard;
    }

    public void setStandard(Standard standard) {
        this.standard = standard;
    }

    public VideoQuality getMaxres() {
        if (maxres == null) {

            return getStandard();
        }

        return maxres;
    }

    public void setMaxres(Maxres maxres) {
        this.maxres = maxres;
    }

    public Maxres getAbsMaxres() {
        return maxres;


    }

}
