package army.prt.recorder.log;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.acft.ACFTRecord;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.databinding.RecyclerviewAcftLogBinding;

public class ACFTLogRecyclerAdapter extends RecyclerView.Adapter<ACFTLogRecyclerAdapter.ACFTLogViewHolder> {
    public static final int LOG_ACFT=0, LOG_APFT=1, LOG_ABCP=2;
    private Context context;
    private Resources resources;
    private ArrayList<ACFTRecord> list;

    public ACFTLogRecyclerAdapter(Context context){
        this.context = context; resources = context.getResources();
        ACFTDBHelper dbHelper = new ACFTDBHelper(context);
        list = dbHelper.getRecordList();
        dbHelper.close();
    }

    public class ACFTLogViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewAcftLogBinding binding;
        public ACFTRecord record;
        public ACFTLogViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public String getQualifiedLevel(){ return (resources.getStringArray(R.array.Level)[record.qualifiedLevel]); }
        public String getAlter(){ return (resources.getStringArray(R.array.CardioEvent)[record.cardioAlter]); }
        public String getLevel(int sco){
            int level;
            if(sco<60) level = Event.FAIL;
            else if(sco<65) level = Event.MODERATE;
            else if(sco<70) level = Event.SIGNIFICANT;
            else level = Event.HEAVY;
            return (resources.getStringArray(R.array.Level)[level]);
        }
        public void onShareClick(View view){
            Toast.makeText(context,"onShareClick",Toast.LENGTH_SHORT).show();
        }
        public void onDeleteClick(View view){
            int index = getAdapterPosition();
            ACFTDBHelper dbHelper = new ACFTDBHelper(context);
            dbHelper.deleteRecord(list.get(index));
            dbHelper.close();
            list.remove(index);
            notifyItemRemoved(index);
        }
    }

    @NotNull @Override public ACFTLogRecyclerAdapter.ACFTLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.recyclerview_acft_log,parent,false);
        return (new ACFTLogViewHolder(view));
    }

    @Override public void onBindViewHolder(@NonNull ACFTLogRecyclerAdapter.ACFTLogViewHolder holder, int position) {
        holder.record = list.get(position);
        holder.binding.setViewholder(holder);
    }

    @Override public int getItemCount() { return list.size(); }

}
