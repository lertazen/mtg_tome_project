package com.example.mtg_tome.view_model;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

public class SearchResultViewModel extends ViewModel {
    private Parcelable recyclerViewState = null;

    public Parcelable getRecyclerViewState() {
        return recyclerViewState;
    }

    public void setRecyclerViewState(Parcelable state) {
        this.recyclerViewState = state;
    }
}
