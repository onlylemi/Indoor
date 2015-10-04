package com.onlylemi.dr.util;

/**
 * Created by 董神 on 2015/9/30.
 * I love programming
 */
public class Test {
    private static Test ourInstance = new Test();

    public static Test getInstance() {
        return ourInstance;
    }

    private Test() {
    }
}
