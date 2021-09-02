package com.example.tycoongamev3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tycoongamev3.databinding.ActivityMainBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final ArrayList<Business> businesses = new ArrayList<>();
    private Handler handler;
    public static final String TAG = "MainActivity";
    @SuppressLint("StaticFieldLeak") // Fixed in UpgradeContent
    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = getApplicationContext();
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // This seems just like the <include> tag but it's dynamic so it might be different
//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            MainFragment fragment = new MainFragment();
//            transaction.replace(R.id.sample_content_fragment, fragment);
//            transaction.commit();
//        }

        // Create a new handler then create the BUSINESSES based off of the stored XML values
        handler = new Handler();
        populateSourceTasks();

        for (Business s: businesses) {
            Log.println(Log.DEBUG, TAG, s.toString());
        }

        loadPrefs("create");
    }

    @Override
    protected void onPause() {
        super.onPause();

        long time = System.currentTimeMillis();
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("time_save", time);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs("resume");
    }

    int counter = 0;

    public void loadPrefs(String name){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        long time_save = sharedPreferences.getLong("time_save", 0);
        System.out.println(name + " " + counter + ":");
        System.out.println(counter + " " + SystemClock.elapsedRealtime());
        System.out.println(counter + " " + System.currentTimeMillis() + " " + time_save + " " + (System.currentTimeMillis() - time_save));
        counter++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Read information from business_info.xml, then populate the BUSINESSES arraylist with BUSINESSES based on that information.
     */
    // This code section donated by: https://www.tutorialspoint.com/android/android_xml_parsers.htm
    public void populateSourceTasks(){
        try {
            InputStream is = getAssets().open("business_info.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("profit_source");

            for (int i=0; i<nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    businesses.add(new Business(element2));
                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }

    public static ArrayList<Business> getBusinesses(){
        return businesses;
    }
}