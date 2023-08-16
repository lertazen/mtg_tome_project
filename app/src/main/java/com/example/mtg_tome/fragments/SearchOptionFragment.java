package com.example.mtg_tome.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mtg_tome.R;
import com.example.mtg_tome.models.SearchOption;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchOptionFragment extends Fragment {
    EditText cardNameEditText;
    Button colorBtn;
    Button searchBtn;
    String inputCardName;
//    ArrayList<String> selectedColors;
    SearchOption searchOption;
    TextView colorText;
    ArrayList<Boolean> selectedColors = new ArrayList<>();
    ArrayList<String> selectedColorNames = new ArrayList<>();

    public SearchOptionFragment() {
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
        return inflater.inflate(R.layout.fragment_search_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchBtn = view.findViewById(R.id.search_button);
        cardNameEditText = view.findViewById(R.id.editCardName);
        colorBtn = view.findViewById(R.id.color_button);
        colorText = view.findViewById(R.id.textView_colors);

        // set initial state of colors related variables
        String[] colorOptions = getResources().getStringArray(R.array.card_colors);
        selectedColors = new ArrayList<>();
        selectedColors.addAll(Arrays.asList(false, false, false,false,false,false ));
//        ArrayList<String> inputColors = new ArrayList<>();

        // Get color choice from the user
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorOptionsDialog(colorOptions, colorText);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCardName = cardNameEditText.getText().toString();
                searchOption = new SearchOption(inputCardName, selectedColorNames);

                if (inputCardName.equals("")) {
                    Toast.makeText(getContext(), "Please select at least one option to search", Toast.LENGTH_SHORT).show();
                } else {
                    NavDirections action = SearchOptionFragmentDirections.searchOptionToSearchResult(searchOption);
                    Navigation.findNavController(v).navigate(action);
                }
            }
        });
    }

    private void showColorOptionsDialog(String[] colorOptions, TextView colorText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the checked items in an array of booleans
        boolean[] checkedColors = new boolean[selectedColors.size()];
        for (int i = 0; i < selectedColors.size(); i++) {
            checkedColors[i] = selectedColors.get(i);
        }

        builder.setTitle("Select Colors ")
                .setMultiChoiceItems(R.array.card_colors, checkedColors,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                               if (isChecked && !selectedColors.get(which)) {
                                    selectedColors.set(which, true);
                               } else if (!isChecked) {
                                    selectedColors.set(which, false);
                               }
                            }
                        })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedColorNames.clear();

                        // Add selected color names based on checked items
                        for (int i = 0; i < selectedColors.size(); i++) {
                            if (selectedColors.get(i)) {
                                selectedColorNames.add(colorOptions[i]);
                            }
                        }
                        colorText.setText(String.join(", ", selectedColorNames));
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }
}
