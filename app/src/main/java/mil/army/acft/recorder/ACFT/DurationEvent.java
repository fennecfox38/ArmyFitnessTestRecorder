package mil.army.acft.recorder.ACFT;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import mil.army.acft.recorder.R;

public class DurationEvent {
    public final static int SDC=4, CARDIO=6;
    private View root;
    private EditText editText_min, editText_sec, editText_sco_event;
    private Duration duration;
    private int sco,eventType;

    DurationEvent(final LayoutInflater inflater, ViewGroup container, int eventtype, Resources resources){
        eventType=eventtype;
        duration = new Duration();

        root = inflater.inflate(R.layout.layout_event_duration, container, true);
        TextView txt_title_event = root.findViewById(R.id.txt_title_event);
        editText_min = root.findViewById(R.id.editText_min);
        editText_sec = root.findViewById(R.id.editText_sec);
        editText_sco_event = root.findViewById(R.id.editText_sco_event);

        View.OnClickListener onTimeClickListen = new View.OnClickListener() {
            @Override public void onClick(View v) {
                final View durationPickerView = inflater.inflate(R.layout.durationpick, null);
                final NumberPicker picker_min = durationPickerView.findViewById(R.id.picker_min);
                final NumberPicker picker_sec = durationPickerView.findViewById(R.id.picker_sec);
                picker_min.setMinValue(0); picker_min.setMaxValue(59); picker_min.setValue(duration.getMin());
                picker_sec.setMinValue(0); picker_sec.setMaxValue(59); picker_sec.setValue(duration.getSec());

                AlertDialog.Builder builder=new AlertDialog.Builder(root.getContext());
                builder.setView(durationPickerView);
                builder.setTitle("Set Duration Time");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override public void onClick(DialogInterface dialog, int id){
                        setDuration(picker_min.getValue(),picker_sec.getValue());
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

        switch(eventType){
            case SDC:
                txt_title_event.setText(resources.getString(R.string.SDC)); break;
            case CARDIO:
                txt_title_event.setText(resources.getString(R.string.Cardio)); break;
        }
    }
    public void setDuration(int min, int sec){
        duration.setTime(min,sec);
        editText_min.setText(String.valueOf(min));
        editText_sec.setText(String.valueOf(sec));
        updateSco();
    }
    public void setDuration(Duration duration){
        this.duration = duration;
        editText_min.setText(String.valueOf(duration.getMin()));
        editText_sec.setText(String.valueOf(duration.getSec()));
        updateSco();
    }
    public Duration getDuration(){ return duration; }
    private void updateSco(){
        sco=60;
        editText_sco_event.setText(String.valueOf(sco));
    }
}
