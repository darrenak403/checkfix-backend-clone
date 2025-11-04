package ttldd.instrumentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//hello
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;


    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200, null, "Success", data);
    }


    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(200, null, message, data);
    }


    public static <T> RestResponse<T> error(int status, String error, Object message) {
        return new RestResponse<>(status, error, message, null);
    }


    public static <T> RestResponse<T> error(int status, String message) {
        return new RestResponse<>(status, "Error", message, null);
    }
}
