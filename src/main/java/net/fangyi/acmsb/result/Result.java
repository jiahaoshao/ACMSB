package net.fangyi.acmsb.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fangyi.acmsb.AcmsbApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> {
    private static final Logger logger = LoggerFactory.getLogger(Result.class);
    private Integer code;
    private String message;
    private T data;

    public static <E> Result<E> success(String message, E data) {
        logger.info("Result success with message: " + message + ", data: " + data.toString());
        return new Result<>(0, message, data);
    }

    public static Result success(String message) {
        logger.info("Result success with message: " + message);
        return new Result(0, message, null);
    }

    public static Result error(String message) {
        logger.info("Result error with message: " + message);
        return new Result(1, message, null);
    }

}
