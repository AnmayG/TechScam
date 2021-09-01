package com.example.tycoongamev3;

import com.example.tycoongamev3.Business;
import com.example.tycoongamev3.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();
    private static final String[] managerNames = {
            "Bobby\nBurglar",
            "Niger\nPrince",
            "TokTik",
            "Spamuel\nSpam",
            "Sequel\nPerson",
            "Harvard\nGrad",
            "Mr. Red\nButton"
    };

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    private static final int COUNT = businesses.size();

    static {
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }

    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        return new PlaceholderItem(String.valueOf(position), managerNames[position], makeDetails(position));
    }

    private static String makeDetails(int position) {
        return "Manages\n" + businesses.get(position).getName();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final String details;

        public PlaceholderItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}