package com.example.mtg_tome.models;

public class CardFace {
    private String name;
    private ImageUris image_uris;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageUris getImage_uris() {
        return image_uris;
    }

    public void setImage_uris(ImageUris image_uris) {
        this.image_uris = image_uris;
    }
}
