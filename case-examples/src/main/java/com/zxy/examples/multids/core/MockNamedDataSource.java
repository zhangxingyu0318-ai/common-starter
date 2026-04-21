package com.zxy.examples.multids.core;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * 仅用于示例的占位 DataSource，不依赖真实 JDBC 驱动。
 */
public class MockNamedDataSource implements DataSource {

    private final String name;

    public MockNamedDataSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new SQLException("Demo datasource does not open real connection: " + name);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("Demo datasource does not open real connection: " + name);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Unsupported unwrap");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        // no-op
    }

    @Override
    public void setLoginTimeout(int seconds) {
        // no-op
    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger("MockNamedDataSource");
    }
}

