package com.gaosh96.flink.udf.utils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/25
 */
public class DroolsUtils {

    private final static String RULE_SQL = "select drl_content from rules where rule_name = '%s' and is_valid = 1";

    /**
     * 根据传入的 drl 字符串生成 kieBase
     *
     * @param drlContent
     * @return
     */
    public static KieBase getKieBaseFromDrlContent(String drlContent) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drlContent, ResourceType.DRL);
        return kieHelper.build();
    }

    /**
     * 从 mysql 查询最新的 drl 配置
     *
     * @return
     */
    public static String getValidDrlConf(String ruleName) {
        Connection connection = getConnection();
        String query = String.format(RULE_SQL, ruleName);

        QueryRunner queryRunner = new QueryRunner();
        try {
            return queryRunner.query(connection, query, new ScalarHandler<>());
        } catch (SQLException e) {
            DbUtils.closeQuietly(connection);
            e.printStackTrace();
            return null;
        }
    }

    private static Connection getConnection() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(DroolsUtils.class.getResourceAsStream("/jdbc.properties")));
            String url = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("load jdbc properties error");
        }
    }

}
