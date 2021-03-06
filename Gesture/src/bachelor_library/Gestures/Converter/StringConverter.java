package bachelor_library.Gestures.Converter;

import bachelor_library.Gestures.Data.Acceleration;
import bachelor_library.Gestures.Gesture;

public class StringConverter {

    private static final String LIST_SEPARATOR = ";";
    private static final String DATA_SEPARATOR = ":";


    public String convertFromGesture(Gesture gesture) {
        String output = "";

        for (Acceleration acceleration : gesture.getData()) {
            output += convertFromAcceleration(acceleration) + LIST_SEPARATOR;
        }

        // letzten Separator entfernen
        return output.substring(0, output.length() - LIST_SEPARATOR.length());
    }

    public String convertFromAcceleration(Acceleration acceleration) {
        return acceleration.x + DATA_SEPARATOR + acceleration.y + DATA_SEPARATOR + acceleration.time;
    }

    public Gesture convertToGesture(String gestureString) throws Exception {

        String[] data = gestureString.split(LIST_SEPARATOR);

        Gesture gesture = new Gesture();

        for (String accelerationString : data) {
            gesture.add(convertToAcceleration(accelerationString));
        }

        return gesture;
    }

    public Acceleration convertToAcceleration(String acceleration) throws Exception {
        String[] data = acceleration.split(DATA_SEPARATOR);

        if (data.length != 3) {
            throw new Exception("Invalid number of data items!");
        }

        Float x = Float.parseFloat(data[0]);
        Float y = Float.parseFloat(data[1]);
        Long time = Long.parseLong(data[2]);

        return new Acceleration(x, y, time);
    }
}
