package mil.army.fitnesstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class ScaleChartActivity extends AppCompatActivity {
    public static final int ACFT_REQUESTED =0, APFT_REQUESTED =1, ABCP_REQUESTED =2, MOS_CHART_REQUESTED=3;
    public static final int[] layoutId={R.layout.layout_acft_scale_chart,R.layout.layout_apft_scale_chart,R.layout.layout_abcp_scale_chart,R.layout.layout_mos_chart};
    public static final int[] titleId={R.string.ACFTScale,R.string.APFTScale,R.string.ABCPScale,R.string.MOSChart};
    private int requested;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requested = getIntent().getIntExtra("requested",ACFT_REQUESTED);
        setContentView(layoutId[requested]);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(titleId[requested]);

    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override public void onBackPressed() {
        super.onBackPressed();
    }
}