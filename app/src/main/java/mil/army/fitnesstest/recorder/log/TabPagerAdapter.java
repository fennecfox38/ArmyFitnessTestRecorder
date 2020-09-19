package mil.army.fitnesstest.recorder.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mil.army.fitnesstest.R;

public class TabPagerAdapter extends RecyclerView.Adapter<TabPagerAdapter.ViewHolder> {
    private Context context;
    public static final int TAB_ACFT=0, TAB_APFT=1, TAB_ABCP=2;
    private View[] view = new View[3];
    private ACFTLogRecyclerAdapter acftAdapter;
    private APFTLogRecyclerAdapter apftAdapter;
    private ABCPLogRecyclerAdapter abcpAdapter;

    public TabPagerAdapter(Context context){
        this.context = context;
        acftAdapter = new ACFTLogRecyclerAdapter(context);
        apftAdapter = new APFTLogRecyclerAdapter(context);
        abcpAdapter = new ABCPLogRecyclerAdapter(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView_log);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view[viewType] = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.layout_container_log, parent, false);
        return new ViewHolder(view[viewType]);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (position){
            case TAB_ACFT: holder.recyclerView.setAdapter(acftAdapter); break;
            case TAB_APFT: holder.recyclerView.setAdapter(apftAdapter);break;
            case TAB_ABCP: holder.recyclerView.setAdapter(abcpAdapter);break;
        }
    }
    @Override public int getItemCount() { return 3; }
    @Override public int getItemViewType(int position) { return position; }

    public void actionDelete(int currentPage){
        switch (currentPage){
            case TAB_ACFT: acftAdapter.deleteAllRecord(view[TAB_ACFT]); break;
            case TAB_APFT: apftAdapter.deleteAllRecord(view[TAB_APFT]); break;
            case TAB_ABCP: abcpAdapter.deleteAllRecord(view[TAB_ABCP]); break;
        }
    }
    public void reloadPages(){
        acftAdapter.reloadList();
        apftAdapter.reloadList();
        abcpAdapter.reloadList();
    }
}
