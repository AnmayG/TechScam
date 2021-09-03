package com.example.tycoongamev3;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeContent {

    /**
     * An array of sample (Upgrade) items.
     */
    public static final List<UpgradeItem> ITEMS = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();
    private static final ArrayList<String> upgradeNames = new ArrayList<>();

    /**
     * A map of sample (Upgrade) items, by ID.
     * Came with standard ListView template.
     */
    public static final Map<String, UpgradeItem> ITEM_MAP = new HashMap<>();

    // This is a static version of the constructor that gets run when the class is loaded.
    // It came with the template.
    static {
        readFile("upgrade_names.txt");
        for (int i = 0; i < upgradeNames.size(); i++) {
            addItem(createUpgradeItem(i));
        }
    }

    /**
     * Reads the file "upgrade_names.txt" and updates upgradeNames using those values
     * Source: https://stackoverflow.com/questions/9544737/read-file-from-assets
     */
    public static void readFile(String fileName){
        BufferedReader reader = null;
        try {
            Context mActivityContext = MainActivity.getmContext().getApplicationContext();
            reader = new BufferedReader(
                    new InputStreamReader(mActivityContext.getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                upgradeNames.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                }
            }
        }

        if(upgradeNames.size() % businesses.size() != 0){
            // logs are actually pretty useful
            Log.e("UPGRADE_CONTENT", "Upgrade names not available for all businesses.");
        }
    }

    private static void addItem(UpgradeItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static UpgradeItem createUpgradeItem(int position) {
        // Multiply by 50,000 because needs 100 for the /100, another 100 to start at 500, and a 5 for fun
        return new UpgradeItem(String.valueOf(position), upgradeNames.get(position), makeDetails(position),
                BigDecimal.valueOf(10).pow(position + 4).multiply(BigDecimal.valueOf(5)));
    }

    private static String makeDetails(int position) {
        int multi = 10 + 10 * (position/businesses.size());
        return "Increases " + businesses.get(position % businesses.size()).getName() + " profits by " + multi + "x.";
    }

    /**
     * A Upgrade item representing a piece of content.
     */

    public static class UpgradeItem {
        public final String id;
        public final String content;
        public final String details;
        public final BigDecimal price;
        public boolean activated = false;

        public UpgradeItem(String id, String content, String details, BigDecimal price) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.price = price;
        }

        @NonNull
        @Override
        public String toString() {
            return content;
        }
    }
}