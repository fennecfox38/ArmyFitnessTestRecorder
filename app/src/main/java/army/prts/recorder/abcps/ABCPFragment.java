package army.prts.recorder.abcps;

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

public class ABCPFragment extends Fragment {

    private ABCPViewModel ABCPViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_abcp, container, false);
        ABCPViewModel = ViewModelProviders.of(this).get(ABCPViewModel.class);
        ABCPViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override public void onChanged(@Nullable String s) {
                //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}