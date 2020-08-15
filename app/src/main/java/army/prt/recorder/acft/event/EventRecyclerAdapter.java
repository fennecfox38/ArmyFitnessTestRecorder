package army.prt.recorder.acft.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.acft.ACFTFragment;
import army.prt.recorder.databinding.DurationpickBinding;
import army.prt.recorder.databinding.RecyclerviewCountEventBinding;
import army.prt.recorder.databinding.RecyclerviewCountFloatEventBinding;
import army.prt.recorder.databinding.RecyclerviewDurationEventBinding;

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ACFTFragment fragment;
    private Context context;
    private Resources resources;
    public ArrayList<Event> eventList;

    public EventRecyclerAdapter(ACFTFragment fragment){
        this.fragment = fragment;
        context = fragment.getContext();
        resources = fragment.getResources();
    }

    public void setEventList(ArrayList<Event> eventList){
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
            try { updateRawSco(Integer.parseInt(string)); }
            catch (Exception e){ }
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRawSco(progress);
        }
        public void onAdjustBtnClick(View v) {
            switch (v.getId()){
                case R.id.btn_minus_event: updateRawSco(event.raw-1); break;
                case R.id.btn_plus_event: updateRawSco(event.raw+1); break;
            }
        }
        private void updateRawSco(int rawSco){
            if(rawSco<0) rawSco = 0;
            else if(rawSco>event.max) rawSco = event.max;
            event.raw = rawSco;
            event.giveScore();
            binding.invalidateAll();
            fragment.ACFTViewModel.updateEvent(event,getAdapterPosition());
        }
    }
    public class CountFloatViewHolder extends RecyclerView.ViewHolder{
        public RecyclerviewCountFloatEventBinding binding;
        public CountFloatEvent event = null; // It will be assigned in 'onBindViewHolder'
        public CountFloatViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{ updateRawSco(Float.parseFloat(string)); }
            catch(Exception e){ }
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRawSco(progress/10.0f);
        }
        public void onAdjustBtnClick(View v) {
            switch (v.getId()){
                case R.id.btn_minus_event: updateRawSco(event.raw-0.1f); break;
                case R.id.btn_plus_event: updateRawSco(event.raw+0.1f); break;
            }
        }
        private void updateRawSco(float rawSco){
            if(rawSco<0) rawSco = 0;
            else if(rawSco>event.max/10.0f) rawSco = event.max/10.0f;
            event.raw = Math.round(rawSco*10)/10.0f;
            event.giveScore();
            binding.invalidateAll();
            fragment.ACFTViewModel.updateEvent(event,getAdapterPosition());
        }
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
                    binding.invalidateAll();
                    fragment.ACFTViewModel.updateEvent(event,getAdapterPosition());
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case Event.SPT:
                return (new EventRecyclerAdapter.CountFloatViewHolder(inflater.inflate(R.layout.recyclerview_count_float_event,parent,false)));
            case Event.SDC: case Event.CARDIO:
                return (new EventRecyclerAdapter.DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event,parent,false)));
            default: // case for MDL, HPU, LTK
                return (new EventRecyclerAdapter.CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
        }
    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.event = (CountEvent) eventList.get(position);
            holder.binding.setViewholder(holder);
        }
        else if(viewHolder instanceof CountFloatViewHolder){
            CountFloatViewHolder holder = (CountFloatViewHolder) viewHolder;
            holder.event = (CountFloatEvent) eventList.get(position);
            holder.binding.setViewholder(holder);
        }
        else if(viewHolder instanceof DurationViewHolder){
            final DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = (DurationEvent) eventList.get(position);
            holder.binding.setViewholder(holder);
            if(position==Event.CARDIO){
                if(holder.binding.spinnerCardioAlter.getVisibility()==View.INVISIBLE){
                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,resources.getStringArray(R.array.Cardio_Event));
                    stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.binding.spinnerCardioAlter.setVisibility(View.VISIBLE);
                    holder.binding.spinnerCardioAlter.setAdapter(stringArrayAdapter);
                    holder.binding.spinnerCardioAlter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            holder.event.cardioAlter = position;
                        }
                        @Override public void onNothingSelected(AdapterView<?> parent) { }
                    });
                    holder.binding.spinnerCardioAlter.setSelection(DurationEvent.RUN);
                }
            }
        }
    }
    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventList.size(); }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, int raw) {
        try{ if(raw == Integer.parseInt(editText.getText().toString())) return; }
        catch (Exception e){  }
        editText.setText(String.valueOf(raw));
        editText.setSelection(editText.length());
    }
    /*@InverseBindingAdapter(attribute = "android:text")
    public static int getText(EditText editText) {
        try{
            return Integer.parseInt(editText.getText().toString());
        }catch (NumberFormatException e) { return 0; }
    }*/

    @BindingAdapter("android:text")
    public static void setText(EditText editText, float raw) {
        try{ if(raw == Float.parseFloat(editText.getText().toString()) ) return; }
        catch (Exception e){ }
        editText.setText(String.valueOf(raw));
        editText.setSelection(editText.length());
    }
    /*@InverseBindingAdapter(attribute = "android:text")
    public static float getText(EditText editText) {
        if (editText.getText().toString().length()==0) return 0.0f;
        return Float.parseFloat(editText.getText().toString());
    }*/


    @BindingAdapter("android:progress")
    public static void setProgress(SeekBar seekBar, float rawFloat) {
        if((int)(rawFloat*10) != seekBar.getProgress())
            seekBar.setProgress((int) (rawFloat*10));
    }
    /*@InverseBindingAdapter(attribute = "android:progress")
    public static float getProgress(SeekBar seekBar) {
        return (seekBar.getProgress()/10.0f);
    }*/

}

