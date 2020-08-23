package army.prt.recorder.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import army.prt.recorder.R;

public class TabPagerAdapter extends PagerAdapter {
    public static final int TAB_ACFT=0, TAB_APFT=1, TAB_ABCP=2;
    private View[] view;
    private Context context;
    private ACFTLogRecyclerAdapter acftAdapter;
    TabPagerAdapter(Context context){
        view = new View[3];
        this.context = context;
        acftAdapter = new ACFTLogRecyclerAdapter(context);
    }

    @NotNull @Override public Object instantiateItem(ViewGroup container, int position) {
        if (context != null) {
            view[position] = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_container_log, container, false);
            if(position == TAB_ACFT){
                RecyclerView recyclerView = view[position].findViewById(R.id.recyclerView_log);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(acftAdapter);
            }
        }
        container.addView(view[position]);
        return view[position];
    }

    public void actionDelete(int currentPage){
        switch (currentPage){
            case TAB_ACFT: acftAdapter.deleteAllRecord(view[TAB_ACFT]); break;
            case TAB_APFT: break;
            case TAB_ABCP: break;
        }
    }

    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @Override public int getCount() { return 3; }
    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) { return (view == (View)object); }

}
