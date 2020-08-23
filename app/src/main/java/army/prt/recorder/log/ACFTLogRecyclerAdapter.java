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

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import army.prt.recorder.R;
import army.prt.recorder.acft.ACFTRecord;
import army.prt.recorder.acft.event.Event;
import army.prt.recorder.databinding.RecyclerviewAcftLogBinding;

public class ACFTLogRecyclerAdapter extends RecyclerView.Adapter<ACFTLogRecyclerAdapter.ACFTLogViewHolder> {
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
            itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                menu.setHeaderTitle(resources.getString(R.string.record));
                menu.add(0,0,0,resources.getString(R.string.share)).setOnMenuItemClickListener(item -> {
                    //context.startActivity(new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, record.toString()).setType("text/plain"));
                    Toast.makeText(context,"Single Record sharing is not available for now.",Toast.LENGTH_SHORT).show();
                    return false;
                });
                menu.add(0,1,1,resources.getString(R.string.delete)).setOnMenuItemClickListener(item -> {
                    list.remove(getAdapterPosition());  notifyItemRemoved(getAdapterPosition());
                    ACFTDBHelper dbHelper = new ACFTDBHelper(context);
                    dbHelper.deleteRecord(record);  dbHelper.close();
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
        public String getQualifiedLevel(){ return (resources.getStringArray(R.array.Level)[record.qualifiedLevel]); }
        public String getAlter(){ return (resources.getStringArray(R.array.CardioEvent)[record.cardioAlter]); }
        public String getLevel(int sco){
            if(sco<60) return (resources.getStringArray(R.array.Level)[Event.FAIL]);
            else if(sco<65) return (resources.getStringArray(R.array.Level)[Event.MODERATE]);
            else if(sco<70) return (resources.getStringArray(R.array.Level)[Event.SIGNIFICANT]);
            else return (resources.getStringArray(R.array.Level)[Event.HEAVY]);
        }

    }

    @NotNull @Override public ACFTLogRecyclerAdapter.ACFTLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.recyclerview_acft_log,parent,false);
        return (new ACFTLogViewHolder(view));
    }

    @Override public void onBindViewHolder(@NonNull ACFTLogRecyclerAdapter.ACFTLogViewHolder holder, int position) {
        holder.record = list.get(position);
        holder.binding.setViewholder(holder);
    }

    @Override public int getItemCount() { return list.size(); }

    public void deleteAllRecord(View root){
        final ArrayList<ACFTRecord> backup = new ArrayList<>(list);
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
