package cc.zsakvo.scpreader.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import cc.zsakvo.scpreader.R;
import cc.zsakvo.scpreader.fragment.ScpSeriesFragment;
import cc.zsakvo.scpreader.listener.ScpSeriesUrlListener;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private String[] scpSeriesUrls = new String[]{
            "http://scp-wiki-cn.wikidot.com/scp-series",
            "http://scp-wiki-cn.wikidot.com/scp-series-2",
            "http://scp-wiki-cn.wikidot.com/scp-series-3",
            "http://scp-wiki-cn.wikidot.com/scp-series-4",
            "http://scp-wiki-cn.wikidot.com/scp-series-5",
            "http://scp-wiki-cn.wikidot.com/scp-series-cn",
            "http://scp-wiki-cn.wikidot.com/scp-series-cn-2",
    };

    private ScpSeriesFragment mContentFragment;

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public int bindMenu() {
        return 0;
    }

    @Override
    public void clickMenu(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
                default:
                    break;
        }
    }

    @Override
    public void initView(View view) {
        drawerLayout = $(R.id.drawer_layout);
        toolbar = $(R.id.toolbar);
        //设置汉堡键
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = $(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        //设置 Toolbar
        toolbar.setTitle(getString(R.string.scp_s_1));
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        mContentFragment = (ScpSeriesFragment) fm.findFragmentById(R.id.content_frame);

        if (mContentFragment == null) {
            mContentFragment = new ScpSeriesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
        }

        loadScpSeries(scpSeriesUrls[0]);

    }

    @Override
    public void doOnStart() {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadScpSeries(String url){
        mContentFragment.setUrl(url);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.scp_s_1:
                loadScpSeries(scpSeriesUrls[0]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_s_1);
                menuItem.setChecked(true);
                break;
            case R.id.scp_s_2:
                loadScpSeries(scpSeriesUrls[1]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_s_2);
                menuItem.setChecked(true);
                break;
            case R.id.scp_s_3:
                loadScpSeries(scpSeriesUrls[2]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_s_3);
                menuItem.setChecked(true);
                break;
            case R.id.scp_s_4:
                loadScpSeries(scpSeriesUrls[3]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_s_4);
                menuItem.setChecked(true);
                break;
            case R.id.scp_s_5:
                loadScpSeries(scpSeriesUrls[4]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_s_5);
                menuItem.setChecked(true);
                break;
            case R.id.scp_cn_s_1:
                loadScpSeries(scpSeriesUrls[5]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_cn_s_1);
                menuItem.setChecked(true);
                break;
            case R.id.scp_cn_s_2:
                loadScpSeries(scpSeriesUrls[6]);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scp_cn_s_2);
                break;
        }
        return false;
    }
}
