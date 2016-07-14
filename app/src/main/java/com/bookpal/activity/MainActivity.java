package com.bookpal.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bookpal.R;
import com.bookpal.fragment.AddFragment;
import com.bookpal.fragment.SearchFragment;
import com.bookpal.utility.MarshMallowUtils;
import com.bookpal.utility.Utility;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE_PERMISSIONS = 102;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private static int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkViewId();
        openDefaultFragment();

        // To check multiple permission call (for marshmallow permission system)
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(" Access Course Location", Manifest.permission.ACCESS_COARSE_LOCATION);
        hashMap.put(" Access Fine Location", Manifest.permission.ACCESS_FINE_LOCATION);
        if (MarshMallowUtils.checkForMultiplePermissionsToGrant(this, hashMap).size() != 0) {
            MarshMallowUtils.requestPermission(this, hashMap, REQUEST_CODE_PERMISSIONS);
        }
    }

    //put the callback(if needed) on request for permission (allowed or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length == 1) {
            //do your code here
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                MarshMallowUtils.showDialog(this, getString(R.string.permission_denial_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        }
    }

    private void openDefaultFragment() {
        Fragment fragment = (Fragment) SearchFragment.newInstance();
        setTitle(getString(R.string.findBook));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    private void linkViewId() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        if (id == R.id.nav_search) {
            fragment = (Fragment) SearchFragment.newInstance();
            replaceFragment(fragment, getString(R.string.findBook));
        } else if (id == R.id.nav_add) {

            // If user is logged in then navigate to add book screen else navigate to sign in screen
            if (Utility.isLoggedIn(this)) {
                fragment = (Fragment) AddFragment.newInstance();
                replaceFragment(fragment, getString(R.string.addBook));
            } else {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else if (id == R.id.nav_share) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment, String title) {
        setTitle(title);
        // Now insert the selected fragment in frame layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }
}
