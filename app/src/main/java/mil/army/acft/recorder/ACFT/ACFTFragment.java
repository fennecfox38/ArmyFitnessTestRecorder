package mil.army.acft.recorder.ACFT;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import mil.army.acft.recorder.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL;

public class ACFTFragment extends Fragment{
    private ACFTViewModel ACFTViewModel;
    private LayoutInflater inflater;
    private View root;
    private EventRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState){
        inflater = layoutInflater;

        root = inflater.inflate(R.layout.fragment_acft, container, false);
        adapter = new EventRecyclerAdapter(requireContext());
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView_acft);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getContext(),"Saving Record is on maintenance",Toast.LENGTH_SHORT).show();
            }
        });

        ACFTViewModel = ViewModelProviders.of(this).get(ACFTViewModel.class);
        ACFTViewModel.getACFTRecord().observe(getViewLifecycleOwner(), new Observer<ACFTRecord>() {
            @Override public void onChanged(@Nullable ACFTRecord record) {

            }
        });


        return root;
    }


}