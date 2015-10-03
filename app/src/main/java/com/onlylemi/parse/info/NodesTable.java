package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * nodes表
 */
public class NodesTable extends BaseTable {

    private int id;
    private int n;
    private int x;
    private int y;
    private int pid;
    private int fn;

    public NodesTable() {
    }

    public NodesTable(int id, int n, int x, int y, int pid, int fn) {
        this.id = id;
        this.n = n;
        this.x = x;
        this.y = y;
        this.pid = pid;
        this.fn = fn;
    }

    public int getId() {
        return id;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
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
