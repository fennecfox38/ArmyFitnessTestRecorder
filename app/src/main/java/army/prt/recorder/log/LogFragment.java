package army.prt.recorder.log;

import android.content.Intent;
import android.net.Uri;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;

import army.prt.recorder.R;

public class LogFragment extends Fragment {
    private View root;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.fragment_log, container, false);

        tabLayout = root.findViewById(R.id.tabLayout_log);
        viewPager = root.findViewById(R.id.viewPager_log) ;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
        pagerAdapter = new TabPagerAdapter(requireContext());
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
                    case 0:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM,
                                new FileProvider(requireContext()).getDatabaseUri());
                        shareIntent.setType("application/vnd.sqlite3"); //shareIntent.setType("application/*");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareDB)));
                        break;
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

    public void shareXLS(){
        Workbook workbook = new HSSFWorkbook();
        ACFTDBHelper acftDBHelper = new ACFTDBHelper(requireContext());
        acftDBHelper.exportExcel(workbook); acftDBHelper.close();
        //apftDBHelper
        ABCPDBHelper abcpDBHelper = new ABCPDBHelper(requireContext());
        abcpDBHelper.exportExcel(workbook); abcpDBHelper.close();
        try{
            FileProvider xlsProvider = new FileProvider(requireContext());
            FileOutputStream fileOutputStream = new FileOutputStream(xlsProvider.xls);
            workbook.write(fileOutputStream);

            Uri uri = xlsProvider.getXLSUri();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/excel"); //shareIntent.setType("application/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareXLS)));

        } catch (IOException e){ e.printStackTrace(); }
    }
}