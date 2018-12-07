package com.lb.baseui.utils.net;

/**
 * 网络请求的回调接口
 *
 * @since Created by JoeSuperM on 2017/3/7 17:16.
 */
public interface ICallBack<T> {
    /**
     * 网络请求成功回调
     *
     * @param t 请求处理后的结果
     */
    void onSuccess(T t);

    /**
     * 网络请求的失败回调
     *
     * @param e 异常信息
     */
    void onError(Throwable e);

    /**
     * 获取当前请求的结果类型，涉及将请求结果转换的类型
     *
     * @return 返回ResultType.xxx的枚举类型
     */
    ResultType getResultType();

    /**
     * 网络请求结束，运行在主线程，不管成功还是失败都会调用，
     * 在onSuccess和onError之后调用
     */
    void onFinish();

    /**
     * 网络请求返回的类型，由getResultType()方法告知底层应当将数据转换成何种数据，<br/>
     * 该类型与T形成对应关系。
     */
    enum ResultType {
        /**
         * 文本字符串,T : String
         */
        TEXT_STR,
        /**
         * 字节数组,T : byte[]
         */
        BYTE_ARR,
        /**
         * 字节流, T : InputStream
         */
        BYTE_STREAM,
        /**
         * 字符流, T : Reader
         */
        CHAR_STREAM,
        /**
         * 原始的反馈
         */
        ORI
    }
}
