package com.example.mtg_tome.interfaces;

import com.example.mtg_tome.models.Card;
import com.example.mtg_tome.models.CardList;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CardResponseAPI {

    // Get a random card response
    @GET("cards/random")
    Call<Card> getRandomCard();

    @GET("cards/search")
    Call<CardList> searchCards(@Query("q") String query);

    @GET("cards/tcgplayer/{id}")
    Call<Card> getCardById(@Path("id") int cardId);
}
