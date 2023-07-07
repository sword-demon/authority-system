package top.wjstar.utils;

import lombok.Data;

@Data
public class Result<T> {
    // 是否成功
    private Boolean success;
    // 状态码
    private Integer code;
    // 返回消息
    private String message;
    // 返回数据
    private T data;

    /**
     * 私有化构造方法
     */
    private Result() {
    }

    /**
     * 操作成功，不返回数据
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 操作失败
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR);
        result.setMessage("操作失败");
        return result;
    }

    /**
     * 操作成功，返回数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setData(data);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 设置是否成功
     *
     * @param success
     * @return
     */
    public Result<T> success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    /**
     * 设置返回状态码
     *
     * @param code
     * @return
     */
    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 设置返回消息
     *
     * @param message
     * @return
     */
    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 是否存在
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> exist() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("操作成功");
        return result;
    }
}
