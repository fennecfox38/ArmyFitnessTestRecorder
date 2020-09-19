package mil.army.fitnesstest.recorder.log;

import android.content.ContentResolver;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.recorder.DBHelper;
import mil.army.fitnesstest.recorder.FileProvider;

import static android.app.Activity.RESULT_OK;

public class LogFragment extends Fragment{
    private static int REQUEST_IMPORT_DB = 101, REQUEST_EXPORT_DB = 102;
    private static int[] TAB_NAME = {R.string.title_acft, R.string.title_apft, R.string.title_abcp};
    private ViewPager2 viewPager;
    private TabPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_log, container, false);

        viewPager = root.findViewById(R.id.viewPager_log) ;
        pagerAdapter = new TabPagerAdapter(requireContext());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(root.findViewById(R.id.tabLayout_log),viewPager, (tab, position) -> {
            tab.setText(TAB_NAME[position]);
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
        return root;
    }

    @Override public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0,0,0,getString(R.string.Import)).setIcon(R.drawable.ic_import).setOnMenuItemClickListener(item -> {
            startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("application/*"),getString(R.string.Import)),REQUEST_IMPORT_DB);
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0,1,1,getString(R.string.Export)).setIcon(R.drawable.ic_share).setOnMenuItemClickListener(item -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
            builder.setTitle(getString(R.string.Export)); builder.setIcon(R.drawable.ic_share);
            builder.setItems(new String[]{getString(R.string.saveDB),getString(R.string.shareDB), getString(R.string.shareXLS)}, (dialog, which) -> {
                switch (which){
                    case 0: intentSaveDB(); break; case 1: intentShareDB(); break; case 2: intentShareXLS(); break;
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
        if(data == null || resultCode != RESULT_OK) return;
        Uri fileUri = Objects.requireNonNull(data.getData());
        ContentResolver resolver = requireContext().getContentResolver();
        if(requestCode == REQUEST_IMPORT_DB){
            try {
                File imported = FileProvider.getTempFile(requireContext(),fileUri);

                FileProvider.fileIO(resolver, fileUri, FileProvider.getDBUri(requireContext()));
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        else if(requestCode == REQUEST_EXPORT_DB){
            try { FileProvider.fileIO(resolver, FileProvider.getDBUri(requireContext()), fileUri); }
            catch (IOException e) { e.printStackTrace(); }
        }
        pagerAdapter.reloadPages();
    }

    private void intentSaveDB() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(FileProvider.dbMIME);
        intent.putExtra(Intent.EXTRA_TITLE, FileProvider.dbName);
        startActivityForResult(intent, REQUEST_EXPORT_DB);
    }
    public void intentShareDB(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(FileProvider.dbMIME);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getDBUri(requireContext()));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareDB)));
    }
    public void intentShareXLS(){
        try (DBHelper dbHelper = new DBHelper(requireContext())) {
            dbHelper.exportExcel(requireContext());
        } // try-with-resources automatically calls dbHelper.close()
        catch (IOException e) { e.printStackTrace(); }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(FileProvider.xlsMIME);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getXLSUri(requireContext()));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareXLS)));
    }

}