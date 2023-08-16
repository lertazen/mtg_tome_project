package com.example.mtg_tome.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.interfaces.CardResponseAPI;
import com.example.mtg_tome.models.Card;
import com.example.mtg_tome.network.RetrofitClient;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    ImageView homeImageView;
    Button nextRandomBtn;
    private static final String KEY_CURRENT_CARD_URL = "currentCardUrl";
    private String currentCardUrl;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeImageView = view.findViewById(R.id.homeImageView);
        nextRandomBtn = view.findViewById(R.id.button_next_random);

        // Initialize Retrofit
        Retrofit retrofit = RetrofitClient.getInstance();

        CardResponseAPI cardResponseAPI = retrofit.create(CardResponseAPI.class);

        // Call the load next random card
        if (savedInstanceState != null) {
            currentCardUrl = savedInstanceState.getString(KEY_CURRENT_CARD_URL);
        }
        if (currentCardUrl != null) {
            Picasso.get().load(currentCardUrl).placeholder(R.drawable.loading_card).into(homeImageView);
        } else {
            // Load a random card
            loadRandomCard(getContext(), homeImageView, cardResponseAPI);
        }

        // Add click listener to button, allowing user to load next random card
        nextRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRandomCard(getContext(), homeImageView, cardResponseAPI);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current card URL to the instance state
        if (currentCardUrl != null) {
            outState.putString(KEY_CURRENT_CARD_URL, currentCardUrl);
        }
    }

    private void loadRandomCard(Context context, ImageView imageView, CardResponseAPI cardResponseAPI) {
        Call<Card> randomCardCall = cardResponseAPI.getRandomCard();

        randomCardCall.enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful()) {
                    Card randomCard = response.body();
                    String randomCardImageUrl = randomCard.getImageUris().getNormal();

                    Picasso.get().load(randomCardImageUrl).placeholder(R.drawable.loading_card).into(imageView);

                    currentCardUrl = randomCardImageUrl;
                } else {
                    Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}