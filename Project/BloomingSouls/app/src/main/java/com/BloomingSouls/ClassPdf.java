package com.BloomingSouls;

public class ClassPdf {
    public String name;
    public String search;
    public String pdfUrl;
    public String imageUrl;
    public String setName;

    public ClassPdf(String name, String search, String pdfUrl, String imageUrl) {
        this.name = name;
        this.search = search;
        this.pdfUrl = pdfUrl;
        this.imageUrl = imageUrl;
    }

    public ClassPdf() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
