package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iVerb on 17-6-2015.
 */
public class Indexable {

    public final static int VECTOR_INDEX = 0;
    public final static int PICKING_INDEX = 1;

    Map<Integer, Integer> indexes = new HashMap<Integer, Integer>();


    public void setIndex(int indexIdentifier, int index) {
        indexes.put(indexIdentifier, index);
    }

    public int getIndex(int indexIdentifier) {
        return indexes.get(indexIdentifier);
    }

}
