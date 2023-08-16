package com.example.mtg_tome.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtg_tome.R;
import com.example.mtg_tome.fragments.DeckDetailFragmentDirections;
import com.example.mtg_tome.interfaces.DeckCardCrossRefDAO;

import java.util.List;

public class DeckDetailAdapter extends RecyclerView.Adapter<DeckDetailAdapter.ViewHolder> {
    private List<DeckCardCrossRefDAO.DeckCardDetail> deckCardDetails;

    public DeckDetailAdapter(List<DeckCardCrossRefDAO.DeckCardDetail> deckCardDetails) {
        this.deckCardDetails = deckCardDetails;
    }

    public void updateRefs(List<DeckCardCrossRefDAO.DeckCardDetail> deckCardDetail) {
        this.deckCardDetails = deckCardDetail;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardNameTextView;
        private final TextView cardQtyTextView;

        public ViewHolder(View view, List<DeckCardCrossRefDAO.DeckCardDetail> refs) {
            super(view);

            cardNameTextView = view.findViewById(R.id.collection_card_name);
            cardQtyTextView = view.findViewById(R.id.collection_card_number);

            // TODO: set click listener for each card in the deck
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        int clickedCardId = refs.get(pos).cardId;
                        int deckId = refs.get(pos).deckId;
                        Bundle bundle = new Bundle();
                        bundle.putInt("card_id", clickedCardId);
                        bundle.putInt("deck_id", deckId);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_deckDetailFragment_to_deckCardDetailFragment,
                                        bundle);
                    }
                }
            });
        }

        public TextView getCardNameTextView() {return cardNameTextView;}
        public TextView getCardQtyTextView() {return cardQtyTextView;}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_item, parent, false);
        return new ViewHolder(view, deckCardDetails);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeckCardCrossRefDAO.DeckCardDetail deckCardDetail = deckCardDetails.get(position);
        String cardName = deckCardDetail.cardName;
        int cardQty = deckCardDetail.cardQuantity;
        holder.getCardNameTextView().setText(cardName);
        holder.getCardQtyTextView().setText(Integer.toString(cardQty));
    }

    @Override
    public int getItemCount() {
        return deckCardDetails.size();
    }
}
