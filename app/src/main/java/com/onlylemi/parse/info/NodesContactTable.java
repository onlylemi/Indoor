package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-09-20.
 *
 * nodes_contract表
 */
public class NodesContactTable extends BaseTable {

    private Integer id;
    private Integer n1;
    private Integer n2;
    private Integer pid;
    private Integer fn;

    public NodesContactTable() {
    }

    public NodesContactTable(Integer id, Integer n1, Integer n2, Integer pid, Integer fn) {
        this.id = id;
        this.n1 = n1;
        this.n2 = n2;
        this.pid = pid;
        this.fn = fn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public Integer getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
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
        return "NodesContactTable{" +
                "id=" + id +
                ", n1=" + n1 +
                ", n2=" + n2 +
                ", pid=" + pid +
                ", fn=" + fn +
                '}';
    }


    @Override
    public String toJson() {
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"n1\":" + "\"" + n1 + "\"" +
                ",\"n2\":" + "\"" + n2 + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                ",\"fn\":" + "\"" + fn + "\"" +
                "}";
        return json;
    }
}
