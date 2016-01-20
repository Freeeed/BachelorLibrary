package bachelor_library.Gestures;

import bachelor_library.Gestures.Data.Acceleration;
import bachelor_library.Gestures.Interfaces.GeneratorInterface;
import bachelor_library.Gestures.Interfaces.GeneratorListenerInterface;
import bachelor_library.Gestures.Interfaces.GestureDetectorListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Detects performed acceleration gestures and passes
 * sensor data to listeners for analyses
 */
public class Detector implements GeneratorListenerInterface {

    protected ArrayList<GestureDetectorListener> listeners = new ArrayList<>();

    protected GeneratorInterface generator;

    protected float range;
    protected int n;
    protected boolean useFilter = true;

    protected boolean inGesture = false;
    protected int inGestureTimer = 0;

    protected LinkedList<Acceleration> queue = new LinkedList<>();

    protected Gesture gesture = new Gesture();
    private Acceleration smoothedAcceleration = new Acceleration(0, 0, 0);

    private boolean log = false;

    /**
     * Constructor sets up generator
     *
     * @param theGenerator
     * @param range
     * @param n
     */
    public Detector(GeneratorInterface theGenerator, float range, int n) {
        generator = theGenerator;
        generator.addListener(this);

        this.range = range;
        this.n = n;
    }

    /**
     * adds listener which will be informed about detected gestures
     *
     * @param listener
     */
    public void addListener(GestureDetectorListener listener) {
        listeners.add(listener);
    }

    @Override
    /**
     * Sensordaten empfangen und verarbeiten
     *
     * @param acceleration
     */
    public void onDataGenerated(Acceleration originalAcceleration) {

        Acceleration currentAcceleration;

        if(useFilter) {
            float dT = (float) (originalAcceleration.time - smoothedAcceleration.time) / 1000; // ms in s umrechnen

            float RC = 0.01333f; // Konstante für RC

            float alpha = dT / (RC + dT);

            // Tiefpass Filter (infinite impules filter) (geglätteter Wert+= (neuer Wert * vorheriger geglätteter Wert) * Glättungskonstante)
            smoothedAcceleration = smoothedAcceleration.add(originalAcceleration.sub(smoothedAcceleration).mult(alpha));

            currentAcceleration = (Acceleration) smoothedAcceleration.clone();
        } else {
            currentAcceleration = originalAcceleration;
        }

        fireAccelerationChanged(currentAcceleration);

        // Aktuellen Beschleunigungsvektor in der Queue speichern
        queue.add(currentAcceleration);

        // Alte Vektoren aus der Queue entfernen
        while (queue.size() > n) {
            queue.poll();
        }

        detectGestureStart(currentAcceleration);

        detectGestureRemain(currentAcceleration);

        // Wenn ein Gestenanfang erkannt wurde, komplette Queue in die Geste übernehmen
        if (deviceInGesture()) {
            while (queue.size() > 0) {
                if(log) {
                    this.log(originalAcceleration);
                    this.log(currentAcceleration);
                }

                gesture.add(queue.poll());
            }
        }

        detectGestureEnd(currentAcceleration);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void log(Acceleration currentAcceleration) {
        log(currentAcceleration.toString());
    }

    /**
     * Public Getter, um von Außerhalb festzustellen, ob sich das Gerät innerhalb einer Geste befindet
     *
     * @return boolean
     */
    public boolean deviceInGesture() {
        return inGesture;
    }

    /**
     * Falls nicht in einer Geste:
     * Zählt die Anzahl der Beschleunigungsvektoren, dessen Norm größer als ein gegebener Grenzwert ist.
     * Ab einer gewissen Anzahl n gehen wir davon aus, dass wir in einer Geste sind
     * <p>
     * Falls in einer Geste:
     * Zählt die Anzahl der Beschleunigungsvektoren, dessen Norm kleiner als ein gegebener Grenzwert ist.
     * Fällt der Wert n zurück auf Null, gehen wir davon aus, dass das Gerät die Geste durchgeführt hat und wieder ruhig ist.
     *
     * @param currentAcceleration
     * @return
     */
    protected void detectGestureStart(Acceleration currentAcceleration) {
        // Wir befinden uns bereits in einer Geste
        if (inGesture) {
            return;
        }

        // Das Gerät befindet sich nicht in ausreichend Bewegung, um einen Gestenstart zu entdecken
        if (currentAcceleration.norm() < range) {
            return;
        }

        if (++inGestureTimer > n) {
            // first timestamp
            fireGestureStartDetected(queue.peek().time - 20);

            // Startpunkt in die Geste einfügen, wir gehen hier von einer Ruheposition aus
            gesture.add(new Acceleration(0, 0, queue.peek().time - 20));

            inGesture = true;
        }
    }

    /**
     * Falls wir uns in einer Geste befinden und zwischenzeitlich das Gerät seine Reichtung ändert (z.B. Schütteln),
     * dann muss beim erneuren Beschleunigen der Zähler wieder hochgezählt werden. Sonst wird über kurz oder lang das Gestenende "erkannt"
     *
     * @param currentAcceleration
     */
    protected void detectGestureRemain(Acceleration currentAcceleration) {
        if (!inGesture) {
            return;
        }

        // Das Gerät befindet sich nicht in Bewegung, abbrechen
        if (currentAcceleration.norm() < range) {
            return;
        }

        if (inGestureTimer < n) {
            inGestureTimer++;
        }
    }

    protected void detectGestureEnd(Acceleration currentAcceleration) {
        // Wir sind in keiner Geste, es kann kein Ende entdeckt werden
        if (!inGesture) {
            return;
        }

        // Das Gerät befindet sich noch in Bewegung
        if (currentAcceleration.norm() >= range) {
            return;
        }

        if (--inGestureTimer == 0) {
            // gesture end detected
            inGesture = false;

            gesture.add(new Acceleration(0, 0, gesture.getData().get(gesture.getData().size() - 1).time + 20));

            fireGestureEndDetected();
        }
    }

    /**
     * Feuert auf allen Listenern das Event onAccelerationChanged
     *
     * @param acceleration Ein Klon der aktuellen Gerätebeschleunigung
     */
    protected void fireAccelerationChanged(Acceleration acceleration) {
        // Jedem Listener einen Klon übergeben
        for (GestureDetectorListener listener : listeners) {
            listener.onAccelerationChanged((Acceleration) acceleration.clone());
        }
    }

    /**
     * Feuert auf allen Listenern das Event onGestureStartDetected
     * @param time
     */
    protected void fireGestureStartDetected(long time) {

        if(log) {
            this.log("start detected " + time);
        }

        // Jedem Listener die Startzeit übergeben
        for (GestureDetectorListener listener : listeners) {
            listener.onGestureStartDetected(time);
        }
    }

    /**
     * Feuert das Event onGestureDetected bei jedem Listener
     */
    protected void fireGestureEndDetected() {

        if(log) {
            this.log("end detected");
        }

        // Jedem Listner einen Klon übergeben
        for (GestureDetectorListener listener : listeners) {
            listener.onGestureEndDetected((Gesture) gesture.clone());
        }

        // Geste für nächste Entdeckung zurücksetzen
        gesture.reset();
    }

    /**
     * Sets the minimum range for gesture start detection
     * <p>
     * If the acceleration data is greater than given range for n times, gesture start is detected
     *
     * @param range
     */
    public void setRange(float range) {
        this.range = range;
    }

    public void activateLogging() {
        this.log = true;
    }

    public void deactivateLogging() {
        this.log = false;
    }
}
