package com.example.mtg_tome.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mtg_tome.entities.CardEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CardEntityDAO {

    // insert one card
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCard(CardEntity card);

    // get all the card for the collection fragment
    @Query("SELECT * FROM CardEntity")
    Single<List<CardEntity>> loadAllCards();

    @Update
    Completable updateCard(CardEntity card);

    @Delete
    Completable deleteCard(CardEntity card);
}
