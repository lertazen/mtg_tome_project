package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;
import com.example.mtg_tome.interfaces.CardResponseAPI;
import com.example.mtg_tome.interfaces.DeckCardCrossRefDAO;
import com.example.mtg_tome.models.Card;
import com.example.mtg_tome.network.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeckCardDetailFragment extends Fragment {
    private int cardId;
    private int deckId;

    public DeckCardDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deck_card_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView deckCardImgView = view.findViewById(R.id.deck_card_image);
        cardId = getArguments().getInt("card_id");
        deckId = getArguments().getInt("deck_id");
        updateDeckCardImgView(deckCardImgView, cardId);

        // Handle the card quantity change in the deck
        Button qtyChangeBtn = view.findViewById(R.id.deck_card_change_qty_btn);
        qtyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeQtyDialog();
            }
        });
    }

    private void updateDeckCardImgView(ImageView deckCardImgView, int cardId) {
        Retrofit retrofit = RetrofitClient.getInstance();
        CardResponseAPI api = retrofit.create(CardResponseAPI.class);

        Call<Card> getCardByIdCall = api.getCardById(cardId);
        getCardByIdCall.enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful()) {
                    Card card = response.body();
                    String cardImgUrl = card.getImageUris().getNormal();
                    Picasso.get().load(cardImgUrl)
                            .placeholder(R.drawable.loading_card).into(deckCardImgView);
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangeQtyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_qty_dialog, null);

        EditText cardQtyEditText = dialogView.findViewById(R.id.input_qty);
        builder.setTitle("Card Quantity in the deck")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputNumberString = cardQtyEditText.getText().toString();
                        try {
                            int inputQty = Integer.parseInt(inputNumberString);

                            if (inputQty != 0) updateCard(deckId, cardId, inputQty);
                            else deleteCard(deckId, cardId);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Please enter a valid number",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteCard(int deckId, int cardId) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        DeckCardCrossRefDAO deckCardCrossRefDAO = db.deckCardCrossRefDAO();

        // call delete dao
        deckCardCrossRefDAO.deleteCardFromDeck(deckId, cardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(requireContext(), "Card deleted successfully",
                                Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(DeckCardDetailFragment.this)
                                .popBackStack();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(requireContext(), "Error changing quantity",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateCard(int deckId, int cardId, int inputQty) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        DeckCardCrossRefDAO deckCardCrossRefDAO = db.deckCardCrossRefDAO();

        // call update dao
        deckCardCrossRefDAO.updateCardQuantity(deckId, cardId, inputQty)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(requireContext(), "Card quantity changed successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(requireContext(), "Error changing quantity",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}