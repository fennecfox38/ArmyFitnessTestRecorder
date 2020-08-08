package mil.army.acft.recorder.ACFT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mil.army.acft.recorder.R;

public class ACFTFragment extends Fragment {
    private ACFTViewModel ACFTViewModel;
    private LayoutInflater inflater;
    private View root;
    private ACFTRecord acftRecord;
    private CountEvent event_MDL, event_SPT,event_HPU,event_LTK;
    private DurationEvent event_SDC,event_Cardio;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = layoutInflater;
        root = inflater.inflate(R.layout.fragment_acft, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getContext(),"Saving Record is on maintenance",Toast.LENGTH_SHORT).show();
            }
        });

        event_MDL = new CountEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_MDL)), CountEvent.MDL, getResources());
        event_SPT = new CountEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_SPT)), CountEvent.SPT, getResources());
        event_HPU = new CountEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_HPU)), CountEvent.HPU, getResources());
        event_SDC = new DurationEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_SDC)),DurationEvent.SDC, getResources());
        event_LTK = new CountEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_LTK)), CountEvent.LTK, getResources());
        event_Cardio = new DurationEvent(inflater,(ViewGroup)(root.findViewById(R.id.layout_event_RUN)),DurationEvent.CARDIO, getResources());

        ACFTViewModel = ViewModelProviders.of(this).get(ACFTViewModel.class);
        ACFTViewModel.getACFTRecord().observe(getViewLifecycleOwner(), new Observer<ACFTRecord>() {
            @Override public void onChanged(@Nullable ACFTRecord record) {
                acftRecord = record;

                if(record!=null){
                    event_MDL.setRaw(record.getRaw_MDL());
                    event_SPT.setRaw(record.getRaw_SPT());
                    event_HPU.setRaw(record.getRaw_HPU());
                    event_SDC.setDuration(record.getRaw_SDC());
                    event_LTK.setRaw(record.getRaw_LTK());
                    event_Cardio.setDuration(record.getRaw_Cardio());
                    //acftRecord.getCardioAlter();
                }
            }
        });

        return root;
    }

    public void getRecordFromEvent(int eventType){
        if(acftRecord==null) return;
        switch(eventType){
            case CountEvent.MDL: acftRecord.setRaw_MDL(event_MDL.getRaw()); break;
            case CountEvent.SPT: acftRecord.setRaw_SPT(event_SPT.getRawTenth()); break;
            case CountEvent.HPU: acftRecord.setRaw_HPU(event_HPU.getRaw()); break;
            case DurationEvent.SDC: acftRecord.setRaw_SDC(event_SDC.getDuration()); break;
            case CountEvent.LTK: acftRecord.setRaw_LTK(event_LTK.getRaw()); break;
            case DurationEvent.CARDIO:
                acftRecord.setRaw_Cardio(event_Cardio.getDuration());
                acftRecord.setCardioAlter(0); break;
        }
    }

}