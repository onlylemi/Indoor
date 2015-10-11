package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * views表
 */
public class ViewsTable extends BaseTable {

    private Integer id;
    private String name;
    private String image;
    private String video;
    private String intro;

    private Integer x;
    private Integer y;

    private Integer pid;
    private Integer fn;

    public ViewsTable() {
    }

    public ViewsTable(Integer id, String name, String image, String video, String intro, Integer x, Integer y, Integer pid, Integer fn) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
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

    public ViewsTable cloneSelf() {
        ViewsTable viewsTable = new ViewsTable();
        viewsTable.setFn(fn);
        viewsTable.setId(id);
        viewsTable.setImage(image);
        viewsTable.setIntro(intro);
        viewsTable.setName(name);
        viewsTable.setPid(pid);
        viewsTable.setVideo(video);
        viewsTable.setX(x);
        viewsTable.setY(y);
        return viewsTable;
    }
}
