package com.lb.baseui.utils.net;

import com.lb.baseui.utils.StringUtils;
import okhttp3.*;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import rx.*;
import rx.schedulers.Schedulers;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author LiuBo
 * @date 2018-10-23
 */
public class CommHttpUtil {
    private static CommHttpUtil sInstance;

    public static CommHttpUtil getInstance() {
        if (sInstance == null) {
            synchronized (CommHttpUtil.class) {
                if (sInstance == null) {
                    sInstance = new CommHttpUtil();
                }
            }
        }
        return sInstance;
    }

    //默认的连接超时时间，60秒钟
    private static final long DEF_CONNECT_TIME = 60;
    //默认的读取超时时间，60秒钟
    private static final long DEF_READ_TIME = 60;
    //默认的写入超时时间，60秒钟
    private static final long DEF_WRITE_TIME = 60;
    //默认的POST读取数据长度，10K
    private static final int DEF_POST_READ_SIZE = 10 * 1024;
    //默认的网络请求客户端
    private OkHttpClient mOkHttpClient;

    private Scheduler mWorkerScheduler;
    private Scheduler mCallBackScheduler;

    private CommHttpUtil() {
        mWorkerScheduler = Schedulers.io();
        mCallBackScheduler = Schedulers.computation();
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEF_CONNECT_TIME, TimeUnit.SECONDS)
                .readTimeout(DEF_READ_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEF_WRITE_TIME, TimeUnit.SECONDS)
                //添加信任所有证书
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                //信任规则全部信任
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }

    public OkHttpClient getHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 设置回调处理的线程
     */
    public void setWorkScheduler(Scheduler scheduler) {
        mWorkerScheduler = scheduler;
    }

    /**
     * 设置回调处理的线程
     */
    public void setCallBackScheduler(Scheduler scheduler) {
        mCallBackScheduler = scheduler;
    }

    /**
     * 信任所有https
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }

    /**
     * 通用的请求某个网络地址，包括Http和Https等
     *
     * @param url      网络地址，Http或Https
     * @param callBack 网络请求回调
     * @param params   get的参数列表
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean get(String url, String tag, Map<String, String> params, ICallBack<T> callBack) {
        StringBuilder sBuilder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            sBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sBuilder.setLength(sBuilder.length() - 1);
        }
        return get(sBuilder.toString(), tag, callBack);
    }

    /** @see #get(String, String, Map, ICallBack) */
    public <T> boolean get(String url, Map<String, String> params, ICallBack<T> callBack) {
        return get(url, url, params, callBack);
    }


    /** @see #get(String, String, ICallBack) */
    public <T> boolean get(String url, ICallBack<T> callBack) {
        return get(url, url, callBack);
    }

    /**
     * 通用的请求某个网络地址，包括Http和Https等
     *
     * @param url      网络地址，Http或Https
     * @param callBack 网络请求回调
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean get(final String url, final String tag, final ICallBack<T> callBack) {
        if (callBack == null || StringUtils.isEmpty(url) || StringUtils.isEmpty(tag)) {
            return false;
        }
        Observable.unsafeCreate(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    Request request = new Request.Builder().get().url(url).build();
                    Call call = mOkHttpClient.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        parseResponseAndInvoke(callBack, response.body(), subscriber);
                    } else {
                        subscriber.onError(new Throwable("Get Request Failed : " + response.code()));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(mWorkerScheduler)
                .observeOn(mCallBackScheduler)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                        //移除缓存请求
                        callBack.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                        onCompleted();
                    }

                    @Override
                    public void onNext(T t) {
                        callBack.onSuccess(t);
                    }
                });
        return true;
    }

    /**
     * 发送json数据到服务器
     *
     * @param url      网络地址，Http或Https
     * @param json     Post的json数据
     * @param callBack 网络请求回调
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(String url, String tag, String json, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(json) || callBack == null) {
            return false;
        }
        MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonMediaType, json);
        return post(url, tag, requestBody, callBack);
    }

    /** @see #post(String, String, String, ICallBack) */
    public <T> boolean post(String url, String json, ICallBack<T> callBack) {
        return post(url, url, json, callBack);
    }

    /**
     * 发送POST请求，携带文件和key-value的键值对
     *
     * @param url      网络地址，Http或Https
     * @param params   需要携带的参数
     * @param callBack 网络请求回调
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(String url, String tag, TreeMap<String, Object> params, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || params == null || params.isEmpty() || callBack == null) {
            return false;
        }
        MultipartBody.Builder MbBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object object = entry.getValue();
            if (object instanceof File) {
                File file = (File) object;
                MbBuilder.addFormDataPart(entry.getKey(), file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file));
            } else {
                MbBuilder.addFormDataPart(entry.getKey(), String.valueOf(object));
            }
        }
        return post(url, tag, MbBuilder.build(), callBack);
    }

    /** @see #post(String, String, TreeMap, ICallBack) */
    public <T> boolean post(String url, TreeMap<String, Object> params, ICallBack<T> callBack) {
        return post(url, url, params, callBack);
    }


    /**
     * 发送通用的POST请求，传入Post的Body
     *
     * @param url         网络地址，Http或Https
     * @param requestBody Post的Body数据
     * @param callBack    网络请求回调
     * @param <T>         相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(final String url, final String tag, final RequestBody requestBody, final ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || requestBody == null || callBack == null || StringUtils.isEmpty(tag)) {
            return false;
        }
        Observable.unsafeCreate(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Call call = mOkHttpClient.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        parseResponseAndInvoke(callBack, response.body(), subscriber);
                    } else {
                        response.body().close();
                        subscriber.onError(new Throwable("Post Request Failed : " + response.code()));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(mWorkerScheduler)
                .observeOn(mCallBackScheduler)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                        callBack.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                        onCompleted();
                    }

                    @Override
                    public void onNext(T t) {
                        callBack.onSuccess(t);
                    }
                });
        return true;
    }

    /** @see #post(String, String, RequestBody, ICallBack) */
    public <T> boolean post(String url, RequestBody requestBody, ICallBack<T> callBack) {
        return post(url, url, requestBody, callBack);
    }


    /**
     * 使用Post上传一个文件，类似于表单提交文件<br/>
     * "<"input type="file" name="file"/>"
     *
     * @param url        网络地址，Http或Https
     * @param uploadFile 指定上传的文件
     * @param callBack   网络请求回调
     * @param <T>        相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */

    public <T> boolean post(String url, String tag, File uploadFile, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || callBack == null ||
                uploadFile == null || !uploadFile.exists()
                || StringUtils.isEmpty(tag)) {
            return false;
        }
        HashMap<String, File> uploadFileMap = new HashMap<>(1);
        uploadFileMap.put("file", uploadFile);
        return post(url, tag, uploadFileMap, callBack);
    }

    /** @see #post(String, String, File, ICallBack) */
    public <T> boolean post(String url, File uploadFile, ICallBack<T> callBack) {
        return post(url, url, uploadFile, callBack);
    }

    /**
     * 上传多个文件，由服务器指定的每个文件对应的表单名称<br/>
     * 类似于多个"<"input type="file" name="file0"/>"<br/>
     * "<"input type="file" name="file1"/>"<br/>
     * "<"input type="file" name="file2"/>"等<br/>
     *
     * @param url           网络地址，Http或Https
     * @param uploadFileMap 上传多个文件，以及每个文件对应的表单名称
     * @param callBack      网络请求回调
     * @param <T>           相应数据转换类型，参见ICallBack.ResultType
     * @return
     */
    public <T> boolean post(String url, String tag, HashMap<String, File> uploadFileMap, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || uploadFileMap == null || uploadFileMap.isEmpty() ||
                callBack == null || StringUtils.isEmpty(tag)) {
            return false;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, File> uploadFile : uploadFileMap.entrySet()) {
            File file = uploadFile.getValue();
            if (file != null && file.exists()) {
                builder.addFormDataPart(uploadFile.getKey(), file.getName(),
                        createProgressRequestBody(file));
            }
        }
        return post(url, tag, builder.build(), callBack);
    }

    /** @see #post(String, String, HashMap, ICallBack) */
    public <T> boolean post(String url, HashMap<String, File> uploadFileMap, ICallBack<T> callBack) {
        return post(url, url, uploadFileMap, callBack);
    }

    /**
     * 上传多个文件，使用默认的表单名称file0，file1,file2....等
     *
     * @param url        网络地址，Http或Https
     * @param uploadFile 上传多个文件列表
     * @param callBack   网络请求回调
     * @param <T>        相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(String url, String tag, List<File> uploadFile, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || callBack == null ||
                uploadFile == null || uploadFile.isEmpty()
                || StringUtils.isEmpty(tag)) {
            return false;
        }
        //将List转为默认的Map方式
        HashMap<String, File> uploadFileMap = new HashMap<>(uploadFile.size());
        int index = 0;
        for (File file : uploadFile) {
            uploadFileMap.put("file" + (index++), file);
        }
        return post(url, tag, uploadFileMap, callBack);
    }

    /** @see #post(String, String, List, ICallBack) */
    public <T> boolean post(String url, List<File> uploadFile, ICallBack<T> callBack) {
        return post(url, url, uploadFile, callBack);
    }

    /**
     * 发送携带key-value参数和字节数组的参数
     *
     * @param url      网络地址，Http或Https
     * @param params   携带的key-value参数
     * @param buffer   需要上传的字节数组
     * @param callBack 网络请求回调
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(String url, String tag, Map<String, String> params, byte[] buffer, ICallBack<T> callBack) {
        if (StringUtils.isEmpty(url) || callBack == null || StringUtils.isEmpty(tag)) {
            return false;
        }
        //两个参数都为空，
        if ((params == null || params.isEmpty())
                && (buffer == null || buffer.length == 0)) {
            return false;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> keyValue : params.entrySet()) {
            builder.addFormDataPart(keyValue.getKey(), keyValue.getValue());
        }
        RequestBody requestBody = createByteArrayAndProgressRequestBody(MediaType.parse("application/octet-stream"), buffer);
        builder.addPart(requestBody);
        return post(url, tag, builder.build(), callBack);
    }

    /** @see #post(String, String, Map, byte[], ICallBack) */
    public <T> boolean post(String url, Map<String, String> params, byte[] buffer, ICallBack<T> callBack) {
        return post(url, url, params, buffer, callBack);
    }

    /**
     * Post的方式请求数据，并携带相应的参数
     *
     * @param url      网络地址，Http或Https
     * @param params   Post的键值对
     * @param callBack 网络请求回调
     * @param <T>      相应数据转换类型，参见ICallBack.ResultType
     * @return 是否发起网络请求
     */
    public <T> boolean post(final String url, final String tag, final Map<String, String> params, final ICallBack<T> callBack) {
        if (callBack == null || StringUtils.isEmpty(url) ||
                params == null || params.isEmpty() || StringUtils.isEmpty(tag)) {
            return false;
        }

        Subscription subscription = Observable.unsafeCreate(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    FormBody.Builder builder = new FormBody.Builder();
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.add(param.getKey(), param.getValue());
                    }
                    RequestBody requestBody = builder.build();

                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Call call = mOkHttpClient.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        parseResponseAndInvoke(callBack, response.body(), subscriber);
                    } else {
                        subscriber.onError(new Throwable("Post Request Failed : " + response.code()));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(mWorkerScheduler)
                .observeOn(mCallBackScheduler)
                .subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                        callBack.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        callBack.onSuccess(t);
                    }
                });
        return true;
    }

    /** @see #post(String, String, Map, ICallBack) */
    public <T> boolean post(String url, Map<String, String> params, ICallBack<T> callBack) {
        return post(url, url, params, callBack);
    }

    //解析网络响应报文中的数据，生成对应的回调数据
    private <T> void parseResponseAndInvoke(ICallBack<T> callBack, ResponseBody responseBody, Subscriber<? super T> subscriber) throws IOException {
        ICallBack.ResultType resultType = callBack.getResultType();
        switch (resultType) {
            case TEXT_STR:
                subscriber.onNext((T) responseBody.string());
                break;
            case BYTE_ARR:
                subscriber.onNext((T) responseBody.bytes());
                break;
            case BYTE_STREAM:
                subscriber.onNext((T) responseBody.byteStream());
                break;
            case CHAR_STREAM:
                subscriber.onNext((T) responseBody.charStream());
                break;
            case ORI:
                subscriber.onNext((T)responseBody);
                break;
            default:
                subscriber.onNext((T) responseBody.string());
                break;
        }
    }

    //发送指定的直接数组
    private <T> RequestBody createByteArrayAndProgressRequestBody(final MediaType mediaType, final byte[] buffer) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() throws IOException {
                return buffer.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                ByteArrayInputStream byteArrayInputStream = null;
                try {
                    byteArrayInputStream = new ByteArrayInputStream(buffer);
                    int readCount = 0;
                    byte[] buffer = new byte[DEF_POST_READ_SIZE];
                    while ((readCount = byteArrayInputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, readCount);
                    }
                } finally {
                    if (byteArrayInputStream != null) {
                        byteArrayInputStream.close();
                    }
                }
            }
        };
    }

    /**
     * 创建文件上传的请求体
     * @param file 文件
     * @return RequestBody
     */
    public static RequestBody createProgressRequestBody(final File file) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream");
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(file);
                    Buffer buffer = new Buffer();
                    for (long readCount; (readCount = source.read(buffer, DEF_POST_READ_SIZE)) != -1; ) {
                        sink.write(buffer, readCount);
                    }
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
}
