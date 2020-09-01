package mil.army.fitnesstest.recorder.abcp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import mil.army.fitnesstest.recorder.ABCPRecord;
import mil.army.fitnesstest.recorder.MainActivity;
import mil.army.fitnesstest.R;
import mil.army.fitnesstest.databinding.FragmentAbcpBinding;
import mil.army.fitnesstest.recorder.ABCPDBHelper;
import mil.army.fitnesstest.recorder.Sex;

public class ABCPFragment extends Fragment {
    private MainActivity activity;
    public ABCPRecord<Item> record = new ABCPRecord<>();
    public MutableLiveData<ArrayList<Item>> itemList = new MutableLiveData<>(null);
    private ItemRecyclerAdapter adapter;
    public FragmentAbcpBinding binding;
    public static Item HIPS = new Item(Item.HIPS, "Hips", "inches",0,80);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_abcp,container,false);

        if(itemList.getValue()==null){
            ArrayList<Item> list = new ArrayList<>();
            list.add(new Item(Item.HEIGHT, getString(R.string.height), getString(R.string.inches),0,78));
            list.add(new Item(Item.WEIGHT, getString(R.string.weight), getString(R.string.lbs),0,220));
            list.add(new Item(Item.NECK, getString(R.string.neck), getString(R.string.inches),0,60));
            list.add(new Item(Item.ABDOMEN_WAIST, getString(R.string.abdomen), getString(R.string.inches),0,80));
            loadData(record, list);
            itemList.setValue(list);
        }

        adapter = new ItemRecyclerAdapter(requireContext(), itemList);
        binding.recyclerViewAbcp.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)) ;
        binding.recyclerViewAbcp.setAdapter(adapter);

        itemList.observe(getViewLifecycleOwner(), items -> {
            record.validate(items);
            adapter.notifyDataSetChanged();
            binding.invalidateAll();
        });
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override public void onDestroyView() {
        saveData(record);
        super.onDestroyView();
    }

    private void loadData(ABCPRecord<Item> record, ArrayList<Item> list){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ABCPRecord", Activity.MODE_PRIVATE);
        record.sex = Sex.valueOf(sharedPreferences.getInt("Sex", 0));
        record.ageGroup = ABCPRecord.AgeGroup.findById(sharedPreferences.getInt("AgeGroup",0));
        record.height = sharedPreferences.getFloat("Height",58.f);
        record.weight = sharedPreferences.getInt("Weight",90);
        record.neck = sharedPreferences.getFloat("Neck",10.f);
        record.abdomen_waist = sharedPreferences.getFloat("Abdomen_Waist",20.f);
        if(record.sex == Sex.Female){
            record.hips = sharedPreferences.getFloat("Hips",20.f);
            list.get(Item.ABDOMEN_WAIST).title = getString(R.string.waist);
            list.add(HIPS);
        }
        record.restoreList(list);
    }

    private void saveData(ABCPRecord<Item> record){
        SharedPreferences.Editor editor = activity.getSharedPreferences("ABCPRecord", Activity.MODE_PRIVATE).edit();
        editor.clear();

        editor.putInt("Sex",record.sex.ordinal());
        editor.putInt("AgeGroup",record.ageGroup.ordinal());
        editor.putFloat("Height",record.height);
        editor.putInt("Weight",record.weight);
        editor.putFloat("Neck",record.neck);
        editor.putFloat("Abdomen_Waist",record.abdomen_waist);
        if(record.sex == Sex.Female)
            editor.putFloat("Hips",record.hips);
        editor.putBoolean("HeightWeightPassed",record.height_weight);
        editor.putFloat("BodyFatPercentage",record.bodyFatPercentage);
        editor.putBoolean("BodyFatPassed",record.bodyFatPass);
        editor.putBoolean("TotalPass",record.isPassed);

        editor.commit();
    }

    public int getPassedColor(boolean isPassed){ return getResources().getColor(isPassed ? R.color.passed: R.color.failed); }
    public String getPassed(boolean isPassed){ return getString(isPassed ? R.string.pass: R.string.fail); }

    public void onSaveClick(View view) {
        ABCPDBHelper dbHelper = new ABCPDBHelper(requireContext());
        dbHelper.insertRecord(record);
        dbHelper.close();
        //Bundle bundle = new Bundle(); bundle.putInt("page",TAB_ABCP);
        Snackbar.make(binding.getRoot(),"Saving is on maintenance", Snackbar.LENGTH_SHORT)
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
        ArrayList<Item> list = itemList.getValue();
        switch (checkedId){
            case R.id.rBtn_male:
                record.sex = Sex.Male;
                list.remove(Item.HIPS);
                list.get(Item.ABDOMEN_WAIST).title = getString(R.string.abdomen);
                break;
            case R.id.rBtn_female:
                record.sex = Sex.Female;
                list.get(Item.ABDOMEN_WAIST).title = getString(R.string.waist);
                list.add(HIPS);
            break;
        } // Attach and Detach Hips item
        itemList.setValue(list);
        record.validate(list);//record.validate();
        binding.invalidateAll();
    }

    public void onAgeSelected(AdapterView<?> parent, View view, int position, long id) {
        record.ageGroup = ABCPRecord.AgeGroup.findById(position);
        record.validate(null);
        binding.invalidateAll();
    }

    @SuppressLint("DefaultLocale")
    @BindingAdapter("android:text")
    public static void setText(TextView textView, float percentage) {
        textView.setText(String.format("%.1f%%",percentage));
    }

    @BindingAdapter("android:selectedItemPosition")
    public static void setSelectedItemPosition(AppCompatSpinner spinner, int selection) {
        spinner.setSelection(selection);
    }
    /*@InverseBindingAdapter(attribute = "android:selectedItemPosition")
    public static int getSelectedItemPosition(AppCompatSpinner spinner) {
        return spinner.getSelectedItemPosition();
    }*/
}