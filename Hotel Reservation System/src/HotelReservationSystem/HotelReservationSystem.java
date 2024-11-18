package HotelReservationSystem;
import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false";
    private static final String userName = "root";
    private static final String userPassword = "123456";


    public static void main(String[] args) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver" );
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url,userName,userPassword);
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM!");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room: ");
                System.out.println("2. View Reservations: ");
                System.out.println("3. Get Room Number: ");
                System.out.println("4. Update Reservation: ");
                System.out.println("5. Delete Reservation: ");
                System.out.println("0. Exit: ");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // Reserve a room
                        reservation(connection,scanner);
                        break;
                    case 2:
                        // view Reservation
                        viewReservations(connection);
                        break;
                    case 3:
                        // Get Room Number
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        // Update Reservation
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        // Delete Reservation
                        deleteReservation(connection,scanner);
                        break;
                    case 0:
                        // Exit
                        System.out.println("Exiting...Thanks For choosing Hotel Management System!");

                        return;
                    default:
                        System.out.println("Choose an correct option!");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Reservation

    private static void reservation(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room Number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.nextLine();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " + "values ('" + guestName + "'," +roomNumber+ ", '" +contactNumber+ "')";

            try(Statement statement = connection.createStatement()){
                int affectRow = statement.executeUpdate(sql);
                if(affectRow>0){
                    System.out.println("Reservation Successfully!");
                }else {
                    System.out.println("Reservation failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // View Reservation

    private static void viewReservations(Connection connection) throws SQLException{
        String sql = "select reservation_id, guest_name,room_number,contact_number, reservation_date from reservations ";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservation: ");
            System.out.println("+----------------+-----------------+---------------+---------------------+---------------------+");
            System.out.println("| Reservation Id | Guest           | Room Number   | Contact Number      | Reservation Date    |");
            System.out.println("+----------------+-----------------+---------------+---------------------+---------------------+");

            while (resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                System.out.printf("| %-14s | %-15s | %-13s | %-20s | %-19s |\n",reservationId,guestName,roomNumber,contactNumber,reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+---------------------+---------------------+");
        }
    }

    private static void getRoomNumber(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter reservation Id: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "select room_number from reservations "+" where reservation_id = "+reservationId+
                    " and guest_name = '"+guestName+ "'";

            try(Statement statement = connection.createStatement();
            ResultSet resultSet= statement.executeQuery(sql)) {

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation Id "+reservationId+" and Guest "+guestName+" is: "+roomNumber);
                }else {
                    System.out.println("Reservation not found for the Id and guest name.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateReservation(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter reservation Id to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExits(connection, reservationId)){
                System.out.print("Reservation not found for the given Id.");
                return;
            }
            System.out.print("Enter new Guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "update reservations set guest_name = '" +newGuestName+ "',"+" room_number = " +newRoomNumber+","+
                    "contact_number = '"+newContactNumber+ "' " +
                    "where reservation_id = " +reservationId;
            try(Statement statement = connection.createStatement()){
                int affectRows = statement.executeUpdate(sql);
                if (affectRows > 0){
                    System.out.println("Reservation update Successfully!");
                }else {
                    System.out.println("Reservation update failed.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean reservationExits(Connection connection,int reservationId){
        try{
            String sql = "select reservation_id  from reservations where reservation_id = "+reservationId;
            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }


    private static void deleteReservation(Connection connection,Scanner scanner){
        try{
            System.out.print("Enter reservation Id to delete: ");
            int reservationId = scanner.nextInt();
            if(!reservationExits(connection,reservationId)){
                System.out.println("Reservation not found for this given Id ");
                return;
            }
            String sql = "Delete from reservations where reservation_id = "+reservationId;
            try(Statement statement = connection.createStatement()){
                int afftectedRow = statement.executeUpdate(sql);
                if(afftectedRow>0){
                    System.out.println("Reservation Delete successfully!");
                }else {
                    System.out.println("Reservation deletion failed");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    }

