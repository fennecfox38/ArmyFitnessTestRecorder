package mil.army.fitnesstest.recorder.apft;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.databinding.FragmentApftBinding;
import mil.army.fitnesstest.recorder.MainActivity;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;
import mil.army.fitnesstest.recorder.apft.event.CountAPFTEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationAPFTEvent;

public class APFTFragment extends Fragment {
    private MainActivity activity;
    public APFTRecord<APFTEvent> record = new APFTRecord<>();
    public MutableLiveData<ArrayList<APFTEvent>> eventList = new MutableLiveData<>(null);
    public APFTEventRecyclerAdapter adapter;
    public FragmentApftBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apft, container, false);

        if(eventList.getValue()==null){
            ArrayList<APFTEvent> list = new ArrayList<>();
            list.add(new CountAPFTEvent(APFTEvent.PU,getString(R.string.PU),100));
            list.add(new CountAPFTEvent(APFTEvent.SU,getString(R.string.SU),100));
            list.add(new DurationAPFTEvent(APFTEvent.CARDIO,getString(R.string.Cardio),59));
            loadData(record,list);
            eventList.setValue(list);
        }

        adapter = new APFTEventRecyclerAdapter(requireContext(), eventList);
        binding.recyclerViewApft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewApft.setAdapter(adapter);

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

    public void loadData(APFTRecord<APFTEvent> record, ArrayList<APFTEvent> list){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("APFTRecord", Activity.MODE_PRIVATE);

        record.raw_PU = sharedPreferences.getInt("raw_PU",0);
        record.raw_SU = sharedPreferences.getInt("raw_SU",0);
        record.raw_Cardio.setTotalInSec(sharedPreferences.getInt("raw_Cardio",0));
        for(int i=0; i<3; ++i) record.sco[i] = sharedPreferences.getInt("sco_"+i,0);

        record.sex = Sex.valueOf(sharedPreferences.getInt("Sex",0));
        record.ageGroup = APFTRecord.AgeGroup.findById(sharedPreferences.getInt("AgeGroup",0));
        record.cardioAlter = APFTCardioAlter.findById(sharedPreferences.getInt("cardioAlter",0));
        record.sco_total = sharedPreferences.getInt("sco_total", 0);
        record.isPassed = sharedPreferences.getBoolean("isPassed",false);
        record.restoreList(list);
    }

    public void saveData(APFTRecord<APFTEvent> record){
        SharedPreferences.Editor editor = activity.getSharedPreferences("APFTRecord", Activity.MODE_PRIVATE).edit();

        editor.putInt("raw_PU",record.raw_PU);
        editor.putInt("raw_SU",record.raw_SU);
        editor.putInt("raw_Cardio",record.raw_Cardio.getTotalInSec());
        for(int i=0; i<3; ++i) editor.putInt("sco_"+i, record.sco[i]);

        editor.putInt("Sex",record.sex.ordinal());
        editor.putInt("AgeGroup",record.ageGroup.ordinal());
        editor.putInt("cardioAlter",record.cardioAlter.ordinal());
        editor.putInt("sco_total",record.sco_total);
        editor.putBoolean("isPassed",record.isPassed);

        editor.commit();
    }

    public int getPassedColor(boolean isPassed){ return getResources().getColor(isPassed ? R.color.passed: R.color.failed); }
    public String getPassed(boolean isPassed){ return getString(isPassed ? R.string.pass: R.string.fail); }

    public void onSaveClick(View view) {
        APFTDBHelper dbHelper = new APFTDBHelper(requireContext());
        dbHelper.insertRecord(record);
        dbHelper.close();
        Snackbar.make(binding.getRoot(),"Saved on log.", Snackbar.LENGTH_SHORT)
                .setAction("log", v -> activity.navController.navigate(R.id.navigation_log)).show();
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

    public void onSexChanged(RadioGroup radioGroup, int checkedId){
        record.sex = (checkedId==R.id.rBtn_male? Sex.Male : Sex.Female);
        record.validateEvent(eventList);
        adapter.notifyDataSetChanged();
        binding.invalidateAll();
    }

    public void onAgeSelected(AdapterView<?> parent, View view, int position, long id) {
        record.ageGroup = APFTRecord.AgeGroup.findById(position);
        record.validateEvent(eventList);
        adapter.notifyDataSetChanged();
        binding.invalidateAll();
    }
}