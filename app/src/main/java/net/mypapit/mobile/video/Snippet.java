
package net.mypapit.mobile.video;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet {

    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("defaultAudioLanguage")
    @Expose
    private String defaultAudioLanguage;
    @SerializedName("defaultLanguage")
    @Expose
    private String defaultLanguage;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("liveBroadcastContent")
    @Expose
    private String liveBroadcastContent;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumbnails")
    @Expose
    private Thumbnails thumbnails;
    @SerializedName("localized")
    @Expose
    private Localized localized;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDefaultAudioLanguage() {
        return defaultAudioLanguage;
    }

    public void setDefaultAudioLanguage(String defaultAudioLanguage) {
        this.defaultAudioLanguage = defaultAudioLanguage;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLiveBroadcastContent() {
        return liveBroadcastContent;
    }

    public void setLiveBroadcastContent(String liveBroadcastContent) {
        this.liveBroadcastContent = liveBroadcastContent;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public Localized getLocalized() {
        return localized;
    }

    public void setLocalized(Localized localized) {
        this.localized = localized;
    }

}
