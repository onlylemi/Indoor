package com.onlylemi.parse;

import com.onlylemi.parse.info.BaseTable;

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
    List<BaseTable> getActivityTableList(int vid);

    /**
     * 得到City表数据
     *
     * @return
     */
    List<BaseTable> getCityTableList();

    /**
     * 得到FloorPlan表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<BaseTable> getFloorPlanTableList(int pid, int fn);

    /**
     * 得到NodesContact表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<BaseTable> getNodesContactTableList(int pid, int fn);

    /**
     * 得到Nodes表数据
     *
     * @param pid 地区表id
     * @param fn  楼层
     * @return
     */
    List<BaseTable> getNodesTableList(int pid, int fn);

    /**
     * 得到Place表数据
     *
     * @param cid 城市表id
     * @return
     */
    List<BaseTable> getPlaceTableList(int cid);

    /**
     * 得到Views表数据
     *
     * @param pid 地区表id
     * @return
     */
    List<BaseTable> getViewsTableList(int pid);

}
