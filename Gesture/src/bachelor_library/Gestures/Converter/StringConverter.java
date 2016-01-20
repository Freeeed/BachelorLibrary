package bachelor_library.Gestures.Converter;

import bachelor_library.Gestures.Data.Acceleration;
import bachelor_library.Gestures.Gesture;

public class StringConverter {

    private static final String LIST_SEPERATOR = ";";
    private static final String DATA_SEPERATOR = "-";


    public String convertFromGesture(Gesture gesture) {
        String output = "";

        for (Acceleration acceleration : gesture.getData()) {
            output+= convertFromAcceleration(acceleration) + LIST_SEPERATOR;
        }

        // letzten Separator entfernen
        return output.substring(0, output.length() - (1 + LIST_SEPERATOR.length()));
    }

    public String convertFromAcceleration(Acceleration acceleration) {
        return acceleration.x + DATA_SEPERATOR + acceleration.y + DATA_SEPERATOR + acceleration.time;
    }

    public Gesture convertToGesture(String gestureString) {

        String[] data = gestureString.split(LIST_SEPERATOR);

        Gesture gesture = new Gesture();

        for(String accelerationString : data) {
            gesture.add(convertToAcceleration(accelerationString));
        }

        return gesture;
    }

    public Acceleration convertToAcceleration(String acceleration) throws IllegalArgumentException {
        String[] data = acceleration.split(DATA_SEPERATOR);

        if(data.length != 3) {
            throw new IllegalArgumentException("Invalid number of data items!");
        }

        Float x = Float.parseFloat(data[0]);
        Float y = Float.parseFloat(data[1]);
        Long time = Long.parseLong(data[2]);

        return new Acceleration(x, y, time);
    }
}
