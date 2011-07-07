
package imp.brickdictionary;

/**
 * purpose: Handle exceptions relating to blocks or the brick dictionary
 * @author Zachary Merritt
 */
public class DictionaryException extends Exception {
    Object reason;
    
    public DictionaryException() {}
    public DictionaryException(String message) {
        super(message);
    }
    public DictionaryException(String message, Object reason) {
        super(message);
        this.reason = reason;
    }
    
    public Object getReason() {
        return this.reason;
    }
}
