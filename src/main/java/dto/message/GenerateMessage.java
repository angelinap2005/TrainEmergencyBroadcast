package dto.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerateMessage {
    private String message;
    public GenerateMessage(String message) {
        this.message = message;
    }
}
