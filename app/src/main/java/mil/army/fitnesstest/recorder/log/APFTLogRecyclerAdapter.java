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

import java.util.ArrayList;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.databinding.RecyclerviewApftLogBinding;
import mil.army.fitnesstest.recorder.apft.APFTDBHandler;
import mil.army.fitnesstest.recorder.apft.APFTRecord;
import mil.army.fitnesstest.recorder.apft.event.APFTEvent;

public class APFTLogRecyclerAdapter extends RecyclerView.Adapter<APFTLogRecyclerAdapter.APFTViewHolder> {
    Context context;
    Resources resources;
    ArrayList<APFTRecord<APFTEvent>> list;

    public APFTLogRecyclerAdapter(Context context){
        this.context = context; resources = context.getResources();
        list = APFTDBHandler.getRecordList(context);
    }

    public class APFTViewHolder extends RecyclerView.ViewHolder{
        RecyclerviewApftLogBinding binding;
        public APFTRecord<APFTEvent> record;
        public APFTViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                menu.setHeaderTitle(resources.getString(R.string.record));
                menu.add(0,0,0,resources.getString(R.string.share)).setOnMenuItemClickListener(item -> {
                    context.startActivity(new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, record.toString()).setType("text/plain"));
                    return false;
                });
                menu.add(0,1,1,resources.getString(R.string.delete)).setOnMenuItemClickListener(item -> {
                    APFTDBHandler.deleteRecord(context, record);
                    list.remove(getAdapterPosition());  notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                            .setAction(resources.getString(R.string.undo), view1 -> {
                                list.add(record);   notifyItemInserted(list.size()-1);
                                APFTDBHandler.insertRecord(context,record);
                            }).show();
                    return false;
                });
            });
            binding = DataBindingUtil.bind(itemView);
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
        public String getPassed(boolean isPassed){ return resources.getString(isPassed ? R.string.pass: R.string.fail); }
    }

    @NonNull @Override public APFTViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.recyclerview_apft_log,parent,false);
        return (new APFTViewHolder(view));
    }
    @Override public void onBindViewHolder(@NonNull APFTViewHolder holder, int position) {
        holder.record = list.get(position);
        holder.binding.setViewholder(holder);
    }
    @Override public int getItemCount() { return list.size(); }

    public void deleteAllRecord(View root){
        final ArrayList<APFTRecord<APFTEvent>> backup = new ArrayList<>(list);
        list.clear(); notifyDataSetChanged();
        APFTDBHandler.deleteAll(context);
        Snackbar.make(root, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                .setAction(resources.getString(R.string.undo), v -> {
                    list = backup;  notifyDataSetChanged();
                    APFTDBHandler.saveRecordList(context, list);
                }).show();
    }
}
