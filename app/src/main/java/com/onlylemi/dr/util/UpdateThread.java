package com.onlylemi.dr.util;

import com.onlylemi.dr.costant_interface.Constant;

/**
 * Created by 董神 on 2015/10/13.
 * I love programming
 */
public class UpdateThread {

    private static UpdateThread instance = null;
    private DiskCache diskCache;

    public static UpdateThread getInstance() {
        if(instance == null) {
            instance = new UpdateThread();
        }
        return instance;
    }

    private UpdateThread() {
        diskCache = DiskCache.getInstance();
    }

    public void updateActivity(final UpdateListener listener) {
        new JSONHttp(Constant.ActivityUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.ActivityTableName, s);
                if(JSONParse.parseActivity(s)) {
                    listener.success();
                }else {
                    listener.faile();
                }

            }
        }).start();
    }

    public void updateCity(final UpdateListener listener) {
        new JSONHttp(Constant.CityUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.CityTableName, s);
                if(JSONParse.parseCity(s)) {
                    listener.success();
                }else {
                    listener.faile();
                }
            }
        }).start();
    }

    public void updatePlace(final UpdateListener listener) {
        new JSONHttp(Constant.PlaceUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.PlaceTableName, s);
                if(JSONParse.parsePlace(s)) {
                    listener.success();
                }else {
                    listener.faile();
                }
            }
        }).start();
    }

    public void updateFloorPlan(final UpdateListener listener) {
        new JSONHttp(Constant.FloorPlanUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.FloorPlanTableName, s);
                if(JSONParse.parseFloor(s)){
                    listener.success();
                }else {
                    listener.faile();
                }
            }
        }).start();
    }

    public void updateNodes(final UpdateListener listener) {
        new JSONHttp(Constant.NodesUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.NodesTableName, s);
                if(JSONParse.parseNodes(s)) {
                   listener.success();
                }else {
                    listener.faile();
                }
            }
        }).start();
    }

    public void updateNodesContact(final UpdateListener listener) {
        new JSONHttp(Constant.NodesContactUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.NodesContactTableName, s);
                if(JSONParse.parseNodesContact(s)) {
                   listener.success();
                }else{
                    listener.faile();
                }
            }
        }).start();
    }

    public void updateViews(final UpdateListener listener) {
        new JSONHttp(Constant.ViewsUrl, new JSONHttp.JSONHttpReturn() {
            @Override
            public void JSONReturn(String s) {
                diskCache.write(diskCache.ViewsTableName, s);
                if(JSONParse.parseView(s)){
                   listener.success();
                }else {
                    listener.faile();
                }
            }
        }).start();
    }

    public interface UpdateListener {
        void success();
        void faile();
    }
}
