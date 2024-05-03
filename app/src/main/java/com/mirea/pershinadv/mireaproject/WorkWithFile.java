package com.mirea.pershinadv.mireaproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkWithFile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkWithFile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkWithFile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkWithFile newInstance(String param1, String param2) {
        WorkWithFile fragment = new WorkWithFile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText inputField;
    private String fileName = "encrypted_file.txt";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_with_file, container, false);
        inputField = rootView.findViewById(R.id.input_field);

        Button readButton = rootView.findViewById(R.id.read_button);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getTextFromFile();
                if (text != null) {
                    inputField.setText(text);
                }
            }
        });

        FloatingActionButton fabButton = rootView.findViewById(R.id.fab_button);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        return rootView;
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Сохранить файл");
        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_alert, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.input);
        String encryptedText = encrypt(inputField.getText().toString());
        input.setText(encryptedText);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveToFile(encryptedText, fileName);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    private void saveToFile(String content, String fileName) {
        FileOutputStream outputStream;
        try {
            outputStream = requireActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
            Toast.makeText(requireContext(), "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка при сохранении файла", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTextFromFile() {
        FileInputStream fin = null;
        try {
            fin = requireActivity().openFileInput(fileName);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String encryptedText = new String(bytes);
            return decrypt(encryptedText);
        } catch (IOException ex) {
            Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
                Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private String encrypt(String text) {
        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                if (c == 'z') {
                    encryptedText.append('a');
                } else if (c == 'Z') {
                    encryptedText.append('A');
                } else {
                    encryptedText.append((char) (c + 1));
                }
            } else {
                encryptedText.append(c);
            }
        }
        return encryptedText.toString();
    }

    private String decrypt(String text) {
        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                if (c == 'a') {
                    decryptedText.append('z');
                } else if (c == 'A') {
                    decryptedText.append('Z');
                } else {
                    decryptedText.append((char) (c - 1));
                }
            } else {
                decryptedText.append(c);
            }
        }
        return decryptedText.toString();
    }
}