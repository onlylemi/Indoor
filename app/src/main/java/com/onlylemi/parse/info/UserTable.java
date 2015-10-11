package com.onlylemi.parse.info;

/**
 * Created by only乐秘 on 2015-10-11.
 */
public class UserTable extends BaseTable {

    private Integer id;
    private String email;
    private String password;
    private String name;
    private Integer pid;

    public UserTable() {
    }

    public UserTable(Integer id, String email, String password, String name, Integer pid) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.pid = pid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "UserTable{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", pid=" + pid +
                '}';
    }

    @Override
    public String toJson() {
        String json = "{" +
                "\"id\":" + "\"" + id + "\"" +
                ",\"email\":" + "\"" + email + "\"" +
                ",\"password\":" + "\"" + password + "\"" +
                ",\"name\":" + "\"" + name + "\"" +
                ",\"pid\":" + "\"" + pid + "\"" +
                "}";
        return json;
    }
}
