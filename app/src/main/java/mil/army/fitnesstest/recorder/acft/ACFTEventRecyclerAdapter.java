package mil.army.fitnesstest.recorder.acft;

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
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.acft.event.ACFTCardioAlter;
import mil.army.fitnesstest.recorder.acft.event.ACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.CountACFTEvent;
import mil.army.fitnesstest.recorder.acft.event.DurationACFTEvent;
import mil.army.fitnesstest.databinding.DurationpickBinding;
import mil.army.fitnesstest.databinding.RecyclerviewCountEventAcftBinding;
import mil.army.fitnesstest.databinding.RecyclerviewDurationEventAcftBinding;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class ACFTEventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Resources resources;
    public MutableLiveData<ArrayList<ACFTEvent>> eventList;

    public ACFTEventRecyclerAdapter(Context context, MutableLiveData<ArrayList<ACFTEvent>> eventList){
        this.context = context;
        resources = context.getResources();
        this.eventList = eventList;
    }

    public class CountViewHolder extends RecyclerView.ViewHolder{
        private RecyclerviewCountEventAcftBinding binding;
        public CountACFTEvent event = null; // It will be assigned in 'onBindViewHolder'
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{
                if(event.eventType != ACFTEvent.SPT) updateRawSco(Integer.parseInt(string));
                else updateRawSco((int)(Float.parseFloat(string)*10));
            }catch(Exception e){ return; }
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRawSco(progress);
        }
        public void onAdjustBtnClick(View v) { updateRawSco(event.raw+ (v.getId()==R.id.btn_plus ? 1 : -1)); }
        private void updateRawSco(int rawSco){
            if(rawSco == event.raw) return;
            else if(rawSco<0) rawSco = 0;
            else if(rawSco>event.max) rawSco = event.max;
            event.raw = rawSco;
            event.giveScore();
            updateEventList(event);
            binding.invalidateAll();
        }
    }

    public class DurationViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewDurationEventAcftBinding binding;
        public DurationACFTEvent event = null; // It will be assigned in 'onBindViewHolder'
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
        public void onAlterSelected(AdapterView<?> parent, View view, int position, long id) {
            event.cardioAlter= ACFTCardioAlter.valueOf(position);
            event.giveScore();
            updateEventList(event);
            binding.invalidateAll();
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case ACFTEvent.SDC: case ACFTEvent.CARDIO:
                return (new ACFTEventRecyclerAdapter.DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event_acft,parent,false)));
            default: // case for CountEvent such as MDL, SPT, HPU, LTK
                return (new ACFTEventRecyclerAdapter.CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event_acft,parent,false)));
        }
    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.event = (CountACFTEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
            if(position == ACFTEvent.SPT)
                holder.binding.editTextRaw.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
        }
        else if(viewHolder instanceof DurationViewHolder){
            DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = (DurationACFTEvent) eventList.getValue().get(position);
            holder.binding.setViewholder(holder);
        }

    }

    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventList.getValue().size(); }

    private void updateEventList(ACFTEvent event){
        ArrayList<ACFTEvent> list = eventList.getValue();
        list.set(event.eventType,event);
        eventList.setValue(list);
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, CountACFTEvent event) {
        if(event.eventType!= ACFTEvent.SPT){
            try{ if(event.raw == Integer.parseInt(editText.getText().toString())) return; }
            catch (NumberFormatException e){e.printStackTrace();}
            editText.setText(String.valueOf(event.raw));
        }
        else{ // for SPT event. In order to convert float to int or reverse.
            try{ if(event.raw == (int)((Float.parseFloat(editText.getText().toString()))*10)) return; }
            catch (NumberFormatException e){e.printStackTrace();}
            editText.setText(String.valueOf((event.raw)/10.0f));
        }
        editText.setSelection(editText.length());
    }
}

