package bachelor_library.Gestures.Data;

/**
 * 2-dim vector representing acceleration in horizontal directions
 */
public class Acceleration implements Cloneable {

    /**
     * Beschleunigung auf der x-Achse
     */
    public float x;

    /**
     * Beschleunigung auf der y-Achse
     */
    public float y;

    /**
     * Zeit in Millisekunden
     */
    public long time;

    /**
     * @param x Beschleunigung auf der x-Achse
     * @param y Beschleunigung auf der y-Achse
     * @param time in Millisekunden
     */
    public Acceleration(float x, float y, long time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    /**
     * calculate norm of acceleration vector
     *
     * @return
     */
    public float norm() {
        return (float) Math.sqrt(this.dot(this));
    }

    /**
     * Calculates dot produkt of two different acceleration vectors
     *
     * @param acceleration
     * @return
     */
    public float dot(Acceleration acceleration) {
        return x * acceleration.x + y * acceleration.y;
    }

    /**
     * calculates the sum of two accelerations
     *
     * @param acceleration
     * @return
     */
    public Acceleration add(Acceleration acceleration) {
        return new Acceleration(
                x + acceleration.x,
                y + acceleration.y,
                Math.max(time, acceleration.time)
        );
    }

    /**
     * calculates the differenz between two accelerations
     *
     * @param acceleration
     * @return
     */
    public Acceleration sub(Acceleration acceleration) {
        return new Acceleration(
                x - acceleration.x,
                y - acceleration.y,
                Math.max(time, acceleration.time)
        );
    }

    /**
     * multiply acceleration vector with constant
     *
     * @param constant
     * @return
     */
    public Acceleration mult(float constant) {
        return new Acceleration(constant * x, constant * y, time);
    }


    /**
     * divide acceleration vector with constant
     *
     * @param constant
     * @return
     */
    public Acceleration div(float constant) {
        return new Acceleration(x / constant, y / constant, time);
    }

    /**
     * Compares to acceleration vectors component wise
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj instanceof Acceleration) {
            Acceleration acceleration = (Acceleration) obj;

            return x == acceleration.x && y == acceleration.y;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "{" + time + ", " + (Math.round(x * 100) / 100f) + ", " + (Math.round(y * 100) / 100f) + "}";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
