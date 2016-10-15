package picking;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jeroen van Wijgerden on 22-5-2015.
 */
public class PickHandler {

    public static Set<PickListener> listeners = new HashSet<PickListener>();
    private static int pickedId = -1;
    private static boolean isPicking = false;

    public static void startPicking(int pickedId, int mouseButton) {
        PickHandler.pickedId = pickedId;
        PickHandler.isPicking = true;

        for (PickListener listener : listeners) {
            listener.startPicking(PickHandler.pickedId, mouseButton);
        }
    }

    public static void stopPicking(int mouseButton) {
        PickHandler.isPicking = false;

        for (PickListener listener : listeners) {
            listener.stopPicking(mouseButton);
        }
    }

    public static int getPickedId() {
        return PickHandler.pickedId;
    }

    public static boolean isPicking() {
        return PickHandler.isPicking;
    }

}
