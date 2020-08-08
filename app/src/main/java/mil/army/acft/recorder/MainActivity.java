package mil.army.acft.recorder;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import mil.army.acft.recorder.ABCP.ABCPFragment;
import mil.army.acft.recorder.ACFT.ACFTFragment;
import mil.army.acft.recorder.History.HistoryFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private ACFTFragment frag_ACFT;
    private ABCPFragment frag_ABCP;
    private HistoryFragment frag_History;
    private MenuItem currentItem;
    public final static int FRAG_ACFT = 0, FRAG_ABCP =1, FRAG_HISTORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        frag_ACFT = new ACFTFragment();
        frag_ABCP = new ABCPFragment();
        frag_History = new HistoryFragment();
        pagerAdapter.addFragment(frag_ACFT);
        pagerAdapter.addFragment(frag_ABCP);
        pagerAdapter.addFragment(frag_History);

        viewPager = findViewById(R.id.viewpager_bottom_nav);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override public void onPageSelected(int position) {
                if(currentItem !=null) currentItem.setChecked(false);
                else bottomNavigationView.getMenu().getItem(0).setChecked(false);
                currentItem = bottomNavigationView.getMenu().getItem(position);
                currentItem.setChecked(true);
            }
            @Override public void onPageScrollStateChanged(int state) { }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_acft: viewPager.setCurrentItem(FRAG_ACFT); break;
                    case R.id.navigation_abcp: viewPager.setCurrentItem(FRAG_ABCP); break;
                    case R.id.navigation_history: viewPager.setCurrentItem(FRAG_HISTORY); break;
                }
                return true;
            }
        });

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: Toast.makeText(this,"HomeAsUp Pressed! :-P",Toast.LENGTH_SHORT).show(); break;
            case R.id.menu_setting: Toast.makeText(this,"Setting Pressed! :-P",Toast.LENGTH_SHORT).show(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter { //extends FragmentPagerAdapter{
        private final ArrayList<Fragment> fragList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager fragManager){
            super(fragManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        @NonNull @Override public Fragment getItem(int position) { return fragList.get(position); }
        @Override public int getCount() { return fragList.size(); }
        public void addFragment(Fragment fragment) { fragList.add(fragment); }
    }

}