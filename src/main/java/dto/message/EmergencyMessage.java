package dto.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmergencyMessage {
    private String message;
    public EmergencyMessage(String message) {
        this.message = message;
    }
}
