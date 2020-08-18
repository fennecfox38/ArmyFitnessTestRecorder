package army.prt.recorder.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import army.prt.recorder.R;

public class LogFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private LogViewModel logViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_log, container, false);
        /*logViewModel = ViewModelProviders.of(this).get(LogViewModel.class);
        logViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });*/

        tabLayout = root.findViewById(R.id.tabLayout_log);
        viewPager = root.findViewById(R.id.viewPager_log) ;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
        viewPager.setAdapter(new TabPagerAdapter(requireContext())) ;
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(TabPagerAdapter.TAB_ACFT);

        return root;
    }
}