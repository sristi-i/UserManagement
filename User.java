import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
    private long userId;
    private String fName;
    private String name;
    private Timestamp signupDate;

    // Constructors
    public User() {}

    public User(long userId, String fName, String name, Timestamp signupDate) {
        this.userId = userId;
        this.fName = fName;
        this.name = name;
        this.signupDate = signupDate;
    }

    // Getter and setter methods for the variables
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Timestamp signupDate) {
        this.signupDate = signupDate;
    }

    /**
     * This method queries the users table and returns all the users
     * @param connection
     * @return Collection of users in the table
     * @throws SQLException
     */
    public static Collection<User> getAllUsers(Connection connection) throws SQLException {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getLong("userid"),
                        resultSet.getString("fname"),
                        resultSet.getString("name"),
                        resultSet.getTimestamp("signupdate")
                );
                users.add(user);
            }
        }
        return users;
    }

    /**
     * This method queries the users table for a particular user with given userId
     * @param connection
     * @param userId
     * @return User for the given userId
     * @throws SQLException
     */
    public static User getUserById(Connection connection, long userId) throws SQLException {
        String query = "SELECT * FROM users WHERE userid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("userid"),
                            resultSet.getString("fname"),
                            resultSet.getString("name"),
                            resultSet.getTimestamp("signupdate")
                    );
                }
            }
        }
        return null; // User not found
    }

    /**
     * This method updates an existing user with the modified fields
     * @param connection
     * @param user
     * @throws SQLException
     */
    public static void updateUser(Connection connection, User user) throws SQLException {
        String query = "UPDATE users SET fname = ?, name = ?, signupdate = ? WHERE userid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getfName());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setTimestamp(3, user.getSignupDate());
            preparedStatement.setLong(4, user.getUserId());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * main function to make example calls to all the methods by setting up the connection once
     * @param args
     */
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/mydb";
        String username = "username_for_database";
        String password = "password_for_database";

        // Examples invocations of all methods
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("Querying all users:");
            Collection<User> users = getAllUsers(connection);
            users.forEach(user -> 
                System.out.println(user.getUserId() + " " + user.getfName() + " " + user.getName() + " " + user.getSignupDate())
            );

            System.out.println("\nQuerying user by ID:");
            User user = getUserById(connection, 1L);
            if (user != null) {
                System.out.println("Found user: " + user.getUserId() + " " + user.getfName() + " " + user.getName() + " " + user.getSignupDate());
            } else {
                System.out.println("User not found");
            }

            System.out.println("\nUpdating user:");
            if (user != null) {
                user.setfName("New fName");
                user.setName("New name");
                updateUser(connection, user);
                System.out.println("User updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
