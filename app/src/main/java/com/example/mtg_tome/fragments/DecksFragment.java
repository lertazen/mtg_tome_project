package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.adapters.DeckAdapter;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;
import com.example.mtg_tome.interfaces.DeckEntityDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DecksFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton addDeckBtn;

    public DecksFragment() {
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
        return inflater.inflate(R.layout.fragment_decks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.decks_recycler_view);
        addDeckBtn = view.findViewById(R.id.addNewDeckButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DeckAdapter deckAdapter = new DeckAdapter(new ArrayList<>());
        recyclerView.setAdapter(deckAdapter);

        // Retrieve the collection from the database
        loadDecks(deckAdapter);

        // Set click listener on floating action button
        addDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requireContext();
                showAddDeckDialog(context);
            }
        });
    }

    private void showAddDeckDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_deck_dialog, null);

        EditText deckNameEditText = dialogView.findViewById(R.id.deck_input_name);

        builder.setTitle("New Deck Name ")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputDeckName = deckNameEditText.getText().toString();
                        // Create a new DeckEntity object
                        DeckEntity deck = new DeckEntity(inputDeckName);
                        addDeck(context, deck);
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void loadDecks(DeckAdapter deckAdapter) {
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
                        // Update the RecyclerView adapter with the retrieved cards
                        deckAdapter.updateDecks(deckEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle the error
                        Toast.makeText(getActivity(), "Error loading decks: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addDeck(Context context, DeckEntity deck) {
        AppDatabase db = AppDatabase.getInstance(context);
        DeckEntityDAO deckDAO = db.deckEntityDAO();

        deckDAO.insertDeck(deck)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        // Insert successful, show a success message
                        Toast.makeText(context, "Deck added successfully",
                                Toast.LENGTH_SHORT).show();
                        // Refresh the list of decks to reflect the new addition
                        loadDecks((DeckAdapter) recyclerView.getAdapter());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        // Insert failed, show an error message
                        Toast.makeText(context, "Error adding deck: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}