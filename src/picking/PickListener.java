package picking;

/**
 * Created by Jeroen van Wijgerden on 22-5-2015.
 */
public interface PickListener {

    public void startPicking(int id, int mouseButton);

    public void stopPicking(int mouseButton);

}
