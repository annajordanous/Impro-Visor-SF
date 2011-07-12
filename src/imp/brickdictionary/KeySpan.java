
package imp.brickdictionary;

/**
 * purpose: Object for key/mode pairs with durations -- mainly for drawing
 * @author Zachary Merritt
 */
public class KeySpan {
    private KeyMode keymode = new KeyMode();
    private long duration = 0;
    
    public KeySpan(KeyMode km, long d) {
        keymode = km;
        duration = d;
    }
    
    public KeySpan(long k, String m, long d) {
        KeyMode km = new KeyMode(k, m);
        keymode = km;
        duration = d;
    }
    
    public KeySpan() {}
    
    public long getKey() {
        return keymode.getKey();
    }
    
    public void setKey(long k) {
        keymode.setKey(k);
    }
    
    public String getMode() {
        return keymode.getMode();
    }
    
    public void setMode(String m) {
        keymode.setMode(m);
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long d) {
        duration = d;
    }
}
