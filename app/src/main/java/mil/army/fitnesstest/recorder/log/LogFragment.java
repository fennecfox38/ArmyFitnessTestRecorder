package mil.army.fitnesstest.recorder.log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.FileProvider;

public class LogFragment extends Fragment{
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_log, container, false);

        TabLayout tabLayout = root.findViewById(R.id.tabLayout_log);
        viewPager = root.findViewById(R.id.viewPager_log) ;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
        pagerAdapter = new TabPagerAdapter();
        viewPager.setAdapter(pagerAdapter) ;
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(TabPagerAdapter.TAB_ACFT);

        return root;
    }

    @Override public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0,0,0,getString(R.string.share)).setIcon(R.drawable.ic_share).setOnMenuItemClickListener(item -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            builder.setTitle(getString(R.string.share)); builder.setIcon(R.drawable.ic_share);
            builder.setItems(new String[]{getString(R.string.shareDB), getString(R.string.shareXLS)}, (dialog, which) -> {
                switch (which){
                    case 0: shareDB();break;
                    case 1: shareXLS(); break;
                }
            });
            builder.create().show();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0,1,1,getString(R.string.delete)).setIcon(R.drawable.ic_delete).setOnMenuItemClickListener(item -> {
            pagerAdapter.actionDelete(viewPager.getCurrentItem());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void shareDB(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.sqlite3"); //shareIntent.setType("application/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getDatabaseUri(requireContext()));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareDB)));
    }

    public void shareXLS(){
        Workbook workbook = new HSSFWorkbook();
        DBHelper dbHelper = new DBHelper(requireContext());
        dbHelper.exportExcel(workbook); dbHelper.close();
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(FileProvider.getXLSFile(requireContext()));
            workbook.write(fileOutputStream);
            workbook.close();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/excel"); //shareIntent.setType("application/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getXLSUri(requireContext()));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareXLS)));
        } catch (IOException e){ e.printStackTrace(); }
    }

    private class TabPagerAdapter extends PagerAdapter {
        public static final int TAB_ACFT=0, TAB_APFT=1, TAB_ABCP=2;
        private View[] view;
        private ACFTLogRecyclerAdapter acftAdapter;
        private ABCPLogRecyclerAdapter abcpAdapter;
        TabPagerAdapter(){
            view = new View[3];
            acftAdapter = new ACFTLogRecyclerAdapter(requireContext());
            abcpAdapter = new ABCPLogRecyclerAdapter(requireContext());
        }

        @NotNull @Override public Object instantiateItem(@NotNull ViewGroup container, int position) {
            view[position] = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_container_log, container, false);
            RecyclerView recyclerView = view[position].findViewById(R.id.recyclerView_log);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            switch (position){
                case TAB_ACFT: recyclerView.setAdapter(acftAdapter); break;
                case TAB_APFT: break;
                case TAB_ABCP: recyclerView.setAdapter(abcpAdapter);break;
            }
            container.addView(view[position]);
            return view[position];
        }

        public void actionDelete(int currentPage){
            switch (currentPage){
                case TAB_ACFT: acftAdapter.deleteAllRecord(view[TAB_ACFT]); break;
                case TAB_APFT: break;
                case TAB_ABCP: abcpAdapter.deleteAllRecord(view[TAB_ABCP]); break;
            }
        }

        @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
        @Override public int getCount() { return 3; }
        @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) { return (view == (View)object); }
    }

}