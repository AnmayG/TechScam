package com.example.tycoongamev3;

import android.content.res.Resources;
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
import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class Business {
    // The tasks and the low-level stuff that it needs like the elements
    private long taskStartTime = 0L;
    private final Element src;
    private final Runnable progressTask;
    private final Runnable repeatTask;
    private final Resources res;
    private final String packageName;

    // The data read from business_info.xml
    private final String name;
    private final int initCost;
    private final double costCoeff;
    private final double revenue;
    private final int cooldown;
    private final BigDecimal unlockCost;
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
    private BigDecimal costStorage;

    // General variables
    private int level = 1;
    private static final NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private int multiplier = 1;
    private boolean isManager = false;
    private boolean purchasable = false;
    private boolean levelable = false;
    private BigDecimal prestigeAmount = BigDecimal.ZERO;

    public String getName() { return name; }
    public int getInitCost() { return initCost; }
    public double getRevenue() { return revenue; }
    public int getCooldown() { return cooldown; }
    public int getLevel() { return level; }
    public boolean isUnlocked() { return unlocked; }
    public int getProgressValue() { return progressValue; }
    public void setTopBar(FrameLayout topBar) { this.topBar = topBar; }
    public BigDecimal getUnlockCost() { return unlockCost; }
    public BigDecimal getCostStorage() { return costStorage; }
    public boolean isPurchasable() { return purchasable; }
    public void setPurchasable(boolean purchasable) { this.purchasable = purchasable; }
    public void setLevelable(boolean levelable) { this.levelable = levelable; }

    public void setMultiplier(int multiplier) {
        this.multiplier *= multiplier;
        if(revView != null) {
            revView.setText(toCurrencyNotation(calculateRev(), true));
        }
    }

    public void setManager(boolean b){
        isManager = b;
        handler.removeCallbacks(this.progressTask);
        runRepeatTask(0);
    }

    public Business(Element source, Resources res, String packageName){
        this.src = source;
        this.handler = new Handler();
        this.res = res;
        this.packageName = packageName;

        this.name = getValue("name");
        this.initCost = Integer.parseInt(getValue("cost_init"));
        this.costCoeff = Double.parseDouble(getValue("cost_coeff"));
        this.revenue = Double.parseDouble(getValue("revenue"));
        this.cooldown = Integer.parseInt(getValue("cooldown"));
        this.unlockCost = BigDecimal.valueOf(Long.parseLong(getValue("unlock_cost")));
        this.img = getValue("img");

        this.progressTask = buildProgressTask();
        this.repeatTask = buildRepeatTask(0);
        this.costStorage = BigDecimal.valueOf(initCost * 100L);
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

            progressBar.incrementProgressBy(10);
            textView.setText(formatSecondsToTimeStamp(cooldown - seconds));

            progressValue = progressBar.getProgress();

            if (millis > cooldown * 1000L) {
                handler.removeCallbacks(this.progressTask);
                progressBar.setProgress(0);
                progressValue = 0;
                textView.setText(formatSecondsToTimeStamp(cooldown));
                numRuns = 0;
                isRunning = false;
                setMoneyView(calculateRev());
                return;
            }

            // This is technically recursive but android docs say its better since it's very lightweight
            // It's not constantly creating new things that get constantly allocated while never being deallocated
            // I don't have to wait for garbage collection to clean it up and I have more knowledge about milliseconds than TimerTasks.
            // but yeah it's just a reimplementation of TimerTasks so...
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

            progressBar.incrementProgressBy(10);
            textView.setText(formatSecondsToTimeStamp(cooldown - seconds));

            progressValue = progressBar.getProgress();

            if (millis > cooldown * 1000L) {
                handler.removeCallbacks(this.repeatTask);
                progressBar.setProgress(0);
                progressValue = 0;
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
            // but yeah it's just a reimplementation of TimerTasks so...
            handler.postAtTime(this.repeatTask, start + numRuns + 10);
        };
    }

    public BigDecimal calculateRev(){
        BigDecimal prestigeBenefit = pow(BigDecimal.valueOf(1.02), prestigeAmount);
        return BigDecimal.valueOf(revenue * level)
                .multiply(BigDecimal.valueOf(multiplier * 100L)
                        .multiply(prestigeBenefit));
    }

    // http://www.java2s.com/example/java-utility-method/bigdecimal-power/pow-bigdecimal-base-bigdecimal-exponent-2249f.html
    public static BigDecimal pow(BigDecimal base, BigDecimal exponent) {
        BigDecimal result;
        int signOf2 = exponent.signum();

        // Perform X^(A+B)=X^A*X^B (B = remainder)
        double dn1 = base.doubleValue();
        // Compare the same row of digits according to context
        BigDecimal n2 = exponent.multiply(new BigDecimal(signOf2)); // n2 is now positive
        BigDecimal remainderOf2 = n2.remainder(BigDecimal.ONE);
        BigDecimal n2IntPart = n2.subtract(remainderOf2);
        // Calculate big part of the power using context -
        // bigger range and performance but lower accuracy
        BigDecimal intPow = base.pow(n2IntPart.intValueExact());
        BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));
        result = intPow.multiply(doublePow);

        // Fix negative power
        if (signOf2 == -1)
            result = BigDecimal.ONE.divide(result, RoundingMode.HALF_UP);
        return result;
    }

    public BigDecimal calculateNewCost(){
        BigDecimal pow = BigDecimal.valueOf(costCoeff).pow(level);
        return pow.multiply(BigDecimal.valueOf(initCost * 100L));
    }

    public void setMoneyView(BigDecimal deposit){
        MainFragment.addMoney(deposit);
    }

    public static String toCurrencyNotation(BigDecimal d, boolean longForm){
        d = d.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN);
        if(d.compareTo(BigDecimal.valueOf(1000)) < 0 && d.compareTo(BigDecimal.valueOf(-1000)) > 0) {
            return dollarFormat.format(d);
        }else if(d.compareTo(BigDecimal.valueOf(-1000)) <= 0) {
            return "-$" + NumberNames.createString(d.negate(), longForm);
        }else{
            return "$" + NumberNames.createString(d, longForm);
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
            if(purchasable) {
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                levelView.setVisibility(View.VISIBLE);
                revView.setVisibility(View.VISIBLE);
                blocker.setVisibility(View.GONE);
                setMoneyView(unlockCost.multiply(BigDecimal.valueOf(-100L)));
                unlocked = true;
            }
        });

        button.setText(toCurrencyNotation(costStorage, false));
        revView.setText(toCurrencyNotation(calculateRev(), true));
        imageView.setImageDrawable(ResourcesCompat.getDrawable(res, res.getIdentifier(img, "drawable", packageName), null));

        imageView.setOnClickListener(view -> {
            if(!isRunning) {
                runProgressTask(0);
            }
        });

        button.setOnClickListener(view -> {
            if(levelable) {
                level++;
                levelView.setText(String.valueOf(level));

                BigDecimal c = calculateNewCost();
                button.setText(toCurrencyNotation(c, false));
                setMoneyView(costStorage.negate());
                costStorage = c;

                revView.setText(toCurrencyNotation(calculateRev(), true));
            }
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
                        "  Unlock Cost: %b\n" +
                        "  Image: %s\n" +
                        "  Element: %s\n",
                name, initCost, costCoeff, revenue, cooldown, unlockCost, img, src);
    }
}

