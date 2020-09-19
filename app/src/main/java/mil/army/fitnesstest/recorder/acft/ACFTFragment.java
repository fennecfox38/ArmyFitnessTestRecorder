package mil.army.fitnesstest.recorder.acft;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.MainActivity;
import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.acft.event.ACFTCardioAlter;
import mil.army.fitnesstest.recorder.acft.event.ACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.CountACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.DurationACFTEvent;
import mil.army.fitnesstest.databinding.FragmentAcftBinding;

public class ACFTFragment extends Fragment{
    private MainActivity activity;
    public ACFTRecord record = new ACFTRecord();
    public MutableLiveData<ArrayList<ACFTEvent>> eventList = new MutableLiveData<>(null);
    public FragmentAcftBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_acft,container,false);

        if(eventList.getValue()==null){
            ArrayList<ACFTEvent> list = new ArrayList<>();
            list.add(new CountACFTEvent(ACFTEvent.MDL,getString(R.string.MDL),700,getString(R.string.lbs)));
            list.add(new CountACFTEvent(ACFTEvent.SPT,getString(R.string.SPT),150,getString(R.string.m)));
            list.add(new CountACFTEvent(ACFTEvent.HPU,getString(R.string.HPU),100,getString(R.string.reps)));
            list.add(new DurationACFTEvent(ACFTEvent.SDC,getString(R.string.SDC),5));
            list.add(new CountACFTEvent(ACFTEvent.LTK,getString(R.string.LTK),40,getString(R.string.reps)));
            list.add(new DurationACFTEvent(ACFTEvent.CARDIO,getString(R.string.Cardio),26));
            loadData(record,list);
            eventList.setValue(list);
        }

        ACFTEventRecyclerAdapter adapter = new ACFTEventRecyclerAdapter(requireContext(), eventList);
        binding.recyclerViewAcft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewAcft.setAdapter(adapter);

        eventList.observe(getViewLifecycleOwner(), events -> {
            record.invalidate(events);
            binding.invalidateAll();
        });

        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override public void onDestroyView() {
        saveData(record);
        super.onDestroyView();
    }

    private void loadData(ACFTRecord record, ArrayList<ACFTEvent> list){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ACFTRecord", Activity.MODE_PRIVATE);

        record.raw_0 = sharedPreferences.getInt("raw_0",0);
        record.raw_1 = sharedPreferences.getFloat("raw_1",0.0f);
        record.raw_2 = sharedPreferences.getInt("raw_2",0);
        record.raw_3.setTotalInSec(sharedPreferences.getInt("raw_3",0));
        record.raw_4 = sharedPreferences.getInt("raw_4",0);
        record.raw_5.setTotalInSec(sharedPreferences.getInt("raw_5",0));

        for(int i=0; i<6; ++i) record.sco[i] = sharedPreferences.getInt("sco_"+i,0);

        record.cardioAlter = ACFTCardioAlter.valueOf(sharedPreferences.getInt("cardioAlter",0));
        record.sco_total = sharedPreferences.getInt("sco_total", 0);
        record.qualifiedLevel = Level.valueOf(sharedPreferences.getInt("qualifiedLevel", 0));
        record.mos = ACFTRecord.MOS.valueOf(sharedPreferences.getString("MOS", ACFTRecord.MOS.Moderate.name()));
        record.isPassed = sharedPreferences.getBoolean("isPassed",false);
        record.restoreList(list);
    }
    private void saveData(ACFTRecord record){
        SharedPreferences.Editor editor = activity.getSharedPreferences("ACFTRecord", Activity.MODE_PRIVATE).edit();

        editor.putInt("raw_0", record.raw_0);
        editor.putFloat("raw_1", record.raw_1);
        editor.putInt("raw_2", record.raw_2);
        editor.putInt("raw_3", record.raw_3.getTotalInSec());
        editor.putInt("raw_4", record.raw_4);
        editor.putInt("raw_5", record.raw_5.getTotalInSec());
        for(int i=0; i<6; ++i) editor.putInt("sco_"+ i, record.sco[i]);

        editor.putInt("cardioAlter", record.cardioAlter.ordinal());
        editor.putInt("sco_total", record.sco_total);
        editor.putInt("qualifiedLevel", record.qualifiedLevel.ordinal());
        editor.putString("MOS",record.mos.name());
        editor.putBoolean("isPassed",record.isPassed);
        editor.commit();
    }

    public void onSaveClick(View view) {
        ACFTDBHandler.insertRecord(requireContext(), record);
        Snackbar.make(binding.getRoot(),R.string.recordSaved, Snackbar.LENGTH_SHORT)
                .setAction(R.string.title_log, v -> activity.navController.navigate(R.id.navigation_log)).show();
    }

    public void onDateClick(View view) {
        DatePickerDialog datePick = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
            record.year = year; record.month = month+1;
            record.day = dayOfMonth;
            binding.invalidateAll();
        }, record.year, record.month-1, record.day);
        datePick.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePick.show();
    }

    public void onMOSChanged(RadioGroup radioGroup, int checkedId){
        switch (checkedId){
            case R.id.rBtn_moderate: record.mos = ACFTRecord.MOS.Moderate;break;
            case R.id.rBtn_significant: record.mos = ACFTRecord.MOS.Significant;break;
            case R.id.rBtn_heavy: record.mos = ACFTRecord.MOS.Heavy; break;
        }
        record.invalidate(null);
        binding.invalidateAll();
    }

}