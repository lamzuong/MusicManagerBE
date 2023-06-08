package backend.musicmanager.tma.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ObjectFailed {
    private String message;
    private int status;
}
