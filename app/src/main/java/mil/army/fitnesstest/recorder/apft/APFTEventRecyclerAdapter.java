package mil.army.fitnesstest.recorder.apft;

import android.content.Context;
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
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.databinding.DurationpickBinding;
import mil.army.fitnesstest.databinding.RecyclerviewCountEventApftBinding;
import mil.army.fitnesstest.databinding.RecyclerviewDurationEventApftBinding;
import mil.army.fitnesstest.recorder.apft.event.APFTCardioAlter;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;
import mil.army.fitnesstest.recorder.apft.event.CountAPFTEvent;
import mil.army.fitnesstest.recorder.apft.event.DurationAPFTEvent;

public class APFTEventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Resources resources;
    public MutableLiveData<ArrayList<APFTEvent>> eventList;

    public APFTEventRecyclerAdapter(Context context, MutableLiveData<ArrayList<APFTEvent>> eventList){
        this.context = context;
        resources = context.getResources();
        this.eventList = eventList;
    }
    public class CountViewHolder extends RecyclerView.ViewHolder {
        private RecyclerviewCountEventApftBinding binding;
        public CountAPFTEvent event = null; // It will be assigned in 'onBindViewHolder'

        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{ updateRawSco(Integer.parseInt(string)); }
            catch(Exception e){ return; }
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
        RecyclerviewDurationEventApftBinding binding;
        public DurationAPFTEvent event = null; // It will be assigned in 'onBindViewHolder'
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
            builder.setPositiveButton(resources.getString(R.string.OK), (dialog, id) -> dialog.cancel());
            builder.setOnCancelListener(dialog -> {
                //event.duration is already bound with Number Pickers.
                event.giveScore();
                updateEventList(event);
                binding.invalidateAll();
                dialog.dismiss();
            });
            builder.create().show();
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
        public void onAlterSelected(AdapterView<?> parent, View view, int position, long id) {
            event.cardioAlter= APFTCardioAlter.findById(position);
            event.giveScore();
            updateEventList(event);
            binding.invalidateAll();
        }
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(viewType== APFTEvent.CARDIO) return (new DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event_apft,parent,false)));
        else return (new CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event_apft,parent,false)));
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.event = (CountAPFTEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
        }
        else if(viewHolder instanceof DurationViewHolder){
            DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = (DurationAPFTEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
        }
    }

    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventList.getValue().size(); }

    private void updateEventList(APFTEvent event){
        ArrayList<APFTEvent> list = eventList.getValue();
        list.set(event.eventType,event);
        eventList.setValue(list);
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, CountAPFTEvent event) {
        try{ if(event.raw == Integer.parseInt(editText.getText().toString())) return; }
        catch (NumberFormatException e){e.printStackTrace();}
        editText.setText(String.valueOf(event.raw));
        editText.setSelection(editText.length());
    }
    @BindingAdapter("android:selectedItemPosition")
    public static void setSelectedItemPosition(AppCompatSpinner spinner, int selection) {
        spinner.setSelection(selection);
    }

}
