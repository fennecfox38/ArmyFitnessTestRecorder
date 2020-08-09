package army.prts.recorder.logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import army.prts.recorder.R;

public class LogFragment extends Fragment {

    private LogViewModel logViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_log, container, false);
        logViewModel = ViewModelProviders.of(this).get(LogViewModel.class);
        logViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}