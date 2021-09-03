package com.example.tycoongamev3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.List;

import com.example.tycoongamev3.ManagerContent.ManagerItem;
import com.example.tycoongamev3.UpgradeContent.UpgradeItem;

// https://developer.android.com/guide/fragments/communicate
// I'm using a ViewModel to transfer data between the fragments so that I can have live updates for things like the money.
// This is better than using a FragmentManager and the transactions because this is actually live(ish) rather than based on a commit system.
// That means that I don't have to deal with commits that come at the wrong time or whatever
// Also, the FragmentManager that I found used something like ResultListeners, which only run when the fragment is opened/closed
// Which isn't very good since I want live updates of simple primitive data
// I think that the FragmentManager is supposed to be for view substitution or insertion rather than data transactions.
public class SaveViewModel extends ViewModel {
    private final MutableLiveData<BigDecimal> money = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<List<ManagerItem>> saveManagerValues = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> rep = new MutableLiveData<>();

    public LiveData<BigDecimal> getMoney() {
        return money;
    }

//    public void addMoney(long deposit) {
//        if(money.getValue() != null) {
//            BigDecimal out = money.getValue().add(BigDecimal.valueOf(deposit));
//            money.setValue(out);
//        } else {
//            money.setValue(BigDecimal.valueOf(deposit));
//        }
//    }

    public void addMoney(BigDecimal deposit) {
        if(money.getValue() != null) {
            BigDecimal out = money.getValue().add(deposit);
            money.setValue(out);
        } else {
            money.setValue(deposit);
        }
    }

    public void setMoneyZero() {
        money.setValue(BigDecimal.ZERO);
    }

    public MutableLiveData<List<ManagerItem>> getSaveManagerValues() {
        return saveManagerValues;
    }

    public void setSaveManagerValues(List<ManagerItem> l) {
        saveManagerValues.setValue(l);
    }

    public MutableLiveData<BigDecimal> getRep() {
        return rep;
    }

    public void addRep(BigDecimal bi) {
        if(rep.getValue() != null) {
            rep.setValue(rep.getValue().add(bi));
        } else {
            rep.setValue(bi);
        }
    }
}