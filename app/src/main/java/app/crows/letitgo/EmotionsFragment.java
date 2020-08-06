package app.crows.letitgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmotionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmotionsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMOTION = "emotion";
    private static final String ARG_LANG = "lang";

    // TODO: Rename and change types of parameters
    private String mEmotion, mLang;
    private Button btJoy, btSadness, btFear, btAnger, btAnticipation, btSurprise, btDisgust, btTrust;

    public EmotionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param emotion Parameter 1.
     * @param lang Parameter 2.
     * @return A new instance of fragment EmotionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmotionsFragment newInstance(String emotion, String lang) {
        EmotionsFragment fragment = new EmotionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMOTION, emotion);
        args.putString(ARG_LANG, lang);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmotion = getArguments().getString(ARG_EMOTION);
            mLang = getArguments().getString(ARG_LANG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_emotions, container, false);
        initialize(parent);
        return parent;
    }

    private void initialize(View parent) {
        btJoy = parent.findViewById(R.id.btJoy);
        btJoy.setOnClickListener(this);

        btSadness = parent.findViewById(R.id.btSadness);
        btSadness.setOnClickListener(this);

        btAnger = parent.findViewById(R.id.btAnger);
        btAnger.setOnClickListener(this);

        btFear = parent.findViewById(R.id.btFear);
        btFear.setOnClickListener(this);

        btDisgust = parent.findViewById(R.id.btDisgust);
        btDisgust.setOnClickListener(this);

        btTrust = parent.findViewById(R.id.btTrust);
        btTrust.setOnClickListener(this);

        btSurprise = parent.findViewById(R.id.btSurprise);
        btSurprise.setOnClickListener(this);

        btAnticipation = parent.findViewById(R.id.btAnticipation);
        btAnticipation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        int code = 0;
        switch (v.getId()) {
            case R.id.btJoy:
                code = 1;
                break;
            case R.id.btSadness:
                code = 2;
                break;
            case R.id.btTrust:
                code = 3;
                break;
            case R.id.btDisgust:
                code = 4;
                break;
            case R.id.btFear:
                code = 5;
                break;
            case R.id.btAnger:
                code = 6;
                break;
            case R.id.btSurprise:
                code = 7;
                break;
            case R.id.btAnticipation:
                code = 8;
                break;
        }
        mainActivity.onUserAction(code);

    }
}
