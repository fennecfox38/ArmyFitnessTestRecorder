package army.prt.recorder.acft;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import army.prt.recorder.MainActivity;
import army.prt.recorder.R;
import army.prt.recorder.acft.event.CountEvent;
import army.prt.recorder.acft.event.DurationEvent;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.acft.event.EventRecyclerAdapter;
import army.prt.recorder.databinding.FragmentAcftBinding;

public class ACFTFragment extends Fragment{
    private MainActivity activity;
    public ACFTRecord record = new ACFTRecord();
    public MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<>(null);
    public FragmentAcftBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_acft,container,false);

        if(eventList.getValue()==null){
            ArrayList<Event> list = new ArrayList<>();
            list.add(new CountEvent(Event.MDL,getString(R.string.MDL),700,getString(R.string.lbs)));
            list.add(new CountEvent(Event.SPT,getString(R.string.SPT),150,getString(R.string.m)));
            list.add(new CountEvent(Event.HPU,getString(R.string.HPU),100,getString(R.string.reps)));
            list.add(new DurationEvent(Event.SDC,getString(R.string.SDC),5));
            list.add(new CountEvent(Event.LTK,getString(R.string.LTK),40,getString(R.string.reps)));
            list.add(new DurationEvent(Event.CARDIO,getString(R.string.Cardio),26));
            loadData(record,list);
            eventList.setValue(list);
        }

        EventRecyclerAdapter adapter = new EventRecyclerAdapter(this, eventList);
        binding.recyclerViewAcft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewAcft.setAdapter(adapter);

        eventList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override public void onChanged(ArrayList<Event> events) {
                record.updateRecord(events);
                binding.invalidateAll();
            }
        });

        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override public void onResume() {
        eventList.setValue(loadData(record,eventList.getValue()));
        super.onResume();
    }
    @Override public void onPause() {
        saveData(record);
        super.onPause();
    }
    @Override public void onDestroyView() {
        saveData(record);
        super.onDestroyView();
    }

    private ArrayList<Event> loadData(ACFTRecord record, ArrayList<Event> list){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ACFTRecord", Activity.MODE_PRIVATE);
        for(int i=0; i<6; ++i){
            record.raw[i] = sharedPreferences.getInt("raw_"+i,0);
            record.sco[i] = sharedPreferences.getInt("sco_"+i,0);
        }
        record.cardio_Alter = sharedPreferences.getInt("cardio_Alter",0);
        record.sco_total = sharedPreferences.getInt("sco_total", 0);
        record.qualifiedLevel = sharedPreferences.getInt("qualifiedLevel", record.qualifiedLevel);
        record.stringToDate(sharedPreferences.getString("dateRecord","2020-08-18"));
        record.restoreEventList(list);
        return list;
    }
    private void saveData(ACFTRecord record){
        SharedPreferences.Editor editor = activity.getSharedPreferences("ACFTRecord", Activity.MODE_PRIVATE).edit();
        for(int i=0; i<6; ++i){
            editor.putInt("raw_"+ i, record.raw[i]);
            editor.putInt("sco_"+ i, record.sco[i]);
        }
        editor.putInt("cardio_Alter", record.cardio_Alter);
        editor.putInt("sco_total", record.sco_total);
        editor.putInt("qualifiedLevel", record.qualifiedLevel);
        editor.putString("dateRecord", record.dateToString());
        editor.commit();
    }

    public String setQualifiedLevel(int qualifiedLevel) { return getResources().getStringArray(R.array.Level)[qualifiedLevel]; }

    public void onSaveClick(View view) {
        Snackbar.make(binding.getRoot(),"Saving Record is on maintenance", Snackbar.LENGTH_SHORT)
                .setAction("log", new View.OnClickListener() {
                    @Override public void onClick(View v) { activity.navController.navigate(R.id.navigation_log); }
                }).show();
    }

    public void onDateClick(View view) {
        DatePickerDialog datePick = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                record.year = year; record.month = month+1;
                record.day = dayOfMonth;
                binding.invalidateAll();
            }
        }, record.year, record.month-1, record.day);
        datePick.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePick.show();
    }

}