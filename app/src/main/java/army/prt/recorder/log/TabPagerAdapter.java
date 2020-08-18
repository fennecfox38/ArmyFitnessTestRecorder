package army.prt.recorder.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import army.prt.recorder.R;

public class TabPagerAdapter extends PagerAdapter {
    public static final int TAB_ACFT=0, TAB_APFT=1, TAB_ABCP=2;
    private Context context;
    TabPagerAdapter(Context context){ this.context = context; }

    @NotNull @Override public Object instantiateItem(ViewGroup container, int position) {
        View view = null ;
        if (context != null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                    inflate(R.layout.layout_container_log, container, false);
            ((TextView) view.findViewById(R.id.txt_sample)).setText("Page "+String.valueOf(position+1));
        }
        container.addView(view) ;
        return view ;
    }

    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @Override public int getCount() { return 3; }
    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) { return (view == (View)object); }
}
