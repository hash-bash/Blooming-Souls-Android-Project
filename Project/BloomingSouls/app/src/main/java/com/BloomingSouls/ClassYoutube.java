package com.BloomingSouls;

public class ClassYoutube {
    private String postKey;
    private String title;
    private String videoId;
    private String imageUrl;
    private String search;

    public ClassYoutube() {

    }

    public ClassYoutube(String title, String videoId, String imageUrl, String search) {
        this.title = title;
        this.videoId = videoId;
        this.imageUrl = imageUrl;
        this.search = search;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}