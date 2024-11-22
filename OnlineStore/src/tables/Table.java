package tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    protected static ResultSet select(String query, Object... arguments) throws RuntimeException, IllegalArgumentException {
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Store the result of the select query in the ResultSet
            ResultSet rs = ps.executeQuery();

            // Table is empty
            if (!rs.next()) {
                throw new IllegalArgumentException("ResultSet is empty");
            }

            // Returns the result set
            return rs;

        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected static void update(String query, Object... arguments) {
        Connection conn = DatabaseManager.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(query);

            // Adds all arguments to the query
            setArguments(ps, arguments);

            // Execute the update query
            ps.executeUpdate();
        } catch (SQLException ex) {
            // Major error
            throw new RuntimeException(ex.getMessage());
        }
    }

}
