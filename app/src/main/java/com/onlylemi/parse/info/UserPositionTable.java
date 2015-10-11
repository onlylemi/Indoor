package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-10-08.
 */
public class UserPositionTable extends BaseTable {

    private Integer id;
    private String deviceId;
    private Double lat;
    private Double lon;
    private Integer i;
    private Integer j;
    private Double x;
    private Double y;
    private Double heading;
    private Double uncertainty;
    private Long roundtrip;
    private String time;
    private Integer pid;
    private Integer fn;

    public UserPositionTable() {
    }

    public UserPositionTable(Integer id, String deviceId, Double lat, Double lon, Integer i, Integer j, Double x, Double y, Double heading, Double uncertainty, Long roundtrip, String time, Integer pid, Integer fn) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Integer getJ() {
        return j;
    }

    public void setJ(Integer j) {
        this.j = j;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public Long getRoundtrip() {
        return roundtrip;
    }

    public void setRoundtrip(Long roundtrip) {
        this.roundtrip = roundtrip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getFn() {
        return fn;
    }

    public void setFn(Integer fn) {
        this.fn = fn;
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
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"deviceId\":" + "\"" + deviceId + "\"" +
                ",\"lat\":" + "\"" + lat + "\"" +
                ",\"lon\":" + "\"" + lat + "\"" +
                ",\"i\":" + "\"" + i + "\"" +
                ",\"j\":" + "\"" + j + "\"" +
                ",\"x\":" + "\"" + x + "\"" +
                ",\"y\":" + "\"" + j + "\"" +
                ",\"heading\":" + "\"" + heading + "\"" +
                ",\"uncertainty\":" + "\"" + uncertainty + "\"" +
                ",\"roundtrip\":" + "\"" + roundtrip + "\"" +
                ",\"time\":" + "\"" + time + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                ",\"fn\":" + "\"" + fn + "\"" +
                "}";
        return json;
    }
}
