package com.redisfront.service;

import com.redisfront.constant.Enum;
import com.redisfront.model.ConnectInfo;
import com.redisfront.service.impl.ConnectServiceImpl;
import com.redisfront.util.FunUtil;

import java.util.List;
import java.util.Map;

/**
 * ConnectService
 *
 * @author Jin
 */
public interface ConnectService {

    ConnectService service = new ConnectServiceImpl();

    List<ConnectInfo> getConnectListByName(String name);

    List<ConnectInfo> getAllConnectList();

    ConnectInfo getConnect(Object id);

    void save(ConnectInfo connectInfo);

    void update(ConnectInfo connectInfo);

    void delete(Object id);

    void initDatabase();

    default String buildUpdateSql(ConnectInfo connectInfo) {
        return "update rf_connect" +
                " set " +
                "title ='" +
                connectInfo.title() +
                "'," +
                "host ='" +
                connectInfo.host() +
                "'," +
                "port =" +
                connectInfo.port() +
                "," +
                "username ='" +
                connectInfo.user() +
                "'," +
                "password ='" +
                connectInfo.password() +
                "'," +
                "ssl ='" +
                connectInfo.ssl() +
                "'," +
                "connect_mode ='" +
                connectInfo.connectMode().name() +
                "'," +
                "ssl_config ='" +
                (FunUtil.isNull(connectInfo.sshConfig()) ? "" : connectInfo.sshConfig()) +
                "'," +
                "ssh_config ='" +
                (FunUtil.isNull(connectInfo.sshConfig()) ? "" : connectInfo.sshConfig()) +
                "' where id =" +
                connectInfo.id();

    }

    default String buildInsertSql(ConnectInfo connectInfo) {
        return "insert into rf_connect" +
                "(" +
                "title, " +
                "host, " +
                "port, " +
                (FunUtil.isNotEmpty(connectInfo.user()) ? "username, " : "") +
                (FunUtil.isNotEmpty(connectInfo.password()) ? "password, " : "") +
                "ssl, " +
                "connect_mode, " +
                "ssl_config, " +
                "ssh_config" +
                ") " +
                "values('" +
                connectInfo.title() +
                "','" +
                connectInfo.host() +
                "'," +
                connectInfo.port() +
                ",'" +
                (FunUtil.isNotEmpty(connectInfo.user()) ? connectInfo.user() + "','" : "") +
                (FunUtil.isNotEmpty(connectInfo.password()) ? connectInfo.password() + "','" : "") +
                connectInfo.ssl() +
                "','" +
                connectInfo.connectMode().name() +
                "','" +
                (FunUtil.isNull(connectInfo.sshConfig()) ? "" : connectInfo.sshConfig()) +
                "','" +
                (FunUtil.isNull(connectInfo.sshConfig()) ? "" : connectInfo.sshConfig()) +
                "')";
    }

    default ConnectInfo mapToConnectInfo(Map<String, Object> map) {
        ConnectInfo.SSLConfig sslConfig = FunUtil.isNull(map.get("ssl_config")) ? null : FunUtil.fromJson((String) map.get("ssl_config"), ConnectInfo.SSLConfig.class);
        ConnectInfo.SSHConfig sshConfig = FunUtil.isNull(map.get("ssh_config")) ? null : FunUtil.fromJson((String) map.get("ssh_config"), ConnectInfo.SSHConfig.class);
        return new ConnectInfo()
                .setId((Integer) map.get("id"))
                .setTitle((String) map.get("title"))
                .setHost((String) map.get("host"))
                .setPort((Integer) map.get("port"))
                .setUsername((String) map.get("username"))
                .setPassword((String) map.get("password"))
                .setDatabase((Integer) map.get("database"))
                .setSsl(Boolean.valueOf((String) map.get("ssl")))
                .setConnectMode(Enum.Connect.valueOf((String) map.get("connect_mode")))
                .setSshConfig(sshConfig)
                .setSslConfig(sslConfig)
                ;
    }

}
