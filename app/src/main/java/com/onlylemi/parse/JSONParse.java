package com.onlylemi.parse;


import com.onlylemi.parse.info.*;

import java.util.List;

/**
 * Created by only乐秘 on 2015-09-27.
 */
public interface JSONParse {

    /**
     * 得到Activity表数据
     *
     * @param vid 景点表id
     * @return
     */
    List<ActivityTable> getActivityTableList(int vid);

    /**
     * 得到FloorPlan表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<FloorPlanTable> getFloorPlanTableList(int pid, int fn);

    /**
     * 得到NodesContact表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<NodesContactTable> getNodesContactTableList(int pid, int fn);

    /**
     * 得到Nodes表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<NodesTable> getNodesTableList(int pid, int fn);

    /**
     * 得到Place表数据
     *
     * @param cid 城市表id
     * @return
     */
    List<PlaceTable> getPlaceTableList(int cid);

    /**
     * 得到Views表数据
     *
     * @param pid 地区表id
     * @return
     */
    List<ViewsTable> getViewsTableList(int pid, int fn);

}
