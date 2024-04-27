package com.mirea.pershinadv.mireaproject;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Camera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Camera extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ImageView img_one, img_two;
    private Button buttonPhoto, buttonCollage;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private File photoFile1 = null, photoFile2 = null;
    private boolean isFirstPhoto = true;

    public Camera() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Camera.
     */
    // TODO: Rename and change types and number of parameters
    public static Camera newInstance(String param1, String param2) {
        Camera fragment = new Camera();
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

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        takePhoto();
                    } else {
                        Toast.makeText(getContext(), "Требуется разрешение для доступа к камере", Toast.LENGTH_LONG).show();
                    }
                });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bitmap bitmap = isFirstPhoto ? BitmapFactory.decodeFile(photoFile1.getAbsolutePath()) : BitmapFactory.decodeFile(photoFile2.getAbsolutePath());
                if (isFirstPhoto) {
                    img_one.setImageBitmap(bitmap);
                    isFirstPhoto = false;
                } else {
                    img_two.setImageBitmap(bitmap);
                }
            } else {
                Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        img_one = view.findViewById(R.id.img_one);
        img_two = view.findViewById(R.id.img_two);
        buttonPhoto = view.findViewById(R.id.buttonPhoto);
        buttonCollage = view.findViewById(R.id.buttonCollage);

        buttonPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });

        buttonCollage.setOnClickListener(v -> createCollage());

        return view;
    }

    private void takePhoto() {
        try {
            File photoFile = createImageFile();
            if (isFirstPhoto) {
                photoFile1 = photoFile;
            } else {
                photoFile2 = photoFile;
            }
            Uri photoURI = FileProvider.getUriForFile(requireContext(), "com.mirea.pershinadv.mireaproject.fileprovider", photoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureLauncher.launch(takePictureIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Произошла ошибка при создании коллажа", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void createCollage() {
        if (photoFile1 == null || photoFile2 == null) {
            Toast.makeText(getContext(), "Сделайте, пожалуйста, два фото", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Bitmap firstImage = BitmapFactory.decodeFile(photoFile1.getAbsolutePath());
            Bitmap secondImage =             BitmapFactory.decodeFile(photoFile2.getAbsolutePath());

            int width = firstImage.getWidth() + secondImage.getWidth();
            int height = Math.max(firstImage.getHeight(), secondImage.getHeight());

            Bitmap collageBitmap = Bitmap.createBitmap(width, height, firstImage.getConfig());
            Canvas canvas = new Canvas(collageBitmap);

            canvas.drawBitmap(firstImage, 0f, 0f, null);
            canvas.drawBitmap(secondImage, firstImage.getWidth(), 0f, null);

            img_one.setImageBitmap(collageBitmap);

            saveImageToGallery(collageBitmap);

            Toast.makeText(getContext(), "Коллаж создан и сохранён", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при создании коллажа", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "collage_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = getContext().getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(getContext(), "Коллаж сохранён в галерее", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при сохранении коллажа", Toast.LENGTH_SHORT).show();
        }
    }
}


