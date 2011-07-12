
package imp.brickdictionary;

/**
 * purpose: Object for key/mode pairs corresponding to blocks
 * @author Zachary Merritt
 */
public class KeyMode {
    
    private long key = -1;
    private String mode = "";
    
    public KeyMode(long k, String m) {
        key = k;
        mode = m;
    }
    
    public KeyMode() {}
    
    public long getKey() {
        return key;
    }
    
    public void setKey(long k) {
        key = k;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String m) {
        mode = m;
    }
}
