package tables;

import java.io.Closeable;
import java.nio.channels.ClosedChannelException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author karsten
 */
public abstract class Table implements Closeable {

    private boolean closed = false;

    private static void setArguments(PreparedStatement ps, Object[] arguments) throws SQLException {
        // Adds all arguments to the query
        for (int i = 0; i < arguments.length; i++) {
            ps.setObject(i + 1, arguments[i]);
        }
    }

    protected static ResultSet select(String query) throws RuntimeException, IllegalArgumentException {
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Store the result of the select query in the ResultSet
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Returns the result set
                return rs;
            }

            throw new IllegalArgumentException("ResultSet is empty");

        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected static ResultSet select(String query, Object... arguments) throws RuntimeException, IllegalArgumentException {
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Store the result of the select query in the ResultSet
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Returns the result set
                return rs;
            }

            throw new IllegalArgumentException("ResultSet is empty");

        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected static <T> List<T> map(ResultSet rs, Function<ResultSet, T> func) {
        List<T> result = new ArrayList<>();

        try {
            do {
                result.add(func.apply(rs));
            } while (rs.next());
        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }
    
    protected static <T> List<T> mapIDs(ResultSet rs, Function<Integer, T> func) {
        List<T> result = new ArrayList<>();

        try {
            do {
                result.add(func.apply(rs.getInt(1)));
            } while (rs.next());
        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    protected int update(String query, Object... arguments) {
        checkClosed();
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Execute the update query
            return ps.executeUpdate();
        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected static int insert(String query, Object... arguments) {
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Execute the update query
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet generatedKeys = ps.getGeneratedKeys();

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new IllegalArgumentException("More than one entity was updated/inserted");
        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    protected void deleteMultipleRows(String query, Object... arguments) {
        checkClosed();
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Execute the update query
            int rowsAffected = ps.executeUpdate();

        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
        
        close();
    }
    
    protected void delete(String query, Object... arguments) {
        checkClosed();
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Execute the update query
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                throw new IllegalArgumentException("More than one entity was updated/inserted");
            }

        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
        
        close();
    }

    public boolean isOpen() {
        return !closed;
    }

    public boolean isClose() {
        return closed;
    }

    private void checkClosed() {
        if (closed) {
            throw new RuntimeException("This row/entity is already closed");
        }
    }

    public void close() {
        closed = true;
    }
}
