package rendering;


/**
 * Created by iVerb on 16-6-2015.
 */
public abstract class Renderable {

    public boolean doDraw = true;

    public void render() {
        if (doDraw) {
            draw();
        }
    }

    protected abstract void draw();

}