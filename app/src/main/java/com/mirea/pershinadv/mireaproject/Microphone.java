package com.mirea.pershinadv.mireaproject;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Microphone#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Microphone extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Microphone() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Microphone.
     */
    // TODO: Rename and change types and number of parameters
    public static Microphone newInstance(String param1, String param2) {
        Microphone fragment = new Microphone();
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
    private static final int AUDIO_PERMISSION_REQUEST_CODE = 200;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private TextView Amplitude;
    private Button ButtonStart;
    private Button ButtonStop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_microphone, container, false);
        Amplitude = rootView.findViewById(R.id.Amplitude);
        ButtonStart = rootView.findViewById(R.id.buttonStart);
        ButtonStop = rootView.findViewById(R.id.buttonStop);
        ButtonStart.setOnClickListener(this);
        ButtonStop.setOnClickListener(this);
        return rootView;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.buttonStart) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_REQUEST_CODE);
            } else {
                startRecording();
            }
        } else if (viewId == R.id.buttonStop) {
            stopRecording();
        }
    }


    private void startRecording() {
        if (!isRecording) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

            isRecording = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        audioRecord.startRecording();
                        while (isRecording) {
                            int amplitude = 0;
                            int read = audioRecord.read(buffer, 0, BUFFER_SIZE);
                            for (int i = 0; i < read; i += 2) {
                                short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                                if (sample > amplitude) {
                                    amplitude = sample;
                                }
                            }
                            final int finalAmplitude = amplitude;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Amplitude.setText("Амплитуда: " + finalAmplitude);
                                }
                            });
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(requireContext(), "Для работы приложения необходимо разрешение на запись аудио.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }
}