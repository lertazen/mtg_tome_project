package com.example.mtg_tome.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckCardCrossRef;
import com.example.mtg_tome.models.DeckWithCards;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface DeckCardCrossRefDAO {
    // Insert a new relationship between a deck and a card
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(DeckCardCrossRef ref);

    // Update the number of a specific card in a deck
    @Query("UPDATE DeckCardCrossRef SET cardQuantity = :newQuantity " +
            "WHERE deckId = :deckId AND cardId = :cardId")
    Completable updateCardQuantity(int deckId, int cardId, int newQuantity);

    // Delete a specific card from a deck
    @Query("DELETE FROM DeckCardCrossRef " +
            "WHERE deckId = :deckId AND cardId = :cardId")
    Completable deleteCardFromDeck(int deckId, int cardId);

    // Get card name and its quantity in the deck
    @Query("SELECT c.card_name AS cardName, dc.cardQuantity, c.cardId, dc.deckId " +
            "FROM DeckCardCrossRef AS dc " +
            "JOIN CardEntity AS c ON dc.cardId = c.cardId " +
            "WHERE dc.deckId = :deckId")
    Single<List<DeckCardDetail>> getCardsByDeckId(int deckId);

    class DeckCardDetail {
        public String cardName;
        public int cardQuantity;
        public int cardId;
        public int deckId;
    }
}
