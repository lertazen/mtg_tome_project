package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckCardCrossRef;
import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;
import com.example.mtg_tome.interfaces.CardResponseAPI;
import com.example.mtg_tome.interfaces.DeckCardCrossRefDAO;
import com.example.mtg_tome.interfaces.DeckEntityDAO;
import com.example.mtg_tome.models.Card;
import com.example.mtg_tome.network.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CollectionDetailFragment extends Fragment {

    TextView cardNameText;
    ImageView cardImgView;
    TextView cardQty;
    Button changeQtyBtn;
    Button addToDeckBtn;

    private int cardId;

    public CollectionDetailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_collection_detail, container, false);
        String cardName = CollectionDetailFragmentArgs.fromBundle(getArguments())
                .getCollectionCardEntity().getCardName();

        cardId = CollectionDetailFragmentArgs.fromBundle(getArguments())
                .getCollectionCardEntity().getCardId();

        cardNameText = view.findViewById(R.id.collection_card_name);
        cardNameText.setText(cardName);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int cardNumber = CollectionDetailFragmentArgs.fromBundle(getArguments())
                .getCollectionCardEntity().getCardNumber();
        cardQty = view.findViewById(R.id.collection_card_qty);

        cardQty.setText(getString(R.string.collection_quantity) + cardNumber);

        // update image view
        cardImgView = view.findViewById(R.id.collection_card_image);
        updateImageView(cardId, cardImgView);

        // Set listener on the change quantity button
        changeQtyBtn = view.findViewById(R.id.collection_change_qty_btn);
        changeQtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requireContext();
                showChangeQtyDialog(context);
            }
        });

        // Set listener on the add to deck button
        addToDeckBtn = view.findViewById(R.id.collection_add_to_deck);
        addToDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeckEntities();
            }
        });
    }

    // show dialog for user to choose a deck to which the card is added
    private void getDeckEntities() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        DeckEntityDAO deckDAO = db.deckEntityDAO();

        deckDAO.getAllDecks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<DeckEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<DeckEntity> deckEntities) {
                        showAddToDeckDialog(deckEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle the error
                        Toast.makeText(getActivity(), "Error loading decks: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showAddToDeckDialog(List<DeckEntity> deckEntities) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_to_deck, null);
        Spinner deckSpinner = dialogView.findViewById(R.id.deck_spinner);
        EditText amountEditText = dialogView.findViewById(R.id.add_to_deck_amount);

        ArrayAdapter<DeckEntity> deckSpinnerAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, deckEntities);
        deckSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deckSpinner.setAdapter(deckSpinnerAdapter);

        builder.setTitle("Please select a deck and amount")
            .setView(dialogView)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int amount = 0;
                    String amountString = amountEditText.getText().toString();
                    try {
                        amount = Integer.parseInt(amountString);
                        int selectedPos = deckSpinner.getSelectedItemPosition();
                        DeckEntity selectedDeck = deckEntities.get(selectedPos);
                        int deckId = selectedDeck.getDeckId();

                        addCardToDeck(cardId, deckId, amount);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter valid number",
                                Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // add card and deck relation entity to the database
    private void addCardToDeck(int cardId, int deckId, int amount) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        DeckCardCrossRefDAO deckCardCrossRefDAO = db.deckCardCrossRefDAO();
        DeckCardCrossRef ref = new DeckCardCrossRef(deckId, cardId, amount);

        deckCardCrossRefDAO.insert(ref)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(requireContext(), "Card added successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(requireContext(), "Error adding card: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    // update the image view according to the card id with which the api responds
    private void updateImageView(int cardId, ImageView cardImgView) {
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
                            .placeholder(R.drawable.loading_card).into(cardImgView);
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


    // show a quantity change dialog to the user
    private void showChangeQtyDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_qty_dialog, null);

        EditText cardQtyEditText = dialogView.findViewById(R.id.input_qty);
        builder.setTitle("Card Quantity ")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputNumberString = cardQtyEditText.getText().toString();
                        try {
                            int inputQty = Integer.parseInt(inputNumberString);

                            if (inputQty != 0) updateCard(context, inputQty);
                            else deleteCard(context);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Please enter a valid number",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    // update the card quantity in database after user makes change
    private void updateCard(Context context, int inputQty) {
        CardEntity card = CollectionDetailFragmentArgs.fromBundle(getArguments())
                .getCollectionCardEntity();
        AppDatabase db = AppDatabase.getInstance(context);
        CardEntityDAO cardDAO = db.cardEntityDAO();

        // set the new quantity for the card
        card.setCardNumber(inputQty);

        // call update dao
        cardDAO.updateCard(card)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "Card quantity changed successfully",
                                Toast.LENGTH_SHORT).show();
                        cardQty.setText(getString(R.string.collection_quantity) + inputQty);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(context, "Error changing quantity: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // delete the card if the quantity changed to 0
    private void deleteCard(Context context) {
        CardEntity card = CollectionDetailFragmentArgs.fromBundle(getArguments())
                .getCollectionCardEntity();
        AppDatabase db = AppDatabase.getInstance(context);
        CardEntityDAO cardDAO = db.cardEntityDAO();

        // call the delete card dao
        cardDAO.deleteCard(card)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "Card deleted successfully",
                                Toast.LENGTH_SHORT).show();
                        cardQty.setText(getString(R.string.collection_quantity) + 0);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(context, "Error deleting card: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}