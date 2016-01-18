package bachelor_library.Gestures.Interfaces;

import bachelor_library.Gestures.Data.Acceleration;
import bachelor_library.Gestures.Gesture;

public interface GestureDetectorListener
{
    void onAccelerationChanged(Acceleration acceleration);
    void onGestureStartDetected(long time);
    void onGestureEndDetected(Gesture gesture);
}