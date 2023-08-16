package com.example.mtg_tome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.adapters.CollectionAdapter;
import com.example.mtg_tome.database.AppDatabase;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.interfaces.CardEntityDAO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CollectionFragment extends Fragment {

    public CollectionFragment() {
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
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.collection_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize an empty list
        CollectionAdapter adapter = new CollectionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Retrieve the collection from the database
        loadCollection(adapter);
    }

    // use app database to get collection
    private void loadCollection(CollectionAdapter adapter) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        CardEntityDAO cardDAO = db.cardEntityDAO();

        cardDAO.loadAllCards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CardEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<CardEntity> cardEntities) {
                        // Update the RecyclerView adapter with the retrieved cards
                        adapter.updateCards(cardEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle the error
                        Toast.makeText(getActivity(), "Error loading cards: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}