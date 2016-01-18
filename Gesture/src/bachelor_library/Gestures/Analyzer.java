package bachelor_library.Gestures;

import bachelor_library.Gestures.Data.Acceleration;
import bachelor_library.Gestures.Data.Point;
import bachelor_library.Gestures.Exceptions.UnknownGesture;
import bachelor_library.Gestures.Graph.CurveSketching;

import java.util.ArrayList;

/**
 * Analyzes gestures
 */
public class Analyzer {

    public Analyzer() {

    }

    /**
     * analyzes a gesture and returns the name of the gesture
     * of gesture is unknown an exception is thrown
     *
     * @param gesture
     * @return name of gesture
     * @throws UnknownGesture
     */
    public String analyze(Gesture gesture) throws UnknownGesture {
        // find acceleration peeks...

        ArrayList<Point> pointListX = new ArrayList<>();
        ArrayList<Point> pointListY = new ArrayList<>();

        long startTime = gesture.getData().get(0).time;

        Acceleration a;
        long t;
        for (int i = 0; i < gesture.getData().size(); i++) {
            a = gesture.getData().get(i);

            pointListX.add(new Point(a.time - startTime, a.x));
            pointListY.add(new Point(a.time - startTime, a.y));
        }

        CurveSketching sketcherX = new CurveSketching(pointListX);
        sketcherX.Compute();
        sketcherX.Flatten();

        CurveSketching sketcherY = new CurveSketching(pointListY);
        sketcherY.Compute();
        sketcherY.Flatten();

        if (axisIsFlat(sketcherX) &&
                axisHasExtremePoints(sketcherY) &&
                secondIsLarger(sketcherY) &&
                thirdIsLowerAndFurtherAreNoise(sketcherY)) {

            // upwards & downwards
            return startIsMaximum(sketcherY) ? "upwards" : "downwards";
        } else if (axisIsFlat(sketcherY) &&
                axisHasExtremePoints(sketcherX) &&
                secondIsLarger(sketcherX) &&
                thirdIsLowerAndFurtherAreNoise(sketcherX)) {

            // left & right
            return startIsMaximum(sketcherX) ? "right" : "left";
        }

        throw new UnknownGesture();
    }

    /**
     * Überprüft, ob dr Graph einen Grenzbereich nicht verlässt.
     *
     * @param sketcher
     * @return
     */
    private boolean axisIsFlat(CurveSketching sketcher) {
        return sketcher.AbsoluteMin().y > -5 && sketcher.AbsoluteMax().y < 5;
    }

    /**
     * Es muss mindestens drei Extrempunkte geben
     * Für die erste Beschleunigung, für das Abbremsen und die Richtungsumkehrung
     * und zum Schluß für das erneute Abbremsen im Startpunkt.
     *
     * @param sketcher
     * @return
     */
    private boolean axisHasExtremePoints(CurveSketching sketcher) {
        return sketcher.ExtremePoints().size() >= 3;
    }

    /**
     * Prüft, ob der zweite Extrempunkt vom Absolutwert größer ist, als der Erste.
     * Da das Gerät Zuerst abgebremst und dann entgegengesetzt beschleunigt werden muss, ist dies der Fall.
     *
     * @param sketcher
     * @return
     */
    private boolean secondIsLarger(CurveSketching sketcher) {
        return Math.abs(sketcher.ExtremePoints().get(0).y) < Math.abs(sketcher.ExtremePoints().get(1).y);
    }

    /**
     * Überprüft, ob der dritte Extrempunkt kleiner ist, als der zweite, da das Gerät nur abgebremst wird.
     * Alle Restlichen Extrempunkte werden durch Nachschwankungen verursacht und dürfen nur kleine Beträge haben
     *
     * @param sketcher
     * @return
     */
    private boolean thirdIsLowerAndFurtherAreNoise(CurveSketching sketcher) {

        if (Math.abs(sketcher.ExtremePoints().get(1).y) < Math.abs(sketcher.ExtremePoints().get(2).y)) {
            return false;
        }

        // noise
        for (int i = 3; i < sketcher.ExtremePoints().size(); i++) {
            if (Math.abs(sketcher.ExtremePoints().get(i).y) > 3.5) {
                return false;
            }
        }

        return true;
    }

    /**
     * Prüft, ob der erste Extrempunkt ein Maximum ist.
     * Die wird benötigt, um die Initialrichtung des Gerätes zu ermitteln
     *
     * @param sketcher
     * @return
     */
    private boolean startIsMaximum(CurveSketching sketcher) {
        return sketcher.ExtremePoints().get(0).type == CurveSketching.PointType.MAX;
    }
}
