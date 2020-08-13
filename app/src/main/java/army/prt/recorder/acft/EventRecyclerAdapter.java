package army.prt.recorder.acft;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.databinding.DurationpickBinding;
import army.prt.recorder.databinding.RecyclerviewCountEventBinding;
import army.prt.recorder.databinding.RecyclerviewCountFloatEventBinding;
import army.prt.recorder.databinding.RecyclerviewDurationEventBinding;

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Resources resources;
    public ArrayList<Event> events;

    EventRecyclerAdapter(Context context){
        this.context = context;
        resources = context.getResources();
        events = new ArrayList<>();
        events.add(new CountEvent(Event.MDL,resources.getString(R.string.MDL),700,resources.getString(R.string.lbs)));
        events.add(new CountFloatEvent(Event.SPT,resources.getString(R.string.SPT),150,resources.getString(R.string.m)));
        events.add(new CountEvent(Event.HPU,resources.getString(R.string.HPU),100,resources.getString(R.string.reps)));
        events.add(new DurationEvent(Event.SDC,resources.getString(R.string.SDC),5));
        events.add(new CountEvent(Event.LTK,resources.getString(R.string.LTK),40,resources.getString(R.string.reps)));
        events.add(new DurationEvent(Event.CARDIO,resources.getString(R.string.Cardio),26));
    }
    /*public void setEventList(ArrayList<Event> eventList){
        events = eventList;
    }*/

    public class CountViewHolder extends RecyclerView.ViewHolder{
        private RecyclerviewCountEventBinding binding;
        public CountEvent event = null;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            //binding.setLifecycleOwner(); Fragment.getLifecycleOwner();
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try {
                updateRawSco(Integer.parseInt(string));
            }catch (Exception e){ }
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
            event.sco=60; // Score calculation logic needs to be inserted here.
            binding.invalidateAll();
        }
    }
    public class CountFloatViewHolder extends RecyclerView.ViewHolder{
        public RecyclerviewCountFloatEventBinding binding;
        public CountFloatEvent event = null;
        public CountFloatViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            try{
                updateRaw(Float.parseFloat(string));
            }catch(Exception e){ }
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRaw(progress/10.0f);
        }
        public void onAdjustBtnClick(View v) {
            switch (v.getId()){
                case R.id.btn_minus_event: updateRaw(event.raw-0.1f); break;
                case R.id.btn_plus_event: updateRaw(event.raw+0.1f); break;
            }
        }
        private void updateRaw(float rawSco){
            if(rawSco<0) rawSco = 0;
            else if(rawSco>event.max/10.0f) rawSco = event.max/10.0f;
            event.raw = Math.round(rawSco*10)/10.0f;
            binding.invalidateAll();
            updateSco();
        }
        private void updateSco(){
            event.sco=60;
            binding.invalidateAll();
        }
    }

    public class DurationViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewDurationEventBinding binding;
        public DurationEvent event = null; // It will be assigned in 'onBindViewHolder'\
        public DurationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void onTimeClick(View view){
            DurationpickBinding durationBinding =
                    DataBindingUtil.inflate((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                            R.layout.durationpick,null,false);
            durationBinding.setDuration(event.duration);
            durationBinding.pickerMin.setMaxValue(event.max);
            durationBinding.pickerSec.setMaxValue(59);

            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setView(durationBinding.getRoot());
            builder.setTitle(resources.getString(R.string.title_duration_picker));
            builder.setPositiveButton(resources.getString(R.string.OK), new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog, int id){
                    //event.duration is already changed.
                    event.sco=60; // Score calculation logic needs to be inserted here.
                    binding.invalidateAll();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(resources.getString(R.string.Cancel), new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog, int id){ dialog.dismiss(); }
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
            holder.event = (CountEvent) events.get(position);
            holder.binding.setViewholder(holder);
        }
        else if(viewHolder instanceof CountFloatViewHolder){
            CountFloatViewHolder holder = (CountFloatViewHolder) viewHolder;
            holder.event = (CountFloatEvent) events.get(position);
            holder.binding.setViewholder(holder);
        }
        else if(viewHolder instanceof DurationViewHolder){
            DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = (DurationEvent) events.get(position);
            holder.binding.setViewholder(holder);
        }
    }
    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return events.size(); }

    public class Event {
        public final static int MDL=0,SPT=1,HPU=2,SDC=3, LTK=4, CARDIO=5;
        public String title;
        public int eventType, max, sco = 0;
        Event(int eventType, String title, int max){
            this.eventType = eventType;
            this.title = title;
            this.max = max;
        }
    }
    public class CountEvent extends Event{
        public String unit;
        public int raw = 0;
        CountEvent(int eventType, String title, int max, String unit) {
            super(eventType, title, max);
            this.unit = unit;
        }
    }

    public class CountFloatEvent extends Event{
        public String unit;
        public float raw = 0;
        CountFloatEvent(int eventType, String title, int max, String unit) {
            super(eventType, title, max);
            this.unit = unit;
        }
    }

    public class DurationEvent extends Event{
        public Duration duration;
        DurationEvent(int eventType, String title, int max){
            super(eventType, title, max);
            duration = new Duration(0,0);
        }
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, int raw) {
        try{
            if(raw == Integer.parseInt(editText.getText().toString())) return;
        }catch (Exception e){  }
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
        try{
            if(raw == Float.parseFloat(editText.getText().toString()) ) return;
        }
        catch (Exception e){  }
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
        if((int)(rawFloat*10) != seekBar.getProgress()) {
        seekBar.setProgress((int) (rawFloat*10));
        }
    }
    /*@InverseBindingAdapter(attribute = "android:progress")
    public static float getProgress(SeekBar seekBar) {
        return (seekBar.getProgress()/10.0f);
    }*/

}

