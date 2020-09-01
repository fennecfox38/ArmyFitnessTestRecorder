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
import mil.army.fitnesstest.recorder.abcp.ABCPDBHandler;
import mil.army.fitnesstest.recorder.abcp.ABCPRecord;
import mil.army.fitnesstest.databinding.RecyclerviewAbcpLogBinding;
import mil.army.fitnesstest.recorder.abcp.Item;

public class ABCPLogRecyclerAdapter extends RecyclerView.Adapter<ABCPLogRecyclerAdapter.ABCPLogViewHolder> {
    private Context context;
    private Resources resources;
    private ArrayList<ABCPRecord<Item>> list;

    public ABCPLogRecyclerAdapter(Context context){
        this.context = context; resources = context.getResources();
        list = ABCPDBHandler.getRecordList(context);
    }

    public class ABCPLogViewHolder extends RecyclerView.ViewHolder {
        RecyclerviewAbcpLogBinding binding;
        public ABCPRecord<Item> record;
        public ABCPLogViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                menu.setHeaderTitle(resources.getString(R.string.record));
                menu.add(0,0,0,resources.getString(R.string.share)).setOnMenuItemClickListener(item -> {
                    context.startActivity(new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, record.toString()).setType("text/plain"));
                    return false;
                });
                menu.add(0,1,1,resources.getString(R.string.delete)).setOnMenuItemClickListener(item -> {
                    ABCPDBHandler.deleteRecord(context, record);
                    list.remove(getAdapterPosition());  notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                            .setAction(resources.getString(R.string.undo), view1 -> {
                                list.add(record);   notifyItemInserted(list.size()-1);
                                ABCPDBHandler.insertRecord(context, record);
                            }).show();
                    return false;
                });
            });
            binding = DataBindingUtil.bind(itemView);
        }
        public int getPassedColor(boolean isPassed){ return resources.getColor(isPassed ? R.color.passed: R.color.failed); }
        public String getPassed(boolean isPassed){ return resources.getString(isPassed ? R.string.pass: R.string.fail); }
    }

    @NonNull @Override
    public ABCPLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.recyclerview_abcp_log,parent,false);
        return (new ABCPLogViewHolder(view));
    }

    @Override public void onBindViewHolder(@NonNull ABCPLogViewHolder holder, int position) {
        holder.record = list.get(position);
        holder.binding.setViewholder(holder);
    }

    @Override public int getItemCount() { return list.size(); }

    public void deleteAllRecord(View root){
        ABCPDBHandler.deleteAll(context);
        final ArrayList<ABCPRecord<Item>> backup = new ArrayList<>(list);
        list.clear(); notifyDataSetChanged();
        Snackbar.make(root, resources.getString(R.string.recordDeleted), Snackbar.LENGTH_SHORT)
                .setAction(resources.getString(R.string.undo), v -> {
                    list = backup;  notifyDataSetChanged();
                    ABCPDBHandler.saveRecordList(context, list);
                }).show();
    }

}
