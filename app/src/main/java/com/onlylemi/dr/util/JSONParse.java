package com.onlylemi.dr.util;

import android.util.Log;

import com.onlylemi.parse.Data;
import com.onlylemi.parse.info.ActivityTable;
import com.onlylemi.parse.info.CityTable;
import com.onlylemi.parse.info.FloorPlanTable;
import com.onlylemi.parse.info.NodesContactTable;
import com.onlylemi.parse.info.NodesTable;
import com.onlylemi.parse.info.PlaceTable;
import com.onlylemi.parse.info.ViewsTable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 董神 on 2015/10/12.
 * I love programming
 */
public class JSONParse {

    private static final String TAG = JSONParse.class.getSimpleName();

    public static boolean parsePlace(String s) {
        Data.placeTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("place");
            Data.placeTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                PlaceTable placeTable = new PlaceTable();
                JSONObject object = arrays.getJSONObject(i);
                placeTable.setName(object.getString("name"));
                placeTable.setId(object.getInt("id"));
                placeTable.setImage("http://indoor.onlylemi.com/" + object.getString("image"));
                placeTable.setVideo(object.getString("video"));
                placeTable.setIntro(object.getString("intro"));
                placeTable.setCid(object.getInt("cid"));
                placeTable.setLat(object.getDouble("lat"));
                placeTable.setLng(object.getDouble("lng"));
                placeTable.setAddress(object.getString("address"));
                Data.placeTableList.add(placeTable);
            }
            if (Data.placeTableList.size() == 0) {
                Log.e(TAG, "palce number : " + Data.placeTableList.size());
                return false;
            } else {
                Log.i(TAG, "palce number : " + Data.placeTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析difang列表时出错");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseActivity(String s) {
        Data.activityTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("activity");
            Data.activityTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                ActivityTable activityTable = new ActivityTable();
                JSONObject object = arrays.getJSONObject(i);
                activityTable.setName(object.getString("name"));
                activityTable.setId(object.getInt("id"));
                activityTable.setImage(object.getString("image"));
                activityTable.setStartTime(object.getString("start_time"));
                activityTable.setEndTime(object.getString("end_time"));
                activityTable.setIntro(object.getString("intro"));
                activityTable.setVid(object.getInt("vid"));
                Data.activityTableList.add(activityTable);
            }

            if (Data.activityTableList.size() == 0) {
                Log.e(TAG, "activity number : " + Data.activityTableList.size());
                return false;
            } else {
                Log.i(TAG, "activity number : " + Data.activityTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析活动列表时出错");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseView(String s) {
        Data.viewTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("views");
            Data.viewTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                ViewsTable viewsTable = new ViewsTable();
                JSONObject object = arrays.getJSONObject(i);
                viewsTable.setName(object.getString("name"));
                viewsTable.setId(object.getInt("id"));
                viewsTable.setImage("http://indoor.onlylemi.com/" + object.getString("image"));
                viewsTable.setVideo(object.getString("video"));
                viewsTable.setIntro(object.getString("intro"));
                viewsTable.setX(object.getInt("x"));
                viewsTable.setY(object.getInt("y"));
                viewsTable.setPid(object.getInt("pid"));
                viewsTable.setFn(object.getInt("fn"));
                Data.viewTableList.add(viewsTable);
            }
            if (Data.viewTableList.size() == 0) {
                Log.e(TAG, "views number : " + Data.viewTableList.size());
                return false;
            } else {
                Log.i(TAG, "views number : " + Data.viewTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析views列表时出错");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseFloor(String s) {
        Data.floorPlanTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("floor_plan");
            Data.floorPlanTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                FloorPlanTable floorPlanTable = new FloorPlanTable();
                JSONObject object = arrays.getJSONObject(i);
                floorPlanTable.setVenueid(object.getString("venueid"));
                floorPlanTable.setFloorid(object.getString("floorid"));
                floorPlanTable.setFloorplanid(object.getString("floorplanid"));
                floorPlanTable.setPid(object.getInt("pid"));
                floorPlanTable.setImage(object.getString("image"));
                floorPlanTable.setFn(object.getInt("fn"));
                floorPlanTable.setId(object.getInt("id"));
                Data.floorPlanTableList.add(floorPlanTable);
            }

            if (Data.floorPlanTableList.size() == 0) {
                Log.e(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                return false;
            } else {
                Log.i(TAG, "floorplan number : " + Data.floorPlanTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析floorplan列表时出错");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseNodesContact(String s) {
        Data.nodesContactTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("nodes_contact");
            Data.nodesContactTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                NodesContactTable nodesContact = new NodesContactTable();
                JSONObject object = arrays.getJSONObject(i);
                nodesContact.setN1(object.getInt("n1"));
                nodesContact.setPid(object.getInt("pid"));
                nodesContact.setN2(object.getInt("n2"));
                nodesContact.setFn(object.getInt("fn"));
                nodesContact.setId(object.getInt("id"));
                Data.nodesContactTableList.add(nodesContact);
            }

            if(Data.nodesContactTableList.size() == 0) {
                Log.e(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                return false;
            }else {
                Log.i(TAG, "NodesContact number : " + Data.nodesContactTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析nodesContactList列表时出错" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseNodes(String s) {
        Data.nodesTableList.clear();
        try {
            Data.nodesTableList.clear();
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("nodes");
            Data.nodesTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                NodesTable nodesTable = new NodesTable();
                JSONObject object = arrays.getJSONObject(i);
                nodesTable.setX(object.getInt("x"));
                nodesTable.setPid(object.getInt("pid"));
                nodesTable.setY(object.getInt("y"));
                nodesTable.setN(object.getInt("n"));
                nodesTable.setFn(object.getInt("fn"));
                nodesTable.setId(object.getInt("id"));
                Data.nodesTableList.add(nodesTable);
            }

            if(Data.nodesTableList.size() == 0) {
                Log.e(TAG, "Nodes number : " + Data.nodesTableList.size());
                return false;
            }else {
                Log.i(TAG, "Nodes number : " + Data.nodesTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析nodes列表时出错" + e.toString());
            return false;
        }
    }

    public static boolean parseCity(String s) {
        Data.cityTableList.clear();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arrays = jsonObject.getJSONArray("city");
            Data.cityTableList.clear();
            for (int i = 0; i < arrays.length(); i++) {
                CityTable city = new CityTable();
                JSONObject object = arrays.getJSONObject(i);
                city.setName(object.getString("name"));
                city.setId(object.getInt("id"));
                Data.cityTableList.add(city);
            }
            if(Data.cityTableList.size() == 0) {
                Log.e(TAG, "city number : " + Data.cityTableList.size());
                return false;
            }else {
                Log.i(TAG, "city number : " + Data.cityTableList.size());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "解析城市列表时出错");
            e.printStackTrace();
            return false;
        }
    }
}
