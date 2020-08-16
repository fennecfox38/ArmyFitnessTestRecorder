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
import android.widget.TextView;

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
import army.prt.recorder.databinding.RecyclerviewDurationEventBinding;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

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
            if(event.eventType != Event.SPT){
                try { updateRawSco(Integer.parseInt(string)); }
                catch (Exception e){ }
            }
            else{
                try{ updateRawSco((int)(Float.parseFloat(string)*10)); }
                catch(Exception e){ }
            }
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
        public String setQualifiedLevel(int sco) {
            int qualifiedLevel;
            if(sco>=70) qualifiedLevel = Event.HEAVY;
            else if(sco>=65) qualifiedLevel = Event.SIGNIFICANT;
            else if(sco>=60) qualifiedLevel = Event.MODERATE;
            else qualifiedLevel = Event.FAIL;
            return resources.getStringArray(R.array.Level)[qualifiedLevel];
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
        public String setQualifiedLevel(int sco) {
            int qualifiedLevel;
            if(sco>=70) qualifiedLevel = Event.HEAVY;
            else if(sco>=65) qualifiedLevel = Event.SIGNIFICANT;
            else if(sco>=60) qualifiedLevel = Event.MODERATE;
            else qualifiedLevel = Event.FAIL;
            return resources.getStringArray(R.array.Level)[qualifiedLevel];
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
            holder.event = (CountEvent) eventList.get(position);
            holder.binding.setViewholder(holder);
            if(position == Event.SPT)
                holder.binding.editTextRawEvent.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
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

    /*@BindingAdapter("android:text")
    public static void setText(EditText editText, int raw) {
        try{ if(raw == Integer.parseInt(editText.getText().toString())) return; }
        catch (Exception e){  }
        editText.setText(String.valueOf(raw));
        editText.setSelection(editText.length());
    }
    @InverseBindingAdapter(attribute = "android:text")
    public static int getText(EditText editText) {
        try{
            return Integer.parseInt(editText.getText().toString());
        }catch (NumberFormatException e) { return 0; }
    }*/

}

