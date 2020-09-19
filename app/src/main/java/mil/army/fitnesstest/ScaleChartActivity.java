package mil.army.fitnesstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ScaleChartActivity extends AppCompatActivity {
    public static final int LICENSE_REQUESTED =0, ACFT_REQUESTED =1, APFT_REQUESTED =2, ABCP_REQUESTED =3, MOS_CHART_REQUESTED=4;
    public static final int[] layoutId={R.layout.layout_license,R.layout.layout_acft_scale_chart,R.layout.layout_apft_scale_chart,R.layout.layout_abcp_scale_chart,R.layout.layout_mos_chart};
    public static final int[] titleId={R.string.license, R.string.ACFTScale,R.string.APFTScale,R.string.ABCPScale,R.string.MOSChart};
    private int requested;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requested = getIntent().getIntExtra("requested",LICENSE_REQUESTED);
        setContentView(layoutId[requested]);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(titleId[requested]);

        if(requested == LICENSE_REQUESTED) {
            initLicense();
        }
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override public void onBackPressed() {
        super.onBackPressed();
    }


    private void initLicense(){
        WebView webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient()); // New windows won't pop up.
        WebSettings webSettings = webView.getSettings(); //세부 세팅 등록
        webSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        webSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        webSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        webSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        webSettings.setSupportZoom(true); // 화면 줌 허용 여부
        webSettings.setBuiltInZoomControls(true); // 화면 확대 축소 허용 여부
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        webSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.Q) webSettings.setForceDark(WebSettings.FORCE_DARK_ON);

        webView.loadUrl("file:///android_asset/License.html");
    }
}