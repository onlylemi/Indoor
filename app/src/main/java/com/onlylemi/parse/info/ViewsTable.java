package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * views表
 */
public class ViewsTable extends BaseTable {

    private int id;
    private String name;
    private String image;
    private String video;
    private String intro;

    private int x;
    private int y;

    private int pid;
    private int fn;

    public ViewsTable() {
    }

    public ViewsTable(int id, String name, String image, String video, String intro, int x, int y, int pid, int fn) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.video = video;
        this.intro = intro;
        this.x = x;
        this.y = y;
        this.pid = pid;
        this.fn = fn;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    @Override
    public String toString() {
        return "ViewsTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", intro='" + intro + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", pid=" + pid +
                ", fn=" + fn +
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
                ",\"x\":" + "\"" + x + "\"" +
                ",\"y\":" + "\"" + y + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                ",\"fn\":" + "\"" + fn + "\"" +
                "}";
        return json;
    }


}
