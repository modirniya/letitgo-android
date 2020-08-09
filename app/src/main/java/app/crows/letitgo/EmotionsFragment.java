package app.crows.letitgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class EmotionsFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public EmotionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button btJoy = parent.findViewById(R.id.btJoy);
        btJoy.setOnClickListener(this);

        Button btSadness = parent.findViewById(R.id.btSadness);
        btSadness.setOnClickListener(this);

        Button btAnger = parent.findViewById(R.id.btAnger);
        btAnger.setOnClickListener(this);

        Button btFear = parent.findViewById(R.id.btFear);
        btFear.setOnClickListener(this);

        Button btDisgust = parent.findViewById(R.id.btDisgust);
        btDisgust.setOnClickListener(this);

        Button btTrust = parent.findViewById(R.id.btTrust);
        btTrust.setOnClickListener(this);

        Button btSurprise = parent.findViewById(R.id.btSurprise);
        btSurprise.setOnClickListener(this);

        Button btAnticipation = parent.findViewById(R.id.btAnticipation);
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
        assert mainActivity != null;
        mainActivity.onUserAction(code);

    }
}
