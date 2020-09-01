package mil.army.fitnesstest.recorder.log;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.acft.ACFTDBHelper;
import mil.army.fitnesstest.recorder.acft.ACFTRecord;
import mil.army.fitnesstest.recorder.acft.Level;
import mil.army.fitnesstest.databinding.RecyclerviewAcftLogBinding;
import mil.army.fitnesstest.recorder.acft.event.ACFTEvent;

public class ACFTLogRecyclerAdapter extends RecyclerView.Adapter<ACFTLogRecyclerAdapter.ACFTLogViewHolder> {
    private Context context;
    private Resources resources;
    private ArrayList<ACFTRecord<ACFTEvent>> list;

    public ACFTLogRecyclerAdapter(Context context){
        this.context = context; resources = context.getResources();
        ACFTDBHelper dbHelper = new ACFTDBHelper(context);
        list = dbHelper.getRecordList();
        dbHelper.close();
    }

    public class ACFTLogViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewAcftLogBinding binding;
        public ACFTRecord<ACFTEvent> record;
        public ACFTLogViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                menu.setHeaderTitle(resources.getString(R.string.record));
                menu.add(0,0,0,resources.getString(R.string.share)).setOnMenuItemClickListener(item -> {
                    context.startActivity(new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, record.toString()).setType("text/plain"));
                    return false;
                });
                menu.add(0,1,1,resources.getString(R.string.delete)).setOnMenuItemClickListener(item -> {
                    ACFTDBHelper dbHelper = new ACFTDBHelper(context);
                    dbHelper.deleteRecord(record);  dbHelper.close();
                    list.remove(getAdapterPosition());  notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                            .setAction(resources.getString(R.string.undo), view1 -> {
                                list.add(record);   notifyItemInserted(list.size()-1);
                                ACFTDBHelper dbHelper1 = new ACFTDBHelper(context);
                                dbHelper1.insertRecord(record);  dbHelper1.close();
                            }).show();
                    return false;
                });
            });
            binding = DataBindingUtil.bind(itemView);
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
        public String getPassed(boolean isPassed){ return resources.getString(isPassed ? R.string.pass: R.string.fail); }
        public String getLevel(int sco, boolean pf){
            if(pf) return getPassed(sco>=60);
            else if(sco<60) return Level.Fail.name();
            else if(sco<65) return Level.Moderate.name();
            else if(sco<70) return Level.Significant.name();
            else return Level.Heavy.name();
        }

    }

    @NotNull @Override public ACFTLogRecyclerAdapter.ACFTLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.recyclerview_acft_log,parent,false);
        return (new ACFTLogViewHolder(view));
    }

    @Override public void onBindViewHolder(@NonNull ACFTLogViewHolder holder, int position) {
        holder.record = list.get(position);
        holder.binding.setViewholder(holder);
    }

    @Override public int getItemCount() { return list.size(); }

    public void deleteAllRecord(View root){
        final ArrayList<ACFTRecord<ACFTEvent>> backup = new ArrayList<>(list);
        list.clear(); notifyDataSetChanged();
        ACFTDBHelper dbHelper = new ACFTDBHelper(context);
        dbHelper.deleteAll(); dbHelper.close();
        Snackbar.make(root, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                .setAction(resources.getString(R.string.undo), v -> {
                    list = backup;  notifyDataSetChanged();
                    ACFTDBHelper dbHelper1 = new ACFTDBHelper(context);
                    dbHelper1.saveRecordList(list);  dbHelper1.close();
                }).show();
    }

}
