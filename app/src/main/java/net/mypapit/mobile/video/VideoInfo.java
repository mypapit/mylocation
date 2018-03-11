
package net.mypapit.mobile.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoInfo {

    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("snippet")
    @Expose
    private Snippet snippet;
    @SerializedName("recordingDetails")
    @Expose
    private RecordingDetails recordingDetails;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public RecordingDetails getRecordingDetails() {
        return recordingDetails;
    }

    public void setRecordingDetails(RecordingDetails recordingDetails) {
        this.recordingDetails = recordingDetails;
    }

}
