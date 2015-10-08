package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-10-08.
 */
public class UserPositionTable extends BaseTable{

    private int id;
    private String deviceId;
    private double lat;
    private double lon;
    private int i;
    private int j;
    private double x;
    private double y;
    private double heading;
    private double uncertainty;
    private long roundtrip;
    private String time;
    private int pid;
    private int fn;

    public UserPositionTable() {
    }

    public UserPositionTable(int id, String deviceId, double lat, double lon, int i, int j, double x, double y, double heading, double uncertainty, long roundtrip, String time, int pid, int fn) {
        this.id = id;
        this.deviceId = deviceId;
        this.lat = lat;
        this.lon = lon;
        this.i = i;
        this.j = j;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.uncertainty = uncertainty;
        this.roundtrip = roundtrip;
        this.time = time;
        this.pid = pid;
        this.fn = fn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public long getRoundtrip() {
        return roundtrip;
    }

    public void setRoundtrip(long roundtrip) {
        this.roundtrip = roundtrip;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFn() {
        return fn;
    }

    public void setFn(int fn) {
        this.fn = fn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UserPositionTable{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", i=" + i +
                ", j=" + j +
                ", x=" + x +
                ", y=" + y +
                ", heading=" + heading +
                ", uncertainty=" + uncertainty +
                ", roundtrip=" + roundtrip +
                ", time='" + time + '\'' +
                ", pid=" + pid +
                ", fn=" + fn +
                '}';
    }

    @Override
    public String toJson() {
        return null;
    }
}
