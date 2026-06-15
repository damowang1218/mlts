package com.example.mlts.lts;

public interface LtsLocationCallback {
    void onSuccess(LtsLocationResult result);
    void onError(int code, String message);
}