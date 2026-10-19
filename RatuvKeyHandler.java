public interface RatuvKeyHandler{
    int compare(Object o1, Object o2);
    long getLoad(Object o);
    Object getMaxKey();
    Object getMinKey();
}