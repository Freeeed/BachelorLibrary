package bachelor_library.Gestures;

import bachelor_library.Gestures.Data.Acceleration;

import java.util.ArrayList;

public class Gesture extends Object implements Cloneable {

    protected ArrayList<Acceleration> data = new ArrayList<>();

    public Gesture() {

    }

    public void add(Acceleration dataSet) {
        data.add(dataSet);
    }

    public void reset() {
        data.clear();
    }

    public ArrayList<Acceleration> getData() {
        return data;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Gesture " + data.toString();
    }
}
