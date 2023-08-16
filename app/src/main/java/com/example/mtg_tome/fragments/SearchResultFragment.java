package com.example.mtg_tome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mtg_tome.R;
import com.example.mtg_tome.adapters.CardAdapter;
import com.example.mtg_tome.interfaces.CardResponseAPI;
import com.example.mtg_tome.models.CardList;
import com.example.mtg_tome.network.RetrofitClient;
import com.example.mtg_tome.view_model.SearchResultViewModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchResultFragment extends Fragment{
    private final int GRID_COL_COUNT = 2;

    private CardList cardList;

    private RecyclerView recyclerView;
    private SearchResultViewModel viewModel;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SearchResultViewModel.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        viewModel.setRecyclerViewState(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        recyclerView = view.findViewById(R.id.result_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_COL_COUNT));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the RecyclerView state
        Parcelable state = viewModel.getRecyclerViewState();

        // Initialize an empty list
        CardAdapter adapter = new CardAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Retrieve card name from the arguments
        String cardName = SearchResultFragmentArgs.fromBundle(getArguments()).getSearchOption()
                .getCardName();

        // Get colors from the arguments
        String[] cardColors = SearchResultFragmentArgs.fromBundle(getArguments()).getSearchOption()
                .getColorOptions().toArray(new String[0]);

        // Form the query to pass to api call
        String query = buildQuery(cardName, cardColors);

        // Make the api call
        Retrofit retrofit = RetrofitClient.getInstance();
        CardResponseAPI api = retrofit.create(CardResponseAPI.class);
        Call<CardList> searchCall = api.searchCards(query);

        searchCall.enqueue(new Callback<CardList>() {
            @Override
            public void onResponse(Call<CardList> call, Response<CardList> response) {
                if (response.isSuccessful()) {
                    cardList = response.body();
                    adapter.setCards(cardList.getData());

                    // Restore the RecyclerView state after setting the data
                    if (state != null) {
                        recyclerView.getLayoutManager().onRestoreInstanceState(state);
                    }
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CardList> call, Throwable t) {
                Toast.makeText(getContext(), "No card was found ", Toast.LENGTH_SHORT).show();
                Log.e("MyApp", "API call failed", t);
            }
        });
    }

    // This method accepts card name, color etc to form a search query and return it
    public String buildQuery(String cardName, String[] cardColors) {
        StringBuilder query = new StringBuilder(cardName); // Start with the card name

        for (String color : cardColors) {
            query.append(" c:").append(color); // Append each color with the prefix " c:"
        }

        return query.toString(); // Return the final query string
    }
}