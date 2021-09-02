package com.example.tycoongamev3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// https://developer.android.com/guide/fragments/communicate
// I'm using a ViewModel to transfer data between the fragments so that I can have live updates for things like the money.
// This is better than using a FragmentManager and the transactions because this is actually live(ish) rather than based on a commit system.
// That means that I don't have to deal with commits that come at the wrong time or whatever
// Also, the FragmentManager that I found used something like ResultListeners, which only run when the fragment is opened/closed
// Which isn't very good since I want live updates of simple primitive data
// I think that the FragmentManager is supposed to be for view substitution or insertion rather than data transactions.
public class MoneyViewModel extends ViewModel {
    private final MutableLiveData<Long> money = new MutableLiveData<>(0L);

    public LiveData<Long> getMoney() {
        return money;
    }

    public void addMoney(long deposit) {
        if(money.getValue() != null) {
            long out = money.getValue() + deposit;
            money.setValue(out);
        } else {
            money.setValue(deposit);
        }
    }
}