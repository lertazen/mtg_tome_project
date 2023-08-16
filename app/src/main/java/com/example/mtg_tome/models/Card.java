package com.example.mtg_tome.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Card implements Parcelable {

    private int tcgplayer_id;
    private String name;
    private ImageUris image_uris;
    private List<CardFace> card_faces;

    protected Card(Parcel in) {
        name = in.readString();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageUris getImageUris() {
        if (image_uris != null) {
            return image_uris;
        } else if (card_faces != null && !card_faces.isEmpty()) {
            return card_faces.get(0).getImage_uris();
        }
        return null;
    }

    public List<CardFace> getCardFaces() {
        return card_faces;
    }

    public void setCardFaces(List<CardFace> cardFaces) {
        this.card_faces = cardFaces;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getName());
    }

    public int getTcgplayer_id() {
        return tcgplayer_id;
    }

    public void setTcgplayer_id(int tcgplayer_id) {
        this.tcgplayer_id = tcgplayer_id;
    }
}
