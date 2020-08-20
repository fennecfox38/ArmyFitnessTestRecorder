package army.prt.recorder.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import army.prt.recorder.R;

public class LogRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int LOG_ACFT=0, LOG_APFT=1, LOG_ABCP=2;
    private Context context;
    private int logType;

    public LogRecyclerAdapter(Context context, int logType){ this.context = context; this.logType = logType; }

    public class ACFTLogViewHolder extends RecyclerView.ViewHolder{
        public ACFTLogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public class APFTLogViewHolder extends RecyclerView.ViewHolder{
        public APFTLogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public class ABCPLogViewHolder extends RecyclerView.ViewHolder{
        public ABCPLogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NotNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.recyclerview_log,parent,false);
        switch (viewType){
            case LOG_ACFT: return (new ACFTLogViewHolder(view));
            case LOG_APFT: return (new APFTLogViewHolder(view));
            case LOG_ABCP: return (new ABCPLogViewHolder(view));
        } return null;
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override public int getItemViewType(int position) { return logType; }
    @Override public int getItemCount() { return 2; }
}
