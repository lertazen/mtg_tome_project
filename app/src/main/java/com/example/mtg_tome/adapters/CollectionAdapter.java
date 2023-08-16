package com.example.mtg_tome.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtg_tome.R;
import com.example.mtg_tome.entities.CardEntity;
import com.example.mtg_tome.fragments.CollectionFragmentDirections;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<CardEntity> cards;

    public CollectionAdapter(List<CardEntity> cards) {
        this.cards = cards;
    }

    public void updateCards(List<CardEntity> cardEntities) {
        this.cards = cardEntities;
        notifyDataSetChanged();
    }

    // Provide a reference to the type of views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardNameTextView;
        private final TextView cardQtyTextView;

        public ViewHolder(View view, List<CardEntity> cards) {
            super(view);

            cardNameTextView = view.findViewById(R.id.collection_card_name);
            cardQtyTextView = view.findViewById(R.id.collection_card_number);

            // set individual click listener for each list item
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        CardEntity cardClicked = cards.get(pos);
                        NavDirections action = CollectionFragmentDirections
                                .actionCollectionFragmentToCollectionDetailFragment(cardClicked);
                        Navigation.findNavController(v).navigate(action);
                    }
                }
            });
        }

        public TextView getCardNameTextView() {
            return cardNameTextView;
        }

        public TextView getCardQtyTextView() {
            return cardQtyTextView;
        }
    }

    // Inflate the view holder with collection item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_item, parent, false);
        return new ViewHolder(view, cards);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the element from the dataset at this position and replace the content of the view
        CardEntity card = cards.get(position);
        String cardName = card.getCardName();
        int cardQty = card.getCardNumber();
        viewHolder.getCardNameTextView().setText(cardName);
        viewHolder.getCardQtyTextView().setText(Integer.toString(cardQty));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
