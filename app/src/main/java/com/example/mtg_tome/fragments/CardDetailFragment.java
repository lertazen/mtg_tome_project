package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;
import com.example.mtg_tome.models.Card;
import com.squareup.picasso.Picasso;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class CardDetailFragment extends Fragment {
    TextView cardNameText;
    ImageView cardImageView;
    Button addToCollectionBtn;

    public CardDetailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_card_detail, container, false);
        String cardName = CardDetailFragmentArgs.
                fromBundle(getArguments()).getCardClicked().getName();

        cardNameText = view.findViewById(R.id.cardNameText);
        cardNameText.setText(cardName);

        cardImageView = view.findViewById(R.id.detailImageView);

        String imgUrl = CardDetailFragmentArgs
                .fromBundle(getArguments()).getCardClicked().getImageUris().getNormal();
        Picasso.get().load(imgUrl).placeholder(R.drawable.loading_card).into(cardImageView);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addToCollectionBtn = view.findViewById(R.id.button_add);
        // Add the current selected card to the local database
        addToCollectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requireContext();
                showCardQtyDialog(context);
            }
        });
    }

    // show alert dialog for user to add card to the collection
    private void showCardQtyDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_qty_dialog, null);
        EditText cardQtyEditText = dialogView.findViewById(R.id.input_qty);
        String cardName = CardDetailFragmentArgs.fromBundle(getArguments()).getCardClicked()
                        .getName();
        int cardId = CardDetailFragmentArgs.fromBundle(getArguments()).getCardClicked()
                        .getTcgplayer_id();

        builder.setTitle("Card Quantity ")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputNumberString = cardQtyEditText.getText().toString();
                        try {
                            int inputQty = Integer.parseInt(inputNumberString);

                            // Create a new CardEntity object
                            CardEntity card = new CardEntity(cardId, cardName, inputQty);
                            addCard(context, card);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Please enter a valid number",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // add the card to the database
    private void addCard(Context context, CardEntity card) {
        AppDatabase db = AppDatabase.getInstance(context);
        CardEntityDAO cardDAO = db.cardEntityDAO();

        cardDAO.insertCard(card)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        // Insert successful, show a success message
                        Toast.makeText(context, "Card added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        // Insert failed, show an error message
                        Toast.makeText(context, "Error adding card: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}