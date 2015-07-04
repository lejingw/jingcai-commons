package com.jingcai.apps.common.jdbc.datasource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lejing on 15/7/3.
 */
public class JingcaiDriverManagerDataSource extends DriverManagerDataSource {

    private boolean defaultAutoCommit = true;
    private Boolean defaultReadOnly = null;

    public boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        setDefaultValue(conn);
        return conn;
    }

//    @Override
//    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
//        Connection conn = super.getConnectionFromDriverManager(url, props);
//        setDefaultValue(conn);
//        return conn;
//    }
//
//    @Override
//    public Connection getConnection(String username, String password) throws SQLException {
//        Connection conn = super.getConnection(username, password);
//        setDefaultValue(conn);
//        return conn;
//    }
//
//    @Override
//    protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
//        Connection conn = super.getConnectionFromDriver(username, password);
//        setDefaultValue(conn);
//        return conn;
//    }
//
//    @Override
//    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
//        Connection conn = super.getConnectionFromDriver(props);
//        setDefaultValue(conn);
//        return conn;
//    }

    private void setDefaultValue(Connection conn) throws SQLException {
        conn.setAutoCommit(defaultAutoCommit);

        if (getDefaultReadOnly() != null) {
            if (conn.isReadOnly() != getDefaultReadOnly()) {
                conn.setReadOnly(getDefaultReadOnly());
            }
        }
    }

}
