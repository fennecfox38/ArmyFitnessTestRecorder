package army.prts.recorder.acfts;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import army.prts.recorder.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL;

public class EventRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int MDL=0,SPT=1,HPU=2,SDC=3, LTK=4, CARDIO=5;
    private Context context;
    private ArrayList<Event> eventCard;

    EventRecyclerAdapter(Context context){
        this.context = context;
        Resources resources = context.getResources();
        eventCard = new ArrayList<>();
        eventCard.add(new Event(resources.getString(R.string.MDL),resources.getString(R.string.lbs),700));
        eventCard.add(new Event(resources.getString(R.string.SPT),resources.getString(R.string.m),150));
        eventCard.add(new Event(resources.getString(R.string.HPU),resources.getString(R.string.reps),100));
        eventCard.add(new Event(resources.getString(R.string.SDC),"min/sec",5));
        eventCard.add(new Event(resources.getString(R.string.LTK),resources.getString(R.string.reps),40));
        eventCard.add(new Event(resources.getString(R.string.Cardio),"min/sec",26));
    }

    public class CountViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title, txt_unit;
        EditText editText_raw, editText_sco;
        SeekBar seekBar;
        ImageButton btn_minus, btn_plus;
        int raw, sco;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title_event);
            txt_unit = itemView.findViewById(R.id.txt_unit_event);
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
                    updateRaw(((string.length()!=0) ? Integer.parseInt(string) : 0),false,true);
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
                @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) updateRaw(progress,true,false);
                }
            });
            View.OnClickListener onAdjustBtnClickListen = new View.OnClickListener(){
                @Override public void onClick(View v) {
                    int progress = seekBar.getProgress();
                    switch (v.getId()){
                        case R.id.btn_minus_event: --progress; break;
                        case R.id.btn_plus_event: ++progress; break;
                    }
                    updateRaw(progress,true,true);
                }
            };
            btn_minus.setOnClickListener(onAdjustBtnClickListen);
            btn_plus.setOnClickListener(onAdjustBtnClickListen);
        }
        private void updateRaw(int rawSco, boolean updateEditText, boolean updateSeekBar){
            if(rawSco<0||rawSco>seekBar.getMax()) return;
            raw = rawSco;
            if(updateEditText) editText_raw.setText(String.valueOf(raw));
            if(updateSeekBar) seekBar.setProgress(raw);
            updateSco();
        }
        private void updateSco(){
            sco=60;
            editText_sco.setText(String.valueOf(sco));
        }
    }
    public class CountFloatViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title, txt_unit;
        EditText editText_raw, editText_sco;
        SeekBar seekBar;
        ImageButton btn_minus, btn_plus;
        Float raw; int sco;
        public CountFloatViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title_event);
            txt_unit = itemView.findViewById(R.id.txt_unit_event);
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
        private void updateRaw(float rawSco, boolean updateEditText, boolean updateSeekBar){
            if(rawSco<0||rawSco>seekBar.getMax()/10.0f) return;
            raw = rawSco;
            if(updateEditText) editText_raw.setText(String.valueOf(raw));
            if(updateSeekBar) seekBar.setProgress((int)(raw*10));
            updateSco();
        }
        private void updateSco(){
            sco=60;
            editText_sco.setText(String.valueOf(sco));
        }
    }
    public class DurationViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title;
        EditText editText_min, editText_sec, editText_sco;
        Duration duration;
        int sco, minMax = 30;
        public DurationViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title_event);
            editText_min = itemView.findViewById(R.id.editText_min);
            editText_sec = itemView.findViewById(R.id.editText_sec);
            editText_sco = itemView.findViewById(R.id.editText_sco_event);
            duration = new Duration(0,0); updateSco();
            View.OnClickListener onTimeClickListen = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View durationPickerView = inflater.inflate(R.layout.durationpick, null);
                    final NumberPicker picker_min = durationPickerView.findViewById(R.id.picker_min);
                    final NumberPicker picker_sec = durationPickerView.findViewById(R.id.picker_sec);
                    picker_min.setMinValue(0); picker_min.setMaxValue(minMax); picker_min.setValue(duration.getMin());
                    picker_sec.setMinValue(0); picker_sec.setMaxValue(59); picker_sec.setValue(duration.getSec());

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setView(durationPickerView);
                    builder.setTitle("Set Duration Time");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override public void onClick(DialogInterface dialog, int id){
                            duration.setTime(picker_min.getValue(),picker_sec.getValue());
                            editText_min.setText(String.valueOf(duration.getMin()));
                            editText_sec.setText(String.valueOf(duration.getSec()));
                            updateSco();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        @Override public void onClick(DialogInterface dialog, int id){ dialog.dismiss(); }
                    });
                    builder.create().show();
                }
            };
            editText_min.setOnClickListener(onTimeClickListen);
            editText_sec.setOnClickListener(onTimeClickListen);
        }
        private void updateSco(){
            sco=60;
            editText_sco.setText(String.valueOf(sco));
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case SPT:
                return (new EventRecyclerAdapter.CountFloatViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
            case SDC: case CARDIO:
                return (new EventRecyclerAdapter.DurationViewHolder(inflater.inflate(R.layout.recyclerview_duration_event,parent,false)));
            default: return (new EventRecyclerAdapter.CountViewHolder(inflater.inflate(R.layout.recyclerview_count_event,parent,false)));
        }

    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Event event = eventCard.get(position);
        if(viewHolder instanceof CountViewHolder){
            CountViewHolder holder = (CountViewHolder) viewHolder;
            holder.txt_title.setText(event.title);
            holder.txt_unit.setText(event.unit);
            holder.seekBar.setMax(event.max);
            holder.editText_raw.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL);
            holder.editText_raw.setText(String.valueOf(0));
        }
        else if(viewHolder instanceof CountFloatViewHolder){
            CountFloatViewHolder holder = (CountFloatViewHolder) viewHolder;
            holder.txt_title.setText(event.title);
            holder.txt_unit.setText(event.unit);
            holder.seekBar.setMax(event.max);
            holder.editText_raw.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
            holder.editText_raw.setText(String.valueOf(0.0f));
        }
        else{ // viewHolder instanceof DurationViewHolder
            DurationViewHolder holder = (DurationViewHolder) viewHolder;
            holder.txt_title.setText(event.title);
            holder.minMax = event.max;
            holder.editText_min.setText(String.valueOf(0));
            holder.editText_sec.setText(String.valueOf(0));
        }
    }
    @Override public int getItemViewType(int position) { return position; }
    @Override public int getItemCount() { return eventCard.size(); }



    public class Event {
        private String title, unit;
        private int max;

        Event(String title, String unit, int max){
            this.title = title;
            this.unit = unit;
            this.max = max;
        }
    }

}

