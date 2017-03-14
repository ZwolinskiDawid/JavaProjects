package app;

import java.util.Comparator;

class CountComparator implements Comparator<Object[]> {

    @Override
    public int compare(Object[] o1, Object[] o2) {
        return ((Number) o1[1]).intValue() - ((Number) o2[1]).intValue();
    }
    
}
