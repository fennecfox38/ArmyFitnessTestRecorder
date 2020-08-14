package army.prt.recorder;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    DrawerLayout drawer_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer_main = findViewById(R.id.drawer_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_acft, R.id.navigation_abcp, R.id.navigation_log,R.id.navigation_apft)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener(){
            @Override public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
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