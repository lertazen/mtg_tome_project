package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.adapters.DeckDetailAdapter;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.interfaces.DeckCardCrossRefDAO;
import com.example.mtg_tome.interfaces.DeckEntityDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DeckDetailFragment extends Fragment {
    private String deckName;
    private Button changeDeckNameBtn;
    private Button deleteDeckBtn;
    private TextView deckNameTextView;

    private RecyclerView recyclerView;

    public DeckDetailFragment() {
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
        View view = inflater.inflate(R.layout.fragment_deck_detail, container, false);

        deckName = DeckDetailFragmentArgs.fromBundle(getArguments()).getClickedDeckEntity()
                .getDeckName();
        deckNameTextView = view.findViewById(R.id.deck_detail_name);
        deckNameTextView.setText(deckName);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.deck_detail_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int deckId = DeckDetailFragmentArgs.fromBundle(getArguments()).getClickedDeckEntity()
                .getDeckId();

        Context context = requireContext();
        changeDeckNameBtn = view.findViewById(R.id.deck_detail_change_name);
        deleteDeckBtn = view.findViewById(R.id.deck_detail_delete);

        changeDeckNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDeckDialog(context);
            }
        });

        deleteDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDeckDialog(context);
            }
        });

        loadCardsFromDeck(deckId, view);
    }

    // Load cards from the current deck and inflate the recycler view
    private void loadCardsFromDeck(int deckId, View view) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        DeckCardCrossRefDAO deckCardCrossRefDAO = db.deckCardCrossRefDAO();

        deckCardCrossRefDAO.getCardsByDeckId(deckId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<DeckCardCrossRefDAO.DeckCardDetail>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<DeckCardCrossRefDAO.DeckCardDetail> deckCardDetails) {
                        // create and set your RecyclerView adapter
                        DeckDetailAdapter adapter = new DeckDetailAdapter(deckCardDetails);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Error loading cards: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void showDeleteDeckDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete the deck")
                .setMessage("Are you sure about deleting this deck?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDeck(context);
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showUpdateDeckDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_deck_name, null);

        EditText deckNameEditText = dialogView.findViewById(R.id.dialog_input_deck_name);
        builder.setTitle("New deck name")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputDeckName = deckNameEditText.getText().toString();
                        updateDeck(context, inputDeckName);
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updateDeck(Context context, String inputDeckName) {
        DeckEntity deck = DeckDetailFragmentArgs.fromBundle(getArguments())
                .getClickedDeckEntity();
        AppDatabase db = AppDatabase.getInstance(context);
        DeckEntityDAO deckDAO = db.deckEntityDAO();

        // set the new name for the deck
        deck.setDeckName(inputDeckName);

        // call update dao
        deckDAO.updateDeck(deck)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "Deck name changed successfully",
                                Toast.LENGTH_SHORT).show();
                        deckNameTextView.setText(inputDeckName);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(context, "Error changing deck name: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteDeck(Context context) {
        DeckEntity deck = DeckDetailFragmentArgs.fromBundle(getArguments())
                .getClickedDeckEntity();
        AppDatabase db = AppDatabase.getInstance(context);
        DeckEntityDAO deckDAO = db.deckEntityDAO();

        // call the delete card dao
        deckDAO.deleteDeck(deck)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "Deck deleted successfully",
                                Toast.LENGTH_SHORT).show();
                        // go back to deck fragment
                        NavHostFragment.findNavController(DeckDetailFragment.this).popBackStack();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(context, "Error deleting deck: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}