package army.fitnesstest.recorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_main;
    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_acft, R.id.navigation_abcp, R.id.navigation_log, R.id.navigation_apft)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
            }
        });


        drawer_main = findViewById(R.id.drawer_main);
        NavigationView nav_main = findViewById(R.id.nav_main);
        Menu navMenu = nav_main.getMenu();
        navMenu.findItem(R.id.menu_ACFT_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.ACFT_REQUESTED));
        navMenu.findItem(R.id.menu_APFT_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.APFT_REQUESTED));
        navMenu.findItem(R.id.menu_ABCP_scale).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.ABCP_REQUESTED));
        navMenu.findItem(R.id.menu_MOS_chart).setIntent(new Intent(getApplicationContext(), ScaleChartActivity.class).putExtra("requested", ScaleChartActivity.MOS_CHART_REQUESTED));
        nav_main.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer_main.closeDrawer(GravityCompat.START);
                return false;
            }
        });

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

}