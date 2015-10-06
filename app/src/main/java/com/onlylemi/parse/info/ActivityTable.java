package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * activity表
 */
public class ActivityTable extends BaseTable {

    private int id;
    private String name;
    private String image;
    private String time;
    private String intro;

    private int vid;

    public ActivityTable() {
    }

    public ActivityTable(String time, int id, String name, String image, String intro, int vid) {
        this.time = time;
        this.id = id;
        this.name = name;
        this.image = image;
        this.intro = intro;
        this.vid = vid;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    @Override
    public String toString() {
        return "ActivityTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", time='" + time + '\'' +
                ", intro='" + intro + '\'' +
                ", vid=" + vid +
                '}';
    }

    @Override
    public String toJson() {
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"name\":" + "\"" + name + "\"" +
                ",\"image\":" + "\"" + image + "\"" +
                ",\"time\":" + "\"" + time + "\"" +
                ",\"intro\":" + "\"" + intro + "\"" +
                ",\"vid\":" + "\"" + vid + "\"" +
                "}";
        return json;
    }
}
