package bachelor_library.Gestures.Graph;

import bachelor_library.Gestures.Data.Point;

import java.util.ArrayList;

public class CurveSketching {

    protected ArrayList<Point> pointGraph;

    protected ArrayList<Point> extremPoints = new ArrayList<>();
    protected ArrayList<Point> inflectionPoints = new ArrayList<>();

    protected double previousSlopeExact;
    protected Slope previousSlope;
    protected Concavity previousConcavity;

    protected Point absoluteMin;
    protected Point absoluteMax;

    public CurveSketching(ArrayList<Point> pointGraph) {
        this.pointGraph = pointGraph;
    }

    public void Compute() {
        this.extremPoints.clear();
        this.inflectionPoints.clear();

        Point previous = null;
        for (Point p : pointGraph) {
            if (previous != null) {
                Compare(previous, p);
            }

            previous = p;
        }
    }

    public void Flatten() {
        // Find Extrema directly next to each other
        // Compare y value and decide to flatten or not.
        // Reanalyze after to find new strange extrema
    }

    protected void Compare(Point previous, Point current) {
        if (previous.x >= current.x) {
            // throw new RuntimeException("Invalid point order");
        }

        double slopeExact = calculateSlope(previous, current);
        Slope slope;
        Concavity concavity;

        // Slope
        if (previous.y > current.y) {
            slope = Slope.FALLING;
        } else if (previous.y < current.y) {
            slope = Slope.RISING;
        } else {
            slope = Slope.PLANE;
        }

        // Concavity
        if (previousConcavity == null) {
            concavity = Concavity.NONE;
        } else if (previousSlopeExact > slopeExact) {
            concavity = Concavity.RIGHT;
        } else if (previousSlopeExact < slopeExact) {
            concavity = Concavity.LEFT;
        } else {
            concavity = Concavity.NONE;
        }

        // Extrempunkte finden
        if (previousSlope != null && previousSlope != slope) {
            if (previousSlope == Slope.FALLING && slope == Slope.RISING) {
                this.addMinimum(previous);
            } else if (previousSlope == Slope.RISING && slope == Slope.FALLING) {
                this.addMaximum(previous);
            }
        }

        if (previousConcavity != null && previousConcavity != Concavity.NONE) {
            // Wendepunkte finden (Konkavitätswechsel)
            if (previousConcavity != concavity) {
                this.inflectionPoints.add(previous);
            }
        }

        if (absoluteMin == null || absoluteMin.y > current.y) {
            absoluteMin = current;
        }

        if (absoluteMax == null || absoluteMax.y < current.y) {
            absoluteMax = current;
        }

        previousSlopeExact = slopeExact;
        previousSlope = slope;
        previousConcavity = concavity;
    }

    private void addMaximum(Point point) {
        point.type = PointType.MAX;

        this.extremPoints.add(point);
    }

    private void addMinimum(Point point) {
        point.type = PointType.MIN;

        this.extremPoints.add(point);
    }

    private double calculateSlope(Point previous, Point current) {
        return (current.y - previous.y) / (current.x - previous.x);
    }

    public ArrayList<Point> MinimumPoints() {
        return ExtremPointsOfType(PointType.MIN);
    }

    public ArrayList<Point> MaximumPoints() {
        return ExtremPointsOfType(PointType.MAX);
    }

    public ArrayList<Point> ExtremPointsOfType(PointType type) {
        ArrayList<Point> result = new ArrayList<>();

        for (Point p : ExtremePoints()) {
            if(p.type == type) {
                result.add(p);
            }
        }

        return result;
    }

    public ArrayList<Point> ExtremePoints() {
        return this.extremPoints;
    }

    public ArrayList<Point> InflectionPoints() {
        return inflectionPoints;
    }

    public Point AbsoluteMin() {
        return absoluteMin;
    }

    public Point AbsoluteMax() {
        return absoluteMax;
    }

    public enum Slope {
        RISING, PLANE, FALLING
    }

    public enum Concavity {
        LEFT, RIGHT, NONE
    }

    public enum PointType {
        MIN, MAX, INFL
    }
}
