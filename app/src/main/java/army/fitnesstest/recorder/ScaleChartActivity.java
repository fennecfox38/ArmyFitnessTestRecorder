package army.fitnesstest.recorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class ScaleChartActivity extends AppCompatActivity {
    public static final int ACFT_REQUESTED =0, APFT_REQUESTED =1, ABCP_REQUESTED =2, MOS_CHART_REQUESTED=3;
    private int requested;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_chart);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        requested = getIntent().getIntExtra("requested",ACFT_REQUESTED);
        switch (requested){
            case ACFT_REQUESTED: actionBar.setTitle(R.string.ACFTScale); break;
            case APFT_REQUESTED: actionBar.setTitle(R.string.APFTScale); break;
            case ABCP_REQUESTED: actionBar.setTitle(R.string.ABCPScale); break;
            case MOS_CHART_REQUESTED: actionBar.setTitle(R.string.MOSChart); break;
        }

    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override public void onBackPressed() {
        super.onBackPressed();
    }
}