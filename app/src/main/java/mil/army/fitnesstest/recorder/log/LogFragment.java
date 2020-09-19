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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.FileProvider;

import static android.app.Activity.RESULT_OK;

public class LogFragment extends Fragment{
    private static int[] TAB_NAME = {R.string.title_acft, R.string.title_apft, R.string.title_abcp};
    private ViewPager2 viewPager;
    private TabPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_log, container, false);

        viewPager = root.findViewById(R.id.viewPager_log) ;
        pagerAdapter = new TabPagerAdapter();
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(root.findViewById(R.id.tabLayout_log),viewPager, (tab, position) -> {
            tab.setText(TAB_NAME[position]);
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
        return root;
    }

    @Override public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0,0,0,getString(R.string.Import)).setIcon(R.drawable.ic_import).setOnMenuItemClickListener(item -> {
            startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("application/*"),getString(R.string.Import)),0);
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0,1,1,getString(R.string.share)).setIcon(R.drawable.ic_share).setOnMenuItemClickListener(item -> {
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
        menu.add(0,2,2,getString(R.string.delete)).setIcon(R.drawable.ic_delete).setOnMenuItemClickListener(item -> {
            pagerAdapter.actionDelete(viewPager.getCurrentItem());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != 0 || data == null || resultCode != RESULT_OK) return;
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(data.getData());
            FileOutputStream outputStream = new FileOutputStream(FileProvider.getDBFile(requireContext()));
            byte[] buff = new byte[1024];
            int read;
            while ((read = inputStream.read(buff, 0, buff.length)) > 0)
                outputStream.write(buff, 0, read);
            inputStream.close();
            outputStream.close();
            pagerAdapter.reloadPages();
        } catch (IOException e) { e.printStackTrace(); }
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

    private class TabPagerAdapter extends RecyclerView.Adapter<TabPagerAdapter.ViewHolder> {
        public static final int TAB_ACFT=0, TAB_APFT=1, TAB_ABCP=2;
        private View[] view = new View[3];
        private ACFTLogRecyclerAdapter acftAdapter = new ACFTLogRecyclerAdapter(requireContext());
        private APFTLogRecyclerAdapter apftAdapter = new APFTLogRecyclerAdapter(requireContext());
        private ABCPLogRecyclerAdapter abcpAdapter = new ABCPLogRecyclerAdapter(requireContext());

        public class ViewHolder extends RecyclerView.ViewHolder{
            RecyclerView recyclerView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                recyclerView = itemView.findViewById(R.id.recyclerView_log);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            }
        }

        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view[viewType] = ((LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
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

}