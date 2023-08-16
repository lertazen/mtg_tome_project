package com.example.mtg_tome.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class SearchOption implements Parcelable {
    private String cardName;
    private List<String> colorOptions;

    // Constructor
    public SearchOption(String cardName, List<String> colors) {
        this.setCardName(cardName);
        this.setColorOptions(colors);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getCardName());
    }

    public static final Parcelable.Creator<SearchOption> CREATOR
            = new Parcelable.Creator<SearchOption>() {
        public SearchOption createFromParcel(Parcel in) {
            return new SearchOption(in);
        }

        public SearchOption[] newArray(int size) {
            return new SearchOption[size];
        }
    };

    private SearchOption(Parcel in) {
        setCardName(in.readString());
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public List<String> getColorOptions() {
        return colorOptions;
    }

    public void setColorOptions(List<String> colorOptions) {
        this.colorOptions = colorOptions;
    }
}
