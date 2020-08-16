package army.prt.recorder.acft;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

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
    public ArrayList<Event> eventList = null;
    public FragmentAcftBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_acft,container,false);

        if(eventList==null){
            eventList = new ArrayList<>();
            eventList.add(new CountEvent(Event.MDL,getString(R.string.MDL),700,getString(R.string.lbs)));
            eventList.add(new CountEvent(Event.SPT,getString(R.string.SPT),150,getString(R.string.m)));
            eventList.add(new CountEvent(Event.HPU,getString(R.string.HPU),100,getString(R.string.reps)));
            eventList.add(new DurationEvent(Event.SDC,getString(R.string.SDC),5));
            eventList.add(new CountEvent(Event.LTK,getString(R.string.LTK),40,getString(R.string.reps)));
            eventList.add(new DurationEvent(Event.CARDIO,getString(R.string.Cardio),26));
        }

        EventRecyclerAdapter adapter = new EventRecyclerAdapter(this, eventList);
        binding.recyclerViewAcft.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewAcft.setAdapter(adapter);

        binding.setFragment(this);
        return binding.getRoot();
    }

    public String setQualifiedLevel(int qualifiedLevel) { return getResources().getStringArray(R.array.Level)[qualifiedLevel]; }

    public void onSaveClick(View view) {
        Snackbar.make(binding.getRoot(),"Saving Record is on maintenance", Snackbar.LENGTH_SHORT)
                .setAction("log", new View.OnClickListener() {
                    @Override public void onClick(View v) { activity.navController.navigate(R.id.navigation_log); }
                }).show();
    }

    public void onDateClick(View view) {
        final Calendar calendar = record.dateRecord;
        DatePickerDialog datePick = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                record.dateRecord = calendar;
                binding.invalidateAll();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePick.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePick.show();
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, Calendar calendar) {
        String str = String.valueOf(calendar.get(Calendar.YEAR));
        str += "-"; str += String.valueOf(calendar.get(Calendar.MONTH)+1);
        str += "-"; str += String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        editText.setText(str);
        editText.setSelection(editText.length());
    }
}