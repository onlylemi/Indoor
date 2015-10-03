package com.onlylemi.map.overlay;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

import com.onlylemi.indoor.R;
import com.onlylemi.map.MapView;
import com.onlylemi.map.core.MapBaseOverlay;

public class RouteOverlay extends MapBaseOverlay {

	private static final String TAG = "RouteOverlay";

	private MapView mapView;
	private List<Integer> routeList; // 路线集
	private List<PointF> nodeList; // 标记点集

	private int routeWidth; // 路线的宽

	private Bitmap routeStartBitmap;
	private Bitmap routeEndBitmap;

	private Paint paint;

	public RouteOverlay(MapView mapView, List<PointF> listNode,
			List<Integer> listRoute) {
		this.mapView = mapView;
		this.nodeList = listNode;
		this.routeList = listRoute;

		this.routeWidth = 10;

		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setColor(Color.BLUE);
		this.paint.setStyle(Style.FILL_AND_STROKE);

		routeStartBitmap = BitmapFactory.decodeResource(mapView.getResources(),
				R.mipmap.start_point);
		routeEndBitmap = BitmapFactory.decodeResource(mapView.getResources(),
				R.mipmap.end_point);
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onTouch(MotionEvent event) {

	}

	@Override
	public void draw(Canvas canvas, Matrix matrix, float currentZoom,
			float currentRotateDegrees) {
		canvas.save();
		if (isVisible && routeList != null) {
			for (int i = 0; i < routeList.size() - 1; i++) {
				// 起始点
				PointF start = nodeList.get(routeList.get(i));
				// 终止点
				PointF end = nodeList.get(routeList.get(i + 1));
				// 中心点
				// PointF midPoint =
				// AssistMath.getMidpointBetweenTwoPoints(start,
				// end);
				// // 距离
				// float distance =
				// AssistMath.getDistanceBetweenTwoPoints(start,
				// end);
				// // 角度
				// float angel = AssistMath.getAngleBetweenTwoPointsWithLevel(
				// start, end);

				// canvas.save();
				// canvas.setMatrix(matrix);
				// canvas.translate(midPoint.x, midPoint.y);
				// canvas.rotate(currentRotateDegrees - angel);
				// RectF rect = new RectF(-distance / 2, routeWidth / 2,
				// distance / 2, -routeWidth / 2);
				// canvas.drawRect(rect, paint);
				// canvas.restore();

				float goal1[] = { nodeList.get(routeList.get(i)).x,
						nodeList.get(routeList.get(i)).y };
				float goal2[] = { nodeList.get(routeList.get(i + 1)).x,
						nodeList.get(routeList.get(i + 1)).y };
				matrix.mapPoints(goal1);
				matrix.mapPoints(goal2);
				paint.setStrokeWidth(routeWidth);
				canvas.drawLine(goal1[0], goal1[1], goal2[0], goal2[1], paint);

			}

			// 画起始坐标点
			// canvas.save();
			float goal1[] = { nodeList.get(routeList.get(0)).x,
					nodeList.get(routeList.get(0)).y };
			float goal2[] = {
					nodeList.get(routeList.get(routeList.size() - 1)).x,
					nodeList.get(routeList.get(routeList.size() - 1)).y };
			matrix.mapPoints(goal1);
			matrix.mapPoints(goal2);
			canvas.drawBitmap(routeStartBitmap,
					goal1[0] - routeStartBitmap.getWidth() / 2, goal1[1]
							- routeStartBitmap.getHeight(), paint);
			canvas.drawBitmap(routeEndBitmap,
					goal2[0] - routeEndBitmap.getWidth() / 2, goal2[1]
							- routeEndBitmap.getHeight(), paint);
			// canvas.restore();
		}
		canvas.restore();
	}

	/**
	 * 设置路径list
	 * 
	 * @param routeList
	 */
	public void setRouteList(List<Integer> routeList) {
		this.routeList = routeList;
	}

	/**
	 * 设置节点集
	 * 
	 * @param nodeList
	 */
	public void setNodeList(List<PointF> nodeList) {
		this.nodeList = nodeList;
	}
}
