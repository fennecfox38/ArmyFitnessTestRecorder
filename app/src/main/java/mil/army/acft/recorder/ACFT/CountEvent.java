package mil.army.acft.recorder.ACFT;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import mil.army.acft.recorder.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL;

public class CountEvent {
    public final static int MDL=1,SPT=2,HPU=3,LTK=5;
    private SeekBar seekBar_event;
    private boolean isTenthScale=false; // True for SPT
    private EditText editText_raw_event, editText_sco_event;
    private float rawTenth;
    private int raw,sco,eventType;

    CountEvent(LayoutInflater inflater, ViewGroup container, int eventtype, Resources resources){
        eventType = eventtype;

        View root = inflater.inflate(R.layout.layout_event_count, container, true);
        editText_raw_event = root.findViewById(R.id.editText_raw_event);
        editText_sco_event = root.findViewById(R.id.editText_sco_event);
        seekBar_event = root.findViewById(R.id.seekbar_event);
        ImageButton btn_minus_event = root.findViewById(R.id.btn_minus_event);
        ImageButton btn_plus_event = root.findViewById(R.id.btn_plus_event);
        TextView txt_title_event = root.findViewById(R.id.txt_title_event);
        TextView txt_unit_event = root.findViewById(R.id.txt_unit_event);

        isTenthScale=false; // MDL, HPU LTK Events require just normal integer.
        editText_raw_event.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL);

        switch(eventType){
            case SPT:
                txt_title_event.setText(resources.getString(R.string.SPT));
                txt_unit_event.setText(resources.getString(R.string.m));
                isTenthScale=true; // SPT Event (Standing Power Throw) requires decimal.
                editText_raw_event.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
                seekBar_event.setMax(150); break;
            case MDL:
                txt_title_event.setText(resources.getString(R.string.MDL));
                txt_unit_event.setText(resources.getString(R.string.lbs));
                seekBar_event.setMax(700); break;
            case HPU:
                txt_title_event.setText(resources.getString(R.string.HPU));
                txt_unit_event.setText(resources.getString(R.string.reps));
                seekBar_event.setMax(100); break;
            case LTK:
                txt_title_event.setText(resources.getString(R.string.LTK));
                txt_unit_event.setText(resources.getString(R.string.reps));
                seekBar_event.setMax(40); break;
        }

        editText_raw_event.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(string.length()==0) updateRaw(0,false,true);
                else if(isTenthScale) updateRaw(Float.parseFloat(s.toString()),false,true);
                else updateRaw(Integer.parseInt(s.toString()),false,true);
            }
        });
        seekBar_event.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(isTenthScale) updateRaw(progress/10.0f,true,false);
                    else updateRaw(progress,true,false);
                }

            }
        });
        ImageButton.OnClickListener onAdjustBtnClickListen = new ImageButton.OnClickListener(){
            @Override public void onClick(View v) {
                int progress = seekBar_event.getProgress();
                switch (v.getId()){
                    case R.id.btn_minus_event: --progress; break;
                    case R.id.btn_plus_event: ++progress; break;
                }
                if(isTenthScale) updateRaw(progress/10.0f,true,true);
                else updateRaw(progress,true,true);
            }
        };
        btn_minus_event.setOnClickListener(onAdjustBtnClickListen);
        btn_plus_event.setOnClickListener(onAdjustBtnClickListen);
    }

    private String getStrFromRaw(int rawSco){
        if(isTenthScale) return String.valueOf(rawSco/10.0); // restore tenth scale.
        else return String.valueOf(rawSco);
    }
    private void updateRaw(int rawSco, boolean updateEditText, boolean updateSeekBar){
        if(rawSco<0||rawSco>seekBar_event.getMax()) return;
        raw=rawSco;
        updateSco();
        if(updateEditText){
            editText_raw_event.setText(getStrFromRaw(raw));
            editText_raw_event.setSelection(editText_raw_event.length());
        }
        if(updateSeekBar && seekBar_event.getProgress()!=raw)
            seekBar_event.setProgress(raw);
    }
    private void updateRaw(float rawSco, boolean updateEditText, boolean updateSeekBar){
        if(rawSco<0||rawSco>(seekBar_event.getMax()/10.0)) return;
        rawTenth=rawSco;
        raw=(int)(rawTenth*10);
        updateSco();
        if(updateEditText){
            editText_raw_event.setText(String.valueOf(rawTenth));
            editText_raw_event.setSelection(editText_raw_event.length());
        }
        if(updateSeekBar && seekBar_event.getProgress()!=raw)
            seekBar_event.setProgress(raw);
    }
    private void updateSco(){
        sco=60;
        editText_sco_event.setText(String.valueOf(sco));
    }
    public void setRaw(int rawSco){ updateRaw(rawSco,true,true); }
    public void setRaw(float rawSco){ updateRaw(rawSco,true,true); }
    public int getRaw(){ return raw; }
    public float getRawTenth() { return rawTenth; }
    public int getSco(){ return sco; }
}
