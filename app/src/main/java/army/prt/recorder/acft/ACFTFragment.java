package army.prt.recorder.acft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.CountFloatEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.acft.event.EventRecyclerAdapter;
import army.prt.recorder.databinding.FragmentAcftBinding;

public class ACFTFragment extends Fragment{
    private ACFTViewModel ACFTViewModel;
    private FragmentAcftBinding binding;
    private EventRecyclerAdapter adapter;
    public ACFTRecord record;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_acft,container,false);

        adapter = new EventRecyclerAdapter(this);

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
                CountEvent countEvent = (CountEvent) eventList.get(Event.MDL);
                record.raw_MDL = countEvent.raw;
                record.sco_MDL = countEvent.sco;
                countEvent = (CountEvent) eventList.get(Event.HPU);
                record.raw_HPU = countEvent.raw;
                record.sco_HPU = countEvent.sco;
                countEvent = (CountEvent) eventList.get(Event.LTK);
                record.raw_LTK = countEvent.raw;
                record.sco_LTK = countEvent.sco;
                CountFloatEvent floatEvent = (CountFloatEvent) eventList.get(Event.SPT);
                record.raw_SPT = floatEvent.raw;
                record.sco_SPT = floatEvent.sco;
                DurationEvent durationEvent = (DurationEvent) eventList.get(Event.SDC);
                record.duration_SDC = durationEvent.duration;
                record.sco_SDC = durationEvent.sco;
                durationEvent = (DurationEvent) eventList.get(Event.CARDIO);
                record.duration_Cardio = durationEvent.duration;
                record.sco_Cardio = durationEvent.sco;
                record.getScoreTotal();
                binding.invalidateAll();
            }
        });


        binding.setFragment(this);
        return binding.getRoot();
    }
    public void onSaveClick(View v) {
        Toast.makeText(getContext(),"Saving Record is on maintenance",Toast.LENGTH_SHORT).show();
    }

    public void updateEvent(Event event, int position){
        ACFTViewModel.updateEvent(event,position);
    }

    /*@Override
    public void onResume() {
        super.onResume();
        adapter.setEventList(ACFTViewModel.getEventList().getValue());
    }*/
}