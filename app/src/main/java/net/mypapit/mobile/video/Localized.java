
package net.mypapit.mobile.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Localized {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("title")
    @Expose
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
