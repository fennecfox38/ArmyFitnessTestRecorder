package army.fitnesstest.recorder.acft.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.fitnesstest.recorder.R;
import army.fitnesstest.recorder.acft.CardioAlter;
import army.fitnesstest.recorder.acft.Level;
import army.fitnesstest.recorder.databinding.DurationpickBinding;
import army.fitnesstest.recorder.databinding.RecyclerviewCountEventBinding;
import army.fitnesstest.recorder.databinding.RecyclerviewDurationEventBinding;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Resources resources;
    public MutableLiveData<ArrayList<Event>> eventList;

    public EventRecyclerAdapter(Context context, MutableLiveData<ArrayList<Event>> eventList){
        this.context = context;
        resources = context.getResources();
        this.eventList = eventList;
    }

    public class CountViewHolder extends RecyclerView.ViewHolder{
        private RecyclerviewCountEventBinding binding;
        public CountEvent event = null; // It will be assigned in 'onBindViewHolder'
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{
                if(event.eventType != Event.SPT) updateRawSco(Integer.parseInt(string));
                else updateRawSco((int)(Float.parseFloat(string)*10));
            }catch(Exception e){ return; }
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRawSco(progress);
        }
        public void onAdjustBtnClick(View v) {
            switch (v.getId()){
                case R.id.btn_minus: updateRawSco(event.raw-1); break;
                case R.id.btn_plus: updateRawSco(event.raw+1); break;
            }
        }
        private void updateRawSco(int rawSco){
            if(rawSco<0) rawSco = 0;
            else if(rawSco>event.max) rawSco = event.max;
            event.raw = rawSco;
            event.giveScore();
            updateEventList(event);
            binding.invalidateAll();
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
    }

    public class DurationViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewDurationEventBinding binding;
        public DurationEvent event = null; // It will be assigned in 'onBindViewHolder'
        public DurationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void onTimeClick(View view){
            DurationpickBinding pickerBinding = DataBindingUtil.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                    R.layout.durationpick,null,false);
            pickerBinding.setDuration(event.duration);
            pickerBinding.pickerMin.setMaxValue(event.max);
            pickerBinding.pickerSec.setMaxValue(59);

            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setView(pickerBinding.getRoot());
            builder.setTitle(resources.getString(R.string.title_duration_picker));
            builder.setPositiveButton(resources.getString(R.string.OK), new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog, int id){ dialog.cancel(); }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override public void onCancel(DialogInterface dialog) {
                    //event.duration is already bound with Number Pickers.
                    event.giveScore();
                    updateEventList(event);
                    binding.invalidateAll();
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
        public String setQualifiedLevel(int sco) {
            if(sco>=70) return Level.Heavy.toString();
            else if(sco>=65) return Level.Significant.toString();
            else if(sco>=60) return Level.Moderate.toString();
            else return Level.Fail.toString();
        }
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            event.cardioAlter= CardioAlter.findById(position);
            event.giveScore();
            updateEventList(event);
            binding.invalidateAll();
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case Event.SDC: case Event.CARDIO:
                return (new EventRecyclerAdapter.DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event,parent,false)));
            default: // case for CountEvent such as MDL, SPT, HPU, LTK
                return (new EventRecyclerAdapter.CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
        }
    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.event = (CountEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
            if(position == Event.SPT)
                holder.binding.editTextRaw.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
        }
        else if(viewHolder instanceof DurationViewHolder){
            final DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = (DurationEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
        }

    }

    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventList.getValue().size(); }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, CountEvent event) {
        if(event.eventType!=Event.SPT){
            try{ if(event.raw == Integer.parseInt(editText.getText().toString())) return; }
            catch (Exception e){  }
            editText.setText(String.valueOf(event.raw));
        }
        else{ // for SPT event. In order to convert float to int or reverse.
            try{ if(event.raw == (int)((Float.parseFloat(editText.getText().toString()))*10)) return; }
            catch (Exception e){  }
            editText.setText(String.valueOf((event.raw)/10.0f));
        }
        editText.setSelection(editText.length());
    }

    private void updateEventList(Event event){
        ArrayList<Event> list = eventList.getValue();
        list.set(event.eventType,event);
        eventList.setValue(list);
    }

    @BindingAdapter("android:selection")
    public static void setSelectedItemPosition(AppCompatSpinner spinner, int selection) {
        spinner.setSelection(selection);
    }
    @InverseBindingAdapter(attribute = "android:selection")
    public static int getSelectedItemPosition(AppCompatSpinner spinner) {
        return spinner.getSelectedItemPosition();
    }

}

