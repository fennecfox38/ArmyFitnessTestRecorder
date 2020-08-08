package mil.army.acft.recorder.ABCP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mil.army.acft.recorder.R;

public class ABCPFragment extends Fragment {

    private ABCPViewModel ABCPViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ABCPViewModel =
                ViewModelProviders.of(this).get(ABCPViewModel.class);
        View root = inflater.inflate(R.layout.fragment_abcp, container, false);
        ABCPViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}