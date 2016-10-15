package util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by iVerb on 26-5-2015.
 */
public class IndexedMap<T extends Indexable> extends HashMap<Integer, T> implements Iterable<T> {

    private int maxIndex = 0;
    private int targetIndex;

    public IndexedMap(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public void add(T t) {
        put(maxIndex, t);
        t.setIndex(targetIndex, maxIndex++);
    }

    public void addAll(T... array) {
        for (T t : array) {
            add(t);
        }
    }

    public void clear() {
        super.clear();
        maxIndex = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }
}
