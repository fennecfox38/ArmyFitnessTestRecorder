package army.prt.recorder.acft;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.databinding.DurationpickBinding;
import army.prt.recorder.databinding.RecyclerviewCountEventBinding;
import army.prt.recorder.databinding.RecyclerviewDurationEventBinding;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL;

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public ArrayList<Event> eventCard;

    EventRecyclerAdapter(Context context){
        this.context = context;
        Resources resources = context.getResources();
        eventCard = new ArrayList<>();
        eventCard.add(new Event(Event.MDL,resources.getString(R.string.MDL),resources.getString(R.string.lbs),700));
        eventCard.add(new Event(Event.SPT,resources.getString(R.string.SPT),resources.getString(R.string.m),150));
        eventCard.add(new Event(Event.HPU,resources.getString(R.string.HPU),resources.getString(R.string.reps),100));
        eventCard.add(new Event(Event.SDC,resources.getString(R.string.SDC),"min/sec",5));
        eventCard.add(new Event(Event.LTK,resources.getString(R.string.LTK),resources.getString(R.string.reps),40));
        eventCard.add(new Event(Event.CARDIO,resources.getString(R.string.Cardio),"min/sec",26));
    }

    public class CountViewHolder extends RecyclerView.ViewHolder{
        private RecyclerviewCountEventBinding binding;
        public Event event = null;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            if(string.length()==0) return;
            updateRawSco(Integer.parseInt(string));
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
        public RecyclerviewCountEventBinding binding;
        public Event event = null;
        EditText editText_raw, editText_sco;
        SeekBar seekBar;
        ImageButton btn_minus, btn_plus;
        Float raw; int sco;
        public CountFloatViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            editText_raw = itemView.findViewById(R.id.editText_raw_event);
            editText_sco = itemView.findViewById(R.id.editText_sco_event);
            seekBar = itemView.findViewById(R.id.seekbar_event);
            btn_minus = itemView.findViewById(R.id.btn_minus_event);
            btn_plus = itemView.findViewById(R.id.btn_plus_event);

            editText_raw.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override public void afterTextChanged(Editable s) {
                    String string = s.toString();
                    updateRaw((string.length()!=0) ? Float.parseFloat(string) : 0.0f, false,true);
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
                @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) updateRaw(progress/10.0f,true,false);
                }
            });
            View.OnClickListener onAdjustBtnClickListen = new View.OnClickListener(){
                @Override public void onClick(View v) {
                    int progress = seekBar.getProgress();
                    switch (v.getId()){
                        case R.id.btn_minus_event: --progress; break;
                        case R.id.btn_plus_event: ++progress; break;
                    }
                    updateRaw(progress/10.0f,true,true);
                }
            };
            btn_minus.setOnClickListener(onAdjustBtnClickListen);
            btn_plus.setOnClickListener(onAdjustBtnClickListen);
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) updateRaw(progress/10.0f,true,false);
        }
        private void updateRaw(float rawSco, boolean updateEditText, boolean updateSeekBar){
            if(rawSco<0||rawSco>seekBar.getMax()/10.0f) return;
            raw = rawSco;
            if(updateEditText) editText_raw.setText(String.valueOf(raw));
            if(updateSeekBar) seekBar.setProgress((int)(raw*10));
            binding.invalidateAll();
            updateSco();
        }
        private void updateSco(){
            sco=60;
            //editText_sco.setText(String.valueOf(sco));
            binding.invalidateAll();
        }
    }

    public class DurationViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewDurationEventBinding binding;
        Event event = null; // It will be assigned in 'onBindViewHolder'\
        public DurationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void onTimeClick(View view){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DurationpickBinding durationBinding = DataBindingUtil.bind(inflater.inflate(R.layout.durationpick, null));
            durationBinding.setDuration(event.duration);
            durationBinding.setMax(event.max);
            durationBinding.pickerMin.setMaxValue(event.max);
            durationBinding.pickerSec.setMaxValue(59);

            Resources resources = context.getResources();
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
                return (new EventRecyclerAdapter.CountFloatViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
            case Event.SDC: case Event.CARDIO:
                return (new EventRecyclerAdapter.DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event,parent,false)));
            default: // case for MDL, HPU, LTK
                return (new EventRecyclerAdapter.CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
        }
    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Event event = eventCard.get(position);
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.event = event;
            holder.binding.setViewholder(holder);
            //holder.binding.editTextRawEvent.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL);
        }
        else if(viewHolder instanceof CountFloatViewHolder){
            CountFloatViewHolder holder = (CountFloatViewHolder) viewHolder;
            holder.event = event;
            //holder.binding.setViewholder(holder);
            holder.seekBar.setMax(event.max);
            holder.editText_raw.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
            holder.editText_raw.setText(String.valueOf(0.0f));
        }
        else if(viewHolder instanceof DurationViewHolder){
            final DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.event = event;
            holder.binding.setEvent(event);
            holder.binding.setViewholder(holder);
        }
    }
    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventCard.size(); }

    public class Event {
        public final static int MDL=0,SPT=1,HPU=2,SDC=3, LTK=4, CARDIO=5;
        public String title, unit;
        public int eventType, max, raw = 0, sco = 0;
        public float rawFloat = 0.0f;
        public Duration duration;
        Event(int eventType, String title, String unit, int max){
            this.eventType = eventType;
            this.title = title;
            this.unit = unit;
            this.max = max;
            duration = new Duration(0,0);
        }
    }

    @BindingAdapter("android:text")
    public static void setText(EditText editText, int raw) {
        if(! editText.getText().toString().equals(String.valueOf(raw))) {
            editText.setText(String.valueOf(raw));
            editText.setSelection(editText.length());
        }
    }
    @InverseBindingAdapter(attribute = "android:text")
    public static int getText(EditText editText) {
        if (editText.getText().toString().length()==0) return 0;
        return Integer.parseInt(editText.getText().toString());
    }

}

