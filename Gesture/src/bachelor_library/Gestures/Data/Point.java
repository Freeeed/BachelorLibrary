package bachelor_library.Gestures.Data;

import bachelor_library.Gestures.Graph.CurveSketching;

public class Point {

    public float x;
    public float y;
    public CurveSketching.PointType type = null;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = (float)x;
        this.y = (float)y;
    }

    public Point(Acceleration a) {
        this.x = a.x;
        this.y = a.y;
    }

    /**
     * Compares to acceleration vectors component wise
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point point = (Point) obj;

            return x == point.x && y == point.y;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "{" + (Math.round(x * 100) / 100f) + ", " + (Math.round(y * 100) / 100f) + "}";
    }
}
