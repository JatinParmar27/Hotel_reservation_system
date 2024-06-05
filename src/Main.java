// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_reservation_system_java";

    private static final String username = "root";
    private static final String password = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        System.out.println("Hello WOrld");
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection conn = DriverManager.getConnection(url, username, password);
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 :
                        reserveRoom(conn, sc);
                        break;
                    case 2 :
                        viewReservations(conn);
                        break;
                    case 3 :
                        getRoomNumber(conn, sc);
                        break;
                    case 4 :
                        updateReservation(conn, sc);
                        break;
                    case 5 :
                        deleteReservation(conn, sc);
                        break;
                    case 0 :
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Choise. Try Again :/");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException();
        }


    }

    private static void exit() throws  InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You For Using Hotel Reservation System !!!");
    }

    private static void deleteReservation(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter Reservation ID to delete: ");
            int rid = sc.nextInt();

            if (!reservationExists(conn, rid)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + rid;

            try(Statement st = conn.createStatement()){
                int affrows = st.executeUpdate(sql);

                if (affrows > 0){
                    System.out.println("Reservation deleted successfully! :)");

                }else {
                    System.out.println("Reservation Deletion Failed. :/");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateReservation(Connection conn, Scanner sc) {
        try{
            System.out.println("Enter Reservatoni ID to update: ");
            int rid = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(conn, rid)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter Guest Name: ");
            String gName = sc.nextLine();
            System.out.println("Enter new Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter New Contact number: ");
            String newContactNumber = sc.nextLine();

            String sql = "UPDATE reservations SET guest_name = '" + gName + "', room_number = " + newRoomNumber + ", contact_number = " + newContactNumber + ", WHERE reservation_id = " + rid;

            try(Statement st = conn.createStatement()){
                int affectedRows = st.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation updated Successfully! :)");
                }else {
                    System.out.println("Reservation Update Faild. :/");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean reservationExists(Connection conn, int rid) {

        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + rid;

            try(Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)){
                return rs.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private static void getRoomNumber(Connection conn, Scanner sc) {
        try{
            System.out.println("Enter Reservation ID: ");
            int rid = sc.nextInt();
            System.out.println("Enter Guest Name: ");
            String gname = sc.next();

            String sql = "SELECT room_number FROM reservations WHERE reservation_id = " + rid + " AND guest_name = " + gname;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()){
                    int rno = rs.getInt("room_number");
                    System.out.println("Room Number for reservation id " + rid + " and Guest " + gname + "is : " + rno);
                }else {
                    System.out.println("Reservation not found for the given Id and guest name.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private static void viewReservations(Connection conn) {
        String sql = " SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations ";

        try(Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            System.out.println("Current Reservations: ");
            System.out.println("+---------+------------------------------------+---------------+-------------------+----------------------------+");
            System.out.println("| R ID    | Guest Name                         | Room No.      | contact No.       | Date                       |");
            System.out.println("+---------+------------------------------------+---------------+-------------------+----------------------------+");

            while (rs.next()){
                int rid = rs.getInt("reservation_id");
                String gname = rs.getString("guest_name");
                int rno = rs.getInt("room_number");
                String cno = rs.getString("contact_number");
                String rdate = rs.getTimestamp("reservation_date").toString();

//                format and displa the reservation date in a tabel-like format
                System.out.printf("| %-7d | %-34s | %-13d | %-17s | %-26s |\n ", rid, gname, rno, cno, rdate);
            }
            System.out.println("+---------+------------------------------------+---------------+-------------------+----------------------------+");



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void reserveRoom(Connection conn, Scanner sc) {
        try {
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter Room Number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter Contact Number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number) VALUES ('"+ guestName +"','"+ roomNumber +"','"+ contactNumber +"')";

            try (Statement st = conn.createStatement()) {
                int affectedRows = st.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation Successful! :)");
                }else {
                    System.out.println("Reservation Failed. :/");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}