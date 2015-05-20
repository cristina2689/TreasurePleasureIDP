package org.project.treasurepleasure.camera;

import android.location.Location;

public class Utils {

	public static FinishThread thread;
	/* set with compass */
	public static double orientationAngle;
	/* taken from API */
	public static double cameraViewAngle;
	/* computed below */
	public static double objectOrientationAngle;
	/* difference between objectOrientationAngle and orientationAngle */
	public static double differenceAngle;
	public static Location userLocation;
	public static float deltaDistance;
	public static float width;
	private static final double angle = 180;

	private static double getObjectOrientationAngle(LatLong point1, LatLong point2) {
		double x1, x2, y1, y2;
		double val = 0;
		x1 = point1.longitude;
		x2 = point2.longitude;
		y1 = point1.latitude;
		y2 = point2.latitude;
		if (y1 != y2)
			val = Math.toDegrees(Math.atan((x2 - x1) / (y2 - y1)));
		if (y2 < 0)
			return val + 180;
		return val;
	}

	public static boolean canBeSeen(LatLong point1, LatLong point2) {
		objectOrientationAngle = getObjectOrientationAngle(point1, point2);
		if (orientationAngle > objectOrientationAngle) {
			if (Math.abs(objectOrientationAngle - orientationAngle) > angle)
				differenceAngle = Math.abs(orientationAngle
						- objectOrientationAngle - 360);
			else
				differenceAngle = orientationAngle - objectOrientationAngle;
		} else {
			if (Math.abs(objectOrientationAngle - orientationAngle) > angle)
				differenceAngle = Math.abs(orientationAngle
						- objectOrientationAngle + 360);
			else
				differenceAngle = objectOrientationAngle - orientationAngle;
		}
		if (cameraViewAngle / 2 > differenceAngle)
			return true;
		return false;
	}

	public static boolean isNear(LatLong point) {
		Location treasureLocation = new Location("Treasure Location");
		treasureLocation.setLatitude(point.latitude);
		treasureLocation.setLongitude(point.longitude);
		if (userLocation.distanceTo(treasureLocation) < deltaDistance)
			return true;
		return false;
	}

	public static float getXposition() {
		return ((width * (float) differenceAngle) / (2 * (float) cameraViewAngle));
	}
}