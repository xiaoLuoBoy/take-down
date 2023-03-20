package com.horqian.api.entity.im;

import lombok.Data;

@Data
public class InfoDto {

    /**
     * 用户userId
     */
    private String To_Account;


    /**
     * 用户上线或者下线的动作，Login 表示上线（TCP 建立），Logout 表示下线（TCP 断开），Disconnect 表示网络断开（TCP 断开）
     */
    private String Action;


    /**
     *用户上下线触发的原因：
     * Login 的原因有 Register：App TCP 连接建立或断网重连
     * Logout 的原因有 Unregister：App 用户注销帐号导致 TCP 断开；OpenKickInstance：App 管理员调用 失效帐号登录状态 接口将用户踢下线
     * Disconnect 的原因有 LinkClose：即时通信 IM 检测到 App TCP 连接断开（例如 kill App，客户端发出 TCP 的 FIN 包或 RST 包）；TimeOut：即时通信 IM 检测到 App 心跳包超时，认为 TCP 已断开（例如客户端网络异常断开，未发出 TCP 的 FIN 包或 RST 包，也无法发送心跳包）。心跳超时时间为400秒
     * 各种具体场景触发的回调 Reason 请参考 可能触发该回调的场景
     */
    private String Reason;
}
