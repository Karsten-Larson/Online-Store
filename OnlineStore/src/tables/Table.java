package tables;

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
public abstract class Table {

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

    protected static int update(String query, Object... arguments) {
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

    protected static int updateRow(String query, Object... arguments) {
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
}
