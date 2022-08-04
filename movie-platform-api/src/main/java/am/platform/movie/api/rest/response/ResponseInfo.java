package am.platform.movie.api.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author mher13.02.94@gmail.com
 */

@AllArgsConstructor
@Data
public class ResponseInfo {

    public ResponseMessage status;


    public static ResponseInfo createResponse(ResponseMessage status) {
        return new ResponseInfo(status);
    }
}
