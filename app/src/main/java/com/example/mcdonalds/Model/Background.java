package com.example.mcdonalds.Model;

public class Background {
    private String BackgroundId, BackgroundImg;

    public Background(String backgroundId, String backgroundImg) {
        BackgroundId = backgroundId;
        BackgroundImg = backgroundImg;
    }

    public String getBackgroundId() {
        return BackgroundId;
    }

    public void setBackgroundId(String backgroundId) {
        BackgroundId = backgroundId;
    }

    public String getBackgroundImg() {
        return BackgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        BackgroundImg = backgroundImg;
    }
}
