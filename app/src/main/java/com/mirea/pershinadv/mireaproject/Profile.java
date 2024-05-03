package com.mirea.pershinadv.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText editTextName;
    private EditText editTextWeight;
    private EditText editTextAge;
    private EditText editTextHeight;
    private Button button;

    private static final String PREF_NAME = "my_prefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_HEIGHT = "height";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextHeight = view.findViewById(R.id.editTextHeight);
        button = view.findViewById(R.id.button);

        String savedName = sharedPreferences.getString(KEY_NAME, "");
        int savedWeight = sharedPreferences.getInt(KEY_WEIGHT, 0);
        int savedAge = sharedPreferences.getInt(KEY_AGE, 0);
        int savedHeight = sharedPreferences.getInt(KEY_HEIGHT, 0);

        editTextName.setText(savedName);
        editTextWeight.setText(String.valueOf(savedWeight));
        editTextAge.setText(String.valueOf(savedAge));
        editTextHeight.setText(String.valueOf(savedHeight));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        return view;
    }

    private void savePreferences() {
        String name = editTextName.getText().toString();
        int weight = Integer.parseInt(editTextWeight.getText().toString());
        int age = Integer.parseInt(editTextAge.getText().toString());
        int height = Integer.parseInt(editTextHeight.getText().toString());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putInt(KEY_WEIGHT, weight);
        editor.putInt(KEY_AGE, age);
        editor.putInt(KEY_HEIGHT, height);
        editor.apply();

        if (sharedPreferences.contains(KEY_NAME) && sharedPreferences.contains(KEY_AGE) && sharedPreferences.contains(KEY_WEIGHT) && sharedPreferences.contains(KEY_HEIGHT)) {
            showToast("Данные сохранены!");
        } else {
            showToast("Ошибка сохранения данных.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}