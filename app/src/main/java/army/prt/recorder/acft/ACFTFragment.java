package army.prt.recorder.acft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import army.prt.recorder.MainActivity;
import army.prt.recorder.R;
import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.CountFloatEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.acft.event.EventRecyclerAdapter;
import army.prt.recorder.databinding.FragmentAcftBinding;

public class ACFTFragment extends Fragment{
    public ACFTViewModel ACFTViewModel;
    private FragmentAcftBinding binding;
    public ACFTRecord record;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_acft,container,false);

        EventRecyclerAdapter adapter = new EventRecyclerAdapter(this);

        ACFTViewModel = ViewModelProviders.of(this).get(ACFTViewModel.class);
        if(ACFTViewModel.getEventList().getValue()==null){
            ArrayList<Event> list = new ArrayList<>();
            list.add(new CountEvent(Event.MDL,getString(R.string.MDL),700,getString(R.string.lbs)));
            list.add(new CountFloatEvent(Event.SPT,getString(R.string.SPT),150,getString(R.string.m)));
            list.add(new CountEvent(Event.HPU,getString(R.string.HPU),100,getString(R.string.reps)));
            list.add(new DurationEvent(Event.SDC,getString(R.string.SDC),5));
            list.add(new CountEvent(Event.LTK,getString(R.string.LTK),40,getString(R.string.reps)));
            list.add(new DurationEvent(Event.CARDIO,getString(R.string.Cardio),26));
            ACFTViewModel.setEventList(list);
        }
        adapter.setEventList(ACFTViewModel.getEventList().getValue());
        binding.recyclerViewAcft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewAcft.setAdapter(adapter);

        record = new ACFTRecord();
        ACFTViewModel.getEventList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override public void onChanged(ArrayList<Event> eventList) {
                record.updateRecord(eventList);
                binding.invalidateAll();
            }
        });

        binding.setFragment(this);
        return binding.getRoot();
    }

    public String setQualifiedLevel(int qualifiedLevel) { return getResources().getStringArray(R.array.Level)[qualifiedLevel]; }

    public void onSaveClick(View view) {
        Snackbar.make(binding.getRoot(),"Saving Record is on maintenance", Snackbar.LENGTH_SHORT)
                .setAction("log", new View.OnClickListener() {
                    @Override public void onClick(View v) { ((MainActivity) requireActivity()).navController.navigate(R.id.navigation_log); }
                }).show();
    }

    /*@Override
    public void onResume() {
        super.onResume();
        adapter.setEventList(ACFTViewModel.getEventList().getValue());
    }*/
}