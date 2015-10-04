package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * place表
 */
public class PlaceTable extends BaseTable {

    private int id;
    private String name;
    private String image;
    private String video;
    private String intro;
    private int cid;
    private double lng;
    private double lat;

    public PlaceTable() {
    }

    public PlaceTable(int id, String name, String image, String video, String intro, int cid, double lng, double lat) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.video = video;
        this.intro = intro;
        this.cid = cid;
        this.lng = lng;
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "PlaceTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", intro='" + intro + '\'' +
                ", cid='" + cid + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }

    @Override
    public String toJson() {
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"name\":" + "\"" + name + "\"" +
                ",\"image\":" + "\"" + image + "\"" +
                ",\"video\":" + "\"" + video + "\"" +
                ",\"intro\":" + "\"" + intro + "\"" +
                ",\"cid\":" + "\"" + cid + "\"" +
                ",\"lng\":" + "\"" + lng + "\"" +
                ",\"lat\":" + "\"" + lat + "\"" +
                "}";
        return json;
    }
}
