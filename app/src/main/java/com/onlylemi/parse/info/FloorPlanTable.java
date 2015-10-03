package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * floor_plan表
 */
public class FloorPlanTable extends BaseTable {

    private int id;
    private int fn;
    private String image;
    private String pid;
    private String floorplanid;
    private String floorid;
    private String venueid;

    public FloorPlanTable() {
    }

    public FloorPlanTable(int id, int fn, String image, String pid, String floorplanid, String floorid, String venueid) {
        this.id = id;
        this.fn = fn;
        this.image = image;
        this.pid = pid;
        this.floorplanid = floorplanid;
        this.floorid = floorid;
        this.venueid = venueid;
    }

    public int getId() {
        return id;
    }

    public int getFn() {
        return fn;
    }

    public void setFn(int fn) {
        this.fn = fn;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFloorplanid() {
        return floorplanid;
    }

    public void setFloorplanid(String floorplanid) {
        this.floorplanid = floorplanid;
    }

    public String getFloorid() {
        return floorid;
    }

    public void setFloorid(String floorid) {
        this.floorid = floorid;
    }

    public String getVenueid() {
        return venueid;
    }

    public void setVenueid(String venueid) {
        this.venueid = venueid;
    }

    @Override
    public String toString() {
        return "FloorPlanTable{" +
                "id=" + id +
                ", fn=" + fn +
                ", image='" + image + '\'' +
                ", pid='" + pid + '\'' +
                ", floorplanid='" + floorplanid + '\'' +
                ", floorid='" + floorid + '\'' +
                ", venueid='" + venueid + '\'' +
                '}';
    }

    @Override
    public String toJson() {
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"fn\":" + "\"" + fn + "\"" +
                ",\"image\":" + "\"" + image + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                ",\"floorplanid\":" + "\"" + floorplanid + "\"" +
                ",\"floorid\":" + "\"" + floorid + "\"" +
                ",\"venueid\":" + "\"" + venueid + "\"" +
                "}";
        return json;
    }
}
