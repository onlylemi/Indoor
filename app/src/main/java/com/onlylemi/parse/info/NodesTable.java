package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * nodes表
 */
public class NodesTable extends BaseTable {

    private Integer id;
    private Integer n;
    private Integer x;
    private Integer y;
    private Integer pid;
    private Integer fn;

    public NodesTable() {
    }

    public NodesTable(Integer id, Integer n, Integer x, Integer y, Integer pid, Integer fn) {
        this.id = id;
        this.n = n;
        this.x = x;
        this.y = y;
        this.pid = pid;
        this.fn = fn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public Integer getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Integer getFn() {
        return fn;
    }

    public void setFn(int fn) {
        this.fn = fn;
    }

    @Override
    public String toString() {
        return "NodesTable{" +
                "id=" + id +
                ", n=" + n +
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
                ",\"n\":" + "\"" + n + "\"" +
                ",\"x\":" + "\"" + x + "\"" +
                ",\"y\":" + "\"" + y + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                ",\"fn\":" + "\"" + fn + "\"" +
                "}";
        return json;
    }
}
