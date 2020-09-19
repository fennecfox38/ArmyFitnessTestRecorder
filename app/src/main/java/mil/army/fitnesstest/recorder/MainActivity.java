package mil.army.fitnesstest.recorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.view.GravityCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import mil.army.fitnesstest.R;
import mil.army.fitnesstest.ScaleChartActivity;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_main;
    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_acft, R.id.navigation_abcp, R.id.navigation_apft, R.id.navigation_log).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        });

        drawer_main = findViewById(R.id.drawer_main);
        NavigationView nav_main = findViewById(R.id.nav_main);
        Menu navMenu = nav_main.getMenu();
        /*navMenu.findItem(R.id.menu_ACFT_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.ACFT_REQUESTED));
        navMenu.findItem(R.id.menu_APFT_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.APFT_REQUESTED));
        navMenu.findItem(R.id.menu_ABCP_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.ABCP_REQUESTED));
        navMenu.findItem(R.id.menu_MOS_chart).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.MOS_CHART_REQUESTED));*/
        MenuItem.OnMenuItemClickListener onScaleChartClickListen = item -> {
            Toast.makeText(MainActivity.this,"Showing scale chart is in the plan.",Toast.LENGTH_SHORT).show();
            return false;
        };
        navMenu.findItem(R.id.menu_ACFT_scale).setOnMenuItemClickListener(onScaleChartClickListen);
        navMenu.findItem(R.id.menu_APFT_scale).setOnMenuItemClickListener(onScaleChartClickListen);
        navMenu.findItem(R.id.menu_ABCP_scale).setOnMenuItemClickListener(onScaleChartClickListen);
        navMenu.findItem(R.id.menu_MOS_chart).setOnMenuItemClickListener(onScaleChartClickListen);
        navMenu.findItem(R.id.menu_info).setOnMenuItemClickListener(item -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.info); builder.setIcon(R.drawable.ic_info);
            builder.setMessage(getString(R.string.infobody1)+ getString(R.string.app_name) +", "+ getPackageName()
                    +getString(R.string.infobody2)+"\n\n"+getString(R.string.infobody3)+"\n\n"+getString(R.string.infobody4)+"\n\n"+getString(R.string.infobody5));
            builder.setPositiveButton("Play Store", (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())).setPackage("com.android.vending")));
            builder.setNegativeButton("Email", (dialog, which) -> {
                String body = "*************************************\nFeedback for " + getPackageName() + "\nApp Version : ";
                try { body += getPackageManager().getPackageInfo(getPackageName(),0).versionName; }
                catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); body += "unknown"; }
                body += "\nDevice Model : " + Build.MODEL + "\nDevice OS Version : " + Build.VERSION.RELEASE + "\n*************************************\n\n\n";
                Intent intent = new Intent(Intent.ACTION_SENDTO).setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fennecfox38@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback For Army Fitness Test Recorder");
                intent.putExtra(Intent.EXTRA_TEXT,body);
                if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            });
            builder.setNeutralButton(R.string.Dismiss, (dialog, which) -> dialog.dismiss());
            builder.create().show();
            return false;
        });
        navMenu.findItem(R.id.menu_license).setIntent(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.apache.org/licenses/LICENSE-2.0.txt")));
        nav_main.setNavigationItemSelectedListener(item -> { drawer_main.closeDrawer(GravityCompat.START); return false; });
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(drawer_main.isDrawerOpen(GravityCompat.START)) drawer_main.closeDrawer(GravityCompat.START);
            else drawer_main.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onBackPressed() {
        if (drawer_main.isDrawerOpen(GravityCompat.START))
            drawer_main.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @BindingAdapter("android:selectedItemPosition")
    public static void setSelectedItemPosition(AppCompatSpinner spinner, int selection) {
        spinner.setSelection(selection);
    }
    @InverseBindingAdapter(attribute = "android:selection")
    public static int getSelectedItemPosition(AppCompatSpinner spinner) {
        return spinner.getSelectedItemPosition();
    }
}