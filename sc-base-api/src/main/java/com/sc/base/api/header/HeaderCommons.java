package com.sc.base.api.header;

/**
 * hessian审计头部信息
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class HeaderCommons {

    /**
     * id信息
     */
    public static final String id = "id";//

    /**
     * 用户id
     */
    public static final String userId = "userId";//

    /**
     * 用户的标记，登陆成功后返回，算法需要讨论
     */
    public static final String userToken = "userToken";//
    /**
     * 应用编号
     */
    public static final String applicationCode = "applicationCode";
    /**
     * 设备编号，唯一区别每一个设备，客户端产生
     */
    public static final String clientId = "clientId";

    /**
     * 下载渠道号
     */
    public static final String sourceId = "sourceId";

    /**
     * 客户端版本号
     */
    public static final String version = "version";

    /**
     * 服务版本
     */
    public static final String serviceVersion = "serviceVersion";

    /**
     * 主渠道号
     */
    public static final String channel = "channel";

    /**
     * 子渠道号
     */
    public static final String subChannel = "subChannel";

    /**
     * Header中审计名称
     */
    public static final String AUDIT = "hessian_audit";

    /**
     * 编码方式
     */
    public static final String UTF8 = "UTF-8";

}
