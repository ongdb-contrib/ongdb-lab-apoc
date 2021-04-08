package data.lab.ongdb.load;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.OlabConfiguration;
import data.lab.ongdb.load.util.LoadJdbcConfig;
import data.lab.ongdb.result.RowResult;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static data.lab.ongdb.load.util.JdbcUtil.getConnection;
import static data.lab.ongdb.load.util.JdbcUtil.getUrlOrKey;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.load
 * @Description: TODO
 * @date 2021/4/8 8:42
 */
public class Jdbc {

    static {
        OlabConfiguration.get("jdbc").forEach((k, v) -> {
            if (k.endsWith("driver")) {
                loadDriver(v.toString());
            }
        });
    }

    @Context
    public Log log;

    private static void loadDriver(@Name("driverClass") String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load driver class " + driverClass + " " + e.getMessage());
        }
    }

    /**
     * @param urlOrKey:数据库连接
     * @param query:查询语句
     * @param params:参数
     * @param config:配置
     * @return
     * @Description: TODO(支持执行复杂的SQL存储过程-查询同时支持更新)
     */
    @Procedure(name = "olab.load.jdbc", mode = Mode.DBMS)
    @Description("olab.load.jdbc('key or url','statement',[params],config) YIELD row - select and update relational database, from a SQL statement with optional parameters")
    public Stream<RowResult> jdbc(@Name("jdbc") String urlOrKey, @Name("query") String query, @Name(value = "params", defaultValue = "[]") List<Object> params, @Name(value = "config", defaultValue = "{}") Map<String, Object> config) {
        log.info(String.format("Executing SQL update: %s", query));
        return executeUpdate(urlOrKey, query, config, params.toArray(new Object[params.size()]));
    }

    private Stream<RowResult> executeUpdate(String urlOrKey, String query, Map<String, Object> config, Object... params) {
        String url = getUrlOrKey(urlOrKey);
        LoadJdbcConfig jdbcConfig = new LoadJdbcConfig(config);
        try {
            Connection connection = getConnection(url, jdbcConfig);
            try {
                PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(5000);
                try {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                    ResultSet rs = stmt.executeQuery();
                    Iterator<Map<String, Object>> supplier = new ResultSetIterator(log, rs, true, jdbcConfig);
                    Spliterator<Map<String, Object>> spliterator = Spliterators.spliteratorUnknownSize(supplier, Spliterator.ORDERED);
                    return StreamSupport.stream(spliterator, false)
                            .map(RowResult::new)
                            .onClose(() -> closeIt(log, stmt, connection));
                } catch (Exception sqle) {
                    closeIt(log, stmt);
                    throw sqle;
                }
            } catch (Exception sqle) {
                closeIt(log, connection);
                throw sqle;
            }
        } catch (Exception e) {
            log.error(String.format("Cannot execute SQL statement `%s`.%nError:%n%s", query, e.getMessage()), e);
            String errorMessage = "Cannot execute SQL statement `%s`.%nError:%n%s";
            if (e.getMessage().contains("No suitable driver")) {
                errorMessage = "Cannot execute SQL statement `%s`.%nError:%n%s%n%s";
            }
            throw new RuntimeException(String.format(errorMessage, query, e.getMessage(), "Please download and copy the JDBC driver into $NEO4J_HOME/plugins,more details at https://neo4j-contrib.github.io/neo4j-apoc-procedures/#_load_jdbc_resources"), e);
        }
    }

    static void closeIt(Log log, AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (Exception e) {
                log.warn(String.format("Error closing %s: %s", c.getClass().getSimpleName(), c), e);
                // ignore
            }
        }
    }

    private static class ResultSetIterator implements Iterator<Map<String, Object>> {
        private final Log log;
        private final ResultSet rs;
        private final String[] columns;
        private final boolean closeConnection;
        private Map<String, Object> map;
        private LoadJdbcConfig config;


        public ResultSetIterator(Log log, ResultSet rs, boolean closeConnection, LoadJdbcConfig config) throws SQLException {
            this.config = config;
            this.log = log;
            this.rs = rs;
            this.columns = getMetaData(rs);
            this.closeConnection = closeConnection;
            this.map = get();
        }

        private String[] getMetaData(ResultSet rs) throws SQLException {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] columns = new String[cols + 1];
            for (int col = 1; col <= cols; col++) {
                columns[col] = meta.getColumnLabel(col);
            }
            return columns;
        }

        @Override
        public boolean hasNext() {
            return this.map != null;
        }

        @Override
        public Map<String, Object> next() {
            Map<String, Object> current = this.map;
            this.map = get();
            return current;
        }

        public Map<String, Object> get() {
            try {
                if (handleEndOfResults()) {
                    return null;
                }
                Map<String, Object> row = new LinkedHashMap<>(columns.length);
                for (int col = 1; col < columns.length; col++) {
                    row.put(columns[col], convert(rs.getObject(col), rs.getMetaData().getColumnType(col)));
                }
                return row;
            } catch (Exception e) {
                log.error(String.format("Cannot execute read result-set.%nError:%n%s", e.getMessage()), e);
                closeRs();
                throw new RuntimeException("Cannot execute read result-set.", e);
            }
        }

        private Object convert(Object value, int sqlType) {
            if (value == null) {
                return null;
            }
            if (value instanceof UUID || value instanceof BigInteger || value instanceof BigDecimal) {
                return value.toString();
            }
            if (Types.TIME == sqlType) {
                return ((java.sql.Time) value).toLocalTime();
            }
            if (Types.TIME_WITH_TIMEZONE == sqlType) {
                return OffsetTime.parse(value.toString());
            }
            if (Types.TIMESTAMP == sqlType) {
                if (config.getZoneId() != null) {
                    return ((java.sql.Timestamp) value).toInstant()
                            .atZone(config.getZoneId())
                            .toOffsetDateTime();
                } else {
                    return ((java.sql.Timestamp) value).toLocalDateTime();
                }
            }
            if (Types.TIMESTAMP_WITH_TIMEZONE == sqlType) {
                if (config.getZoneId() != null) {
                    return ((java.sql.Timestamp) value).toInstant()
                            .atZone(config.getZoneId())
                            .toOffsetDateTime();
                } else {
                    return OffsetDateTime.parse(value.toString());
                }
            }
            if (Types.DATE == sqlType) {
                return ((java.sql.Date) value).toLocalDate();
            }
            return value;
        }

        private boolean handleEndOfResults() throws SQLException {
            Boolean closed = isRsClosed();
            if (closed != null && closed) {
                return true;
            }
            if (!rs.next()) {
                closeRs();
                return true;
            }
            return false;
        }

        private void closeRs() {
            Boolean closed = isRsClosed();
            if (closed == null || !closed) {
                closeIt(log, ignore(rs::getStatement), closeConnection ? ignore(() -> rs.getStatement().getConnection()) : null);
            }
        }

        private Boolean isRsClosed() {
            try {
                return ignore(rs::isClosed);
            } catch (AbstractMethodError ame) {
                return null;
            }
        }

        interface FailingSupplier<T> {
            T get() throws Exception;
        }

        public static <T> T ignore(FailingSupplier<T> fun) {
            try {
                return fun.get();
            } catch (Exception e) {
                /*ignore*/
            }
            return null;
        }
    }
}

