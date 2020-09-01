package mil.army.fitnesstest.recorder.apft;

import android.app.DatePickerDialog;
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


import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.databinding.FragmentApftBinding;
import mil.army.fitnesstest.recorder.MainActivity;
import mil.army.fitnesstest.recorder.Sex;
import mil.army.fitnesstest.recorder.apft.event.CountEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationEvent;
import mil.army.fitnesstest.recorder.apft.event.Event;

public class APFTFragment extends Fragment {
    private MainActivity activity;
    public APFTRecord<Event> record = new APFTRecord<>();
    public MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<>(null);
    public EventRecyclerAdapter adapter;
    public FragmentApftBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apft, container, false);

        if(eventList.getValue()==null){
            ArrayList<Event> list = new ArrayList<>();
            list.add(new CountEvent(Event.PU,getString(R.string.PU),100));
            list.add(new CountEvent(Event.SU,getString(R.string.SU),100));
            list.add(new DurationEvent(Event.CARDIO,getString(R.string.Cardio),59));
            //loadData(record,list);
            eventList.setValue(list);
        }

        adapter = new EventRecyclerAdapter(requireContext(), eventList);
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
        //saveData(record);
        super.onDestroyView();
    }

    public int getPassedColor(boolean isPassed){ return getResources().getColor(isPassed ? R.color.passed: R.color.failed); }
    public String getPassed(boolean isPassed){ return getString(isPassed ? R.string.pass: R.string.fail); }

    public void onSaveClick(View view) {
        /*ACFTDBHelper dbHelper = new ACFTDBHelper(requireContext());
        dbHelper.insertRecord(record);
        dbHelper.close();
        Snackbar.make(binding.getRoot(),"Saved on log.", Snackbar.LENGTH_SHORT)
                .setAction("log", v -> activity.navController.navigate(R.id.navigation_log)).show();*/
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