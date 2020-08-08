package mil.army.acft.recorder.ACFT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mil.army.acft.recorder.R;

public class ACFTFragment extends Fragment {
    private ACFTViewModel ACFTViewModel;
    private ACFTRecord acftRecord;
    private CountEvent event_MDL, event_SPT,event_HPU,event_LTK;
    private DurationEvent event_SDC,event_Cardio;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_acft, container, false);
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
                setEventFromRecord();
            }
        });

        return root;
    }

    public void getRecordFromEvent(){
        acftRecord.setRaw_MDL(event_MDL.getRaw());
        acftRecord.setRaw_SPT(event_SPT.getRawTenth());
        acftRecord.setRaw_HPU(event_HPU.getRaw());
        acftRecord.setRaw_SDC(event_SDC.getDuration());
        acftRecord.setRaw_LTK(event_LTK.getRaw());
        acftRecord.setRaw_Cardio(event_Cardio.getDuration());
        acftRecord.setCardioAlter(0);
    }
    private void setEventFromRecord(){
        event_MDL.setRaw(acftRecord.getRaw_MDL());
        event_SPT.setRaw(acftRecord.getRaw_SPT());
        event_HPU.setRaw(acftRecord.getRaw_HPU());
        event_SDC.setDuration(acftRecord.getRaw_SDC());
        event_LTK.setRaw(acftRecord.getRaw_LTK());
        event_Cardio.setDuration(acftRecord.getRaw_Cardio());
        //acftRecord.getCardioAlter();
    }

}