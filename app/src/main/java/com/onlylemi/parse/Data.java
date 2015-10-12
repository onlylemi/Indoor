package com.onlylemi.parse;


import com.onlylemi.parse.info.ActivityTable;
import com.onlylemi.parse.info.CityTable;
import com.onlylemi.parse.info.FloorPlanTable;
import com.onlylemi.parse.info.NodesContactTable;
import com.onlylemi.parse.info.NodesTable;
import com.onlylemi.parse.info.PlaceTable;
import com.onlylemi.parse.info.ViewsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 董神 on 2015/9/30.
 * I love programming
 * all data collection
 */
public class Data {

    public static List<ActivityTable> activityTableList = new ArrayList<>();
    private static List<CityTable> cityTableList = new ArrayList<>();
    public static List<FloorPlanTable> floorPlanTableList = new ArrayList<>();
    public static List<NodesContactTable> nodesContactTableList = new ArrayList<>();
    public static List<NodesTable> nodesTableList = new ArrayList<>();
    public static List<PlaceTable> placeTableList = new ArrayList<>();
    public static List<ViewsTable> viewTableList = new ArrayList<>();
    private static List<String> strings;

    /**
     * 得到城市列表
     *
     * @return 城市列表
     */
    public static List<String> getOnlyCity() {
        List<String> string = new ArrayList<>();
            for (int i = 0; i < cityTableList.size(); i++) {
                String s = cityTableList.get(i).getName();
                string.add(s);
            }
        return string;
    }

    public static int getCityId(String city) {
        int id = -1;
        if (cityTableList != null) {
            for (CityTable cityTable : cityTableList) {
                if (cityTable.getName().equals(city)) {
                    id = cityTable.getId();
                }
            }
        }
        return id;
    }

    public static List<ActivityTable> getActivityTableList(int vid) {
        List<ActivityTable> list = new ArrayList<>();
        for (ActivityTable activityTable : activityTableList) {
            if (activityTable.getVid() == vid) {
                list.add(activityTable);
            }
        }
        return list;
    }

    public static List<FloorPlanTable> getFloorPlanTableList(int pid, int fn) {
        List<FloorPlanTable> list = new ArrayList<>();
        for (FloorPlanTable floorPlanTable : floorPlanTableList) {
            if (floorPlanTable.getPid() == pid && floorPlanTable.getFn() == fn) {
                list.add(floorPlanTable);
            }
        }
        return list;
    }

    public static List<NodesContactTable> getNodesContactTableList(int pid, int fn) {
        List<NodesContactTable> list = new ArrayList<>();
        for (NodesContactTable nodesContactTable : nodesContactTableList) {
            if (nodesContactTable.getPid() == pid && nodesContactTable.getFn() == fn) {
                list.add(nodesContactTable);
            }
        }
        return list;
    }

    public static List<NodesTable> getNodesTableList(int pid, int fn) {
        List<NodesTable> list = new ArrayList<>();
        for (NodesTable nodesTable : nodesTableList) {
            if (nodesTable.getPid() == pid && nodesTable.getFn() == fn) {
                list.add(nodesTable);
            }
        }
        return list;
    }

    public static List<PlaceTable> getPlaceTableList(int cid) {
        List<PlaceTable> list = new ArrayList<>();
        for (PlaceTable placeTable : placeTableList) {
            if (placeTable.getCid() == cid) {
                list.add(placeTable);
            }
        }
        return list;
    }

    public static List<ViewsTable> getViewsTableList(int pid, int fn) {
        List<ViewsTable> list = new ArrayList<>();
        for (ViewsTable viewsTable : viewTableList) {
            if (viewsTable.getPid() == pid && viewsTable.getFn() == fn) {
                list.add(viewsTable);
            }
        }
        return list;
    }

    public synchronized static List<CityTable> getCityTableList() {
            return cityTableList;
    }

}
