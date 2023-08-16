package com.example.mtg_tome.entities;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"deckId", "cardId"})
public class DeckCardCrossRef {
    public int deckId;
    public int cardId;

    public int cardQuantity;

    @Ignore
    public DeckCardCrossRef() {}

    public DeckCardCrossRef(int deckId, int cardId, int cardQuantity) {
        this.deckId = deckId;
        this.cardId =cardId;
        this.cardQuantity = cardQuantity;
    }
}
