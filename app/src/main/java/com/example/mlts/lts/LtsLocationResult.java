package com.example.mlts.lts;

public class LtsLocationResult {
    public double latitude;
    public double longitude;
    public String address;       // 完整地址
    public String province;
    public String city;
    public String district;
    public int coordType;        // 0=WGS84, 1=BD09, 3=GCJ02
    public float accuracy;
    public long timestamp;

    public boolean isValid() {
        return latitude != 0 || longitude != 0;
    }
}