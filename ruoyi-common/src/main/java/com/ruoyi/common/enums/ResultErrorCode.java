package com.ruoyi.common.enums;

/**
 * @Author hsj
 * @Date 2022/7/20 11:22
 * @Description 异常返回代码
 **/
public enum ResultErrorCode {

    OK("200", "处理成功"),
    BAD_REQUEST("400", "请求参数有误"),
    UNAUTHORIZED("401", "未授权"),
    //PARAMS_ROLEMISS("482", "请选择角色"),
    PARAMS_MISS("483", "缺少必填参数"),
    PARAM_ERROR("484", "参数非法"),
    FAILED_DEL_OWN("485", "不能删除自己"),
    FAILED_USER_ALREADY_EXIST("486", "该用户已存在"),
    NUN_USER("487", "没有找到该用户"),
    PASSWORD_ERROR("490", "该用户不存在"),
    NUN_FILE("488", "没有找到该文件"),
    NUN_UPLOAD("489", "上传文件失败，请重试"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    TOKEN_REQUIED("9999", "Token为空"),
    TOKEN_ERROR("9998", "Token验证不通过"),
    ACCESSTOKEN_ERROR("9997", "无法获取accessToken"),
    JSAPI_TOKEN_ERROR("9996", "JsApi鉴权失败"),
    NOT_IMPLEMENTED("501", "业务异常"),
    ID_REPEAT("502", "ID重复"),
    NON_COMMUNICATION("601", "通信异常,发送失败"),





            ;

    private String code;
    private String msg;

    ResultErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



}
