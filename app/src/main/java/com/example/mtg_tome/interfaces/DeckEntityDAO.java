package com.example.mtg_tome.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.models.DeckWithCards;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface DeckEntityDAO {
    @Insert
    Completable insertDeck(DeckEntity deck);

    @Query("SELECT * FROM DeckEntity")
    Single<List<DeckEntity>> getAllDecks();

    @Update
    Completable updateDeck(DeckEntity deck);

    @Delete
    Completable deleteDeck(DeckEntity deck);
}
