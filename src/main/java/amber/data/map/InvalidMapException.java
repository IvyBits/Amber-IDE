package amber.data.map;

import java.io.IOException;

/**
 *
 * @author Tudor
 */
public class InvalidMapException extends IOException {
    
    public InvalidMapException(String error) {
        super(error);
    }
}
