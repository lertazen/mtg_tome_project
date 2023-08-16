package com.example.mtg_tome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CardEntity implements Parcelable {
    @PrimaryKey
    private int cardId;

    @ColumnInfo(name = "card_name")
    private String cardName;

    @ColumnInfo(name = "card_number")
    private int cardNumber;

    public CardEntity(int cardId, String cardName, int cardNumber) {
        this.setCardId(cardId);
        this.setCardName(cardName);
        this.setCardNumber(cardNumber);
    }

    @Ignore
    public CardEntity() {

    }


    protected CardEntity(Parcel in) {
        cardId = in.readInt();
        cardName = in.readString();
        cardNumber = in.readInt();
    }

    public static final Creator<CardEntity> CREATOR = new Creator<CardEntity>() {
        @Override
        public CardEntity createFromParcel(Parcel in) {
            return new CardEntity(in);
        }

        @Override
        public CardEntity[] newArray(int size) {
            return new CardEntity[size];
        }
    };

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(cardId);
        dest.writeString(cardName);
        dest.writeInt(cardNumber);
    }
}
