package com.example.mcdonalds.Model;

public class Background {
    private String backgroundid, backgroundimg;

    public Background(String backgroundid, String backgroundimg) {
        this.backgroundid = backgroundid;
        this.backgroundimg = backgroundimg;
    }

    public String getBackgroundid() {
        return backgroundid;
    }

    public void setBackgroundid(String backgroundid) {
        this.backgroundid = backgroundid;
    }

    public String getBackgroundimg() {
        return backgroundimg;
    }

    public void setBackgroundimg(String backgroundimg) {
        this.backgroundimg = backgroundimg;
    }
}
