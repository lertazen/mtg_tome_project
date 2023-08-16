package com.example.mtg_tome.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtg_tome.R;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.entities.DeckEntity;
import com.example.mtg_tome.fragments.CollectionFragmentDirections;
import com.example.mtg_tome.fragments.DeckDetailFragmentDirections;
import com.example.mtg_tome.fragments.DecksFragmentDirections;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {
    private List<DeckEntity> decks;

    public DeckAdapter(List<DeckEntity> decks) {
        this.decks = decks;
    }

    public void updateDecks(List<DeckEntity> deckEntities) {
        this.decks = deckEntities;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deckNameTextView;

        public ViewHolder(View view, List<DeckEntity> decks) {
            super(view);

            deckNameTextView = view.findViewById(R.id.deck_card_name);

            // set individual click listener for each deck
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        DeckEntity deckClicked = decks.get(pos);

                        NavDirections action =
                                DecksFragmentDirections.actionDecksFragmentToDeckDetailFragment(deckClicked);
                        Navigation.findNavController(v).navigate(action);
                    }
                }
            });
        }
        public TextView getDeckNameTextView() {
            return deckNameTextView;
        }
    }

    // Inflate the view holder with deck item
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deck_item, parent, false);
        return new ViewHolder(view, decks);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the element from the dataset at this position and replace the content of the view
        DeckEntity deck = decks.get(position);
        String deckName = deck.getDeckName();
        holder.getDeckNameTextView().setText(deckName);
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }
}
