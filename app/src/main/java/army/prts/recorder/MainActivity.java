package army.prts.recorder;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_acft, R.id.navigation_abcp, R.id.navigation_log,R.id.navigation_more)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        /*navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()){
                    case R.id.navigation_acft: Toast.makeText(MainActivity.this,"navigated to ACFT",Toast.LENGTH_SHORT).show(); break;
                    case R.id.navigation_abcp: Toast.makeText(MainActivity.this,"navigated to ABCP",Toast.LENGTH_SHORT).show(); break;
                    case R.id.navigation_log: Toast.makeText(MainActivity.this,"navigated to Log",Toast.LENGTH_SHORT).show(); break;
                    case R.id.navigation_more: Toast.makeText(MainActivity.this,"navigated to More",Toast.LENGTH_SHORT).show(); break;
                }
            }
        });*/
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_setting)
            Toast.makeText(this,"Setting Pressed! :-P",Toast.LENGTH_SHORT).show();
        /*switch(item.getItemId()){
            case R.id.menu_setting: Toast.makeText(this,"Setting Pressed! :-P",Toast.LENGTH_SHORT).show(); break;
        }*/
        return super.onOptionsItemSelected(item);
    }

}