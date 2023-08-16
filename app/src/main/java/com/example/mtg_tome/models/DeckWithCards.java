package com.example.mtg_tome.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckCardCrossRef;
import com.example.mtg_tome.entities.DeckEntity;

import java.util.List;

public class DeckWithCards {
    @Embedded
    public DeckEntity deck;

    @Relation(
            parentColumn = "deckId",
            entityColumn = "cardId",
            associateBy = @Junction(DeckCardCrossRef.class)
    )
    public List<CardEntity> cards;
}
