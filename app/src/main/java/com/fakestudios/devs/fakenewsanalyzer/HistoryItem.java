package com.fakestudios.devs.fakenewsanalyzer;

public class HistoryItem {

    String url;

    public HistoryItem(){
    }

    public HistoryItem(String url){
        this.url = url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}
