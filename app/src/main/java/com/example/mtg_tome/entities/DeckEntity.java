package com.example.mtg_tome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class DeckEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int deckId;

    @ColumnInfo(name = "deck_name")
    private String deckName;

    @Ignore
    public DeckEntity() {}

    public DeckEntity(String deckName) {
        this.deckName = deckName;
    }

    protected DeckEntity(Parcel in) {
        deckId = in.readInt();
        deckName = in.readString();
    }

    public static final Creator<DeckEntity> CREATOR = new Creator<DeckEntity>() {
        @Override
        public DeckEntity createFromParcel(Parcel in) {
            return new DeckEntity(in);
        }

        @Override
        public DeckEntity[] newArray(int size) {
            return new DeckEntity[size];
        }
    };

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    @NonNull
    @Override
    public String toString() {
        return deckName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(deckName);
        dest.writeInt(deckId);
    }
}
