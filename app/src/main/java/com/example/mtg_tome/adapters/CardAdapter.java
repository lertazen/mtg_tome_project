package com.example.mtg_tome.adapters;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtg_tome.R;
import com.example.mtg_tome.fragments.SearchResultFragmentDirections;
import com.example.mtg_tome.models.Card;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Card> cards;
    private static int clickPos;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardImageView;
        public ViewHolder(View view, List<Card> cards) {
            super(view);

            cardImageView = view.findViewById(R.id.cardImageView);

            // Set the onClick listener for the entire item view (the card)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPos = getAdapterPosition();
                    if (clickPos != RecyclerView.NO_POSITION) {
                        Card cardClicked = cards.get(clickPos);
                        NavDirections action = SearchResultFragmentDirections
                                .searchResultToCardDetail(cardClicked);
                        Navigation.findNavController(v).navigate(action);
                    }
                }
            });
        }
    }

    // Set the initial data for the view holder
    public CardAdapter(List<Card> cards) {
        this.cards = cards;
    }

    public void setCards(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
        notifyDataSetChanged();
    }

    // Inflate the view holder with item views
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view, cards);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the element from the dataset at this position and replace the content of the view
        Card card = cards.get(position);
        String imageUrl = card.getImageUris().getNormal();
        Picasso.get().load(imageUrl).placeholder(R.drawable.small_loading_card).into(viewHolder.cardImageView);
    }

    // Get the size of data
    @Override
    public int getItemCount() {
        return cards.size();
    }
}
