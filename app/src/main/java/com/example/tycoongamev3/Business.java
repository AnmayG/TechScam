package com.example.tycoongamev3;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Business {
    // The tasks and the low-level stuff that it needs like the elements
    private long taskStartTime = 0L;
    private final Element src;
    private final Runnable progressTask;
    private final Runnable repeatTask;

    // The data read from business_info.xml
    private final String name;
    private final int initCost;
    private final double costCoeff;
    private final double revenue;
    private final int cooldown;
    private final long unlockCost;
    private final String img;

    // The views associated with the business, created in BusinessRecyclerViewAdapter.java
    private TextView blocker = null;
    private TextView textView = null;
    private ImageView imageView = null;
    private ProgressBar progressBar = null;
    private Button button = null;
    private TextView levelView = null;
    private FrameLayout topBar = null;
    private TextView revView = null;

    // The values necessary to run the Runnables and make them work well
    private final Handler handler;
    private boolean isRunning = false;
    private long numRuns = 0;
    private int progressValue;
    private boolean unlocked;
    private long costStorage;

    // General variables
    private int level = 1;
    private static final NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public long getTaskStartTime() { return taskStartTime; }
    public void setTaskStartTime(long taskStartTime) { this.taskStartTime = taskStartTime; }
    public Element getSrc() { return src; }
    public Runnable getProgressTask() { return progressTask; }
    public Runnable getRepeatTask() {
        return repeatTask;
    }
    public String getName() { return name; }
    public int getInitCost() { return initCost; }
    public double getCostCoeff() { return costCoeff; }
    public double getRevenue() { return revenue; }
    public int getCooldown() { return cooldown; }
    public String getImg() { return img; }
    public int getLevel() {
        return level;
    }
    public boolean isUnlocked() { return unlocked; }
    public int getProgressValue() { return progressValue; }
    public void setTopBar(FrameLayout topBar) {
        this.topBar = topBar;
    }
    public long getUnlockCost() {
        return unlockCost;
    }

    public Business(Element source){
        src = source;
        handler = new Handler();

        name = getValue("name");
        initCost = Integer.parseInt(getValue("cost_init"));
        costCoeff = Double.parseDouble(getValue("cost_coeff"));
        revenue = Double.parseDouble(getValue("revenue"));
        cooldown = Integer.parseInt(getValue("cooldown"));
        unlockCost = Long.parseLong(getValue("unlock_cost"));
        img = getValue("img");

        progressTask = buildProgressTask();
        repeatTask = buildRepeatTask(0);
        costStorage = initCost * 100L;
    }

    private String getValue(String tag) {
        NodeList nodeList = src.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    //This code donated by: https://android-developers.googleblog.com/2007/11/stitch-in-time.html
    public Runnable buildProgressTask(){
        return () -> {
            if(blocker == null || textView == null || imageView == null || progressBar == null ||
                    button == null || topBar == null || revView == null || levelView == null){
                Log.e("Business", "View not attached to source\n" + this.toString());
                return;
            }

            final long start = taskStartTime;
            if(!isRunning){
                isRunning = true;
                // The progressBar is 2 frames behind: 1 for the numRuns (doesn't +10 fast enough) and 1 for the time (millis varies by ~5 millis and that adds up)
                progressBar.incrementProgressBy(10);
            }

            long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            numRuns += 10;

            // System.out.println(progressBar.getProgress() + " " + cooldown*1000 + " " + millis + " " + numRuns + " " + (start + millis));

            progressBar.incrementProgressBy(10);
            textView.setText(formatSecondsToTimeStamp(cooldown - seconds));

            progressValue = progressBar.getProgress();

            if (millis > cooldown * 1000L) {
                handler.removeCallbacks(this.progressTask);
                progressBar.setProgress(0);
                textView.setText(formatSecondsToTimeStamp(cooldown));
                numRuns = 0;
                isRunning = false;
                setMoneyView(calculateRev());
                return;
            }

            // This is technically recursive but android docs say its better since it's very lightweight
            // It's not constantly creating new things that get constantly allocated while never being deallocated
            // I don't have to wait for garbage collection to clean it up and I have more knowledge about milliseconds than TimerTasks.
            handler.postAtTime(this.progressTask, start + numRuns + 10);
        };
    }

    public Runnable buildRepeatTask(int delayMillis){
        // I'm creating a RepeatTask and a ProgressTask because they have different end behavior.
        // Also it's easier to understand at a higher level of abstraction if I use an if statement rather than pass a parameter.
        return () -> {
            if(blocker == null || textView == null || imageView == null || progressBar == null ||
                    button == null || topBar == null || revView == null || levelView == null){
                Log.e("Business", "View not attached to source\n" + this.toString());
                return;
            }

            final long start = taskStartTime;
            if(!isRunning){
                isRunning = true;
                // The progressBar is 2 frames behind: 1 for the numRuns (doesn't +10 fast enough) and 1 for the time (millis varies by ~5 millis and that adds up)
                progressBar.incrementProgressBy(10);
            }

            long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            numRuns += 10;

            // System.out.println(progressBar.getProgress() + " " + cooldown*1000 + " " + millis + " " + numRuns + " " + (start + millis));

            progressBar.incrementProgressBy(10);
            textView.setText(formatSecondsToTimeStamp(cooldown - seconds));

            progressValue = progressBar.getProgress();

            if (millis > cooldown * 1000L) {
                handler.removeCallbacks(this.repeatTask);
                progressBar.setProgress(0);
                textView.setText(formatSecondsToTimeStamp(cooldown));
                numRuns = 0;
                isRunning = false;
                setMoneyView(calculateRev());
                runRepeatTask(delayMillis);
                return;
            }

            // This is technically recursive but android docs say its better since it's very lightweight
            // It's not constantly creating new things that get constantly allocated while never being deallocated
            // I don't have to wait for garbage collection to clean it up and I have more knowledge about milliseconds than TimerTasks.
            handler.postAtTime(this.repeatTask, start + numRuns + 10);
        };
    }

    public long calculateRev(){
        return (long) (revenue * level * 100);
    }

    public long calculateNewCost(){
        return (long) (initCost * Math.pow(costCoeff, level) * 100);
    }

    public void setMoneyView(long deposit){
        TextView moneyView = topBar.findViewById(R.id.topTextView);
        System.out.println(deposit + " " + MainFragment.getMoney());
        MainFragment.addMoney(deposit);
        moneyView.setText(toCurrencyNotation(MainFragment.getMoney()));
    }

    public static String toCurrencyNotation(long d){
        if(d < 1000 && d > -1000) {
            return dollarFormat.format(d / 100.0);
        }else if(d <= -1000) {
            return "-$" + NumberNames.createString(new BigDecimal(String.valueOf(-1 * d / 100.0)));
        }else{
            return "$" + NumberNames.createString(new BigDecimal(String.valueOf(d / 100.0)));
        }
    }

    /**
     * Get the Views that the Business will update as it ticks through its tasks.
     * It also sets their onClickListeners so that everything is all up-to-date.
     * @param bl The blocker TextView
     * @param t The clock TextView
     * @param i The image ImageView
     * @param p The progress bar ProgressBar
     * @param b The level button Button
     * @param l The level display TextView
     */
    public void setViews(TextView bl, TextView t, ImageView i, ProgressBar p, Button b, TextView l,
                         TextView r){
        blocker = bl;
        textView = t;
        imageView = i;
        progressBar = p;
        button = b;
        levelView = l;
        revView = r;

        // This has everything show up when the blocker is clicked or purchased
        blocker.setOnClickListener(view -> {
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            levelView.setVisibility(View.VISIBLE);
            revView.setVisibility(View.VISIBLE);
            blocker.setVisibility(View.GONE);
            unlocked = true;
        });

        button.setText(toCurrencyNotation(costStorage));
        revView.setText(toCurrencyNotation(calculateRev()));

        imageView.setOnClickListener(view -> {
            if(!isRunning) {
                runProgressTask(0);
            }
        });

        button.setOnClickListener(view -> {
            level++;
            levelView.setText(String.valueOf(level));

            long c = calculateNewCost();
            button.setText(toCurrencyNotation(c));
            setMoneyView(-1 * costStorage);
            costStorage = c;

            revView.setText(toCurrencyNotation(calculateRev()));
        });
    }

    /**
     * Runs the Runnable object located within the Business.
     * @param delayMillis The number of milliseconds that should pass before the task is executed.
     */
    public void runProgressTask(int delayMillis){
        taskStartTime = SystemClock.uptimeMillis();
        handler.removeCallbacks(progressTask);
        handler.postDelayed(progressTask, delayMillis);
    }

    public void runRepeatTask(int delayMillis){
        taskStartTime = SystemClock.uptimeMillis();
        handler.removeCallbacks(repeatTask);
        handler.postDelayed(repeatTask, delayMillis);
    }

    public String formatSecondsToTimeStamp(int seconds){
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String out = "" + seconds;
        if(seconds < 10){
            out = "0" + out;
        }
        return minutes + ":" + out;
    }

    /**
     * Return information about the Business object as a string.
     * @return String containing the formatted values about the object.
     */
    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US,
                "Business Object: \n" +
                        "  Name: %s\n" +
                        "  Init Cost: %d\n" +
                        "  Cost Coeff: %f\n" +
                        "  Revenue: %f\n" +
                        "  Cooldown: %d\n" +
                        "  Unlock Cost: %d\n" +
                        "  Image: %s\n" +
                        "  Element: %s\n",
                name, initCost, costCoeff, revenue, cooldown, unlockCost, img, src);
    }
}

