package com.example.mtg_tome.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckCardCrossRef;
import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;
import com.example.mtg_tome.interfaces.DeckCardCrossRefDAO;
import com.example.mtg_tome.interfaces.DeckEntityDAO;

@Database(entities = {DeckEntity.class, CardEntity.class, DeckCardCrossRef.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeckEntityDAO deckEntityDAO();

    public abstract CardEntityDAO cardEntityDAO();

    public abstract DeckCardCrossRefDAO deckCardCrossRefDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
