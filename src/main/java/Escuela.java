import java.util.Scanner;
import java.sql.*;

public class Escuela {
    private static java.sql.Connection con;
    private static int curentScreen = 0;

    private static String userName;
    private static int userID;

    private static int getOption() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        try {
            option = Integer.parseInt(scanner.next());
            if (curentScreen == 0 && option > 3 || curentScreen == 1 && option > 3 || curentScreen == 2 && option > 4) {
                System.out.println("Incorrect option");
            }
        } catch (IllegalArgumentException iae) {
            System.out.println("Incorrect option");
        }
        return option;
    }

    public static void printMenu() {
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (curentScreen == 0) {
            System.out.println("0 Exit | 1 All Subjects | 2 Login | 3 Register");
        } else if (curentScreen == 1) {
            System.out.println("0 Exit | 1 My Subjects | 2 Add Subject | 3 Logout " + userName);
        } else {
            System.out.println("0 Exit | 1 My Subjects | 2 My Salary | 3 Add Subject | 4 Qualify | 5 Logout " + userName);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void login() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login as a student or as a teacher? (1-Student 2-Teacher)");
        int opcion = scanner.nextInt();
        if (opcion == 1){
            loginStudent();
        }else if (opcion == 2){
            loginTeacher();
        }
    }

    public static void loginStudent() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String name = scanner.nextLine();
        PreparedStatement st;
        String query = "SELECT * FROM alumno WHERE nombre = ?";
        st = con.prepareStatement(query);
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            userID = rs.getInt(1);
            userName = rs.getString(2);
            curentScreen = 1;
        } else {
            System.out.println("User not found");
        }
    }

    public static void loginTeacher() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String name = scanner.nextLine();
        PreparedStatement st;
        String query = "SELECT * FROM profesor WHERE nombre = ?";
        st = con.prepareStatement(query);
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            userID = rs.getInt(1);
            userName = rs.getString(2);
            curentScreen = 2;
        } else {
            System.out.println("User not found");
        }
    }
    private static void logout() {
        curentScreen = 0;
    }

    private static void allSubjects() throws SQLException {
        PreparedStatement st;
        String query = "SELECT * FROM asignatura";
        st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " - " + rs.getString(2) + "\n\t" + "Horas: " + rs.getInt(3));
        }
    }

    private static void register() throws SQLException {
        System.out.println("Do you want to registes as a student or as a teacher? (1-Student 2-Teacher)");
        Scanner scanner = new Scanner(System.in);
        int opcion = scanner.nextInt();
        if (opcion == 1){
            registerStudent();
        }else if (opcion == 2){
            registerTeacher();
        }
    }


    private static void registerStudent() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st;
        System.out.println("Name:");
        String name = scanner.nextLine();
        String query = "INSERT INTO alumno (nombre) VALUES (?)";
        st = con.prepareStatement(query);
        st.setString(1, name);
        st.executeUpdate();
    }

    private static void registerTeacher() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st;
        System.out.println("Name:");
        String name = scanner.nextLine();
        System.out.println("Salary:");
        int salary = scanner.nextInt();
        String query = "INSERT INTO profesor (nombre,salario) VALUES (?,?)";
        st = con.prepareStatement(query);
        st.setString(1, name);
        st.setInt(2,salary);
        st.executeUpdate();
    }
    private static void mySubjects() throws SQLException {
        PreparedStatement st;
        String query = "SELECT a.id , a.nombre , COALESCE(aa.nota,0) ,a.horas FROM alumno_asignatura aa ,asignatura a WHERE a.id = aa.id_asignatura AND aa.id_alumno  = ?";
        st = con.prepareStatement(query);
        st.setInt(1, userID);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " - " + rs.getString(2) + "\n\t" + "Nota: " + rs.getInt(3) + "\n\t" + "Horas: " + rs.getInt(4));
        }
    }

    private static void othersPosts() throws SQLException {
        PreparedStatement st;
        String query = "SELECT p.id, p.texto, p.likes, p.fecha, u.nombre FROM posts p,usuarios u where p.id_usuario = u.id and p.id_usuario != ?";
        st = con.prepareStatement(query);
        st.setInt(1, userID);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " - " + rs.getString(2) + "\n\t" + "Likes: " + rs.getInt(3) + "\n\t" + "Fecha: " + rs.getTimestamp(4) + "\n\t" + "Usuario: " + rs.getString(5));
        }
    }

    private static void addSubject() throws SQLException {
        allSubjects();
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st;
        System.out.println("Introduce the subject id:");
        int id_asignatura = scanner.nextInt();
        String query = "INSERT INTO alumno_asignatura (id_alumno,id_asignatura) VALUES (?,?)";
        st = con.prepareStatement(query);
        st.setInt(1,userID);
        st.setInt(2, id_asignatura);
        st.executeUpdate();
    }

    private static void comment() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int postId;
        String texto;
        System.out.println("Select a post to comment:");
        othersPosts();
        postId = Integer.parseInt(scanner.nextLine());
        PreparedStatement st;
        System.out.println("Comentario:");
        texto = scanner.nextLine();
        String query = "INSERT INTO comentarios (texto,fecha,id_usuario,id_post) VALUES (?,current_timestamp,?,?)";
        st = con.prepareStatement(query);
        st.setString(1, texto);
        st.setInt(2, userID);
        st.setInt(3, postId);
        st.executeUpdate();
    }

    private static void like() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int postId;
        System.out.println("Select a post to like:");
        othersPosts();
        postId = Integer.parseInt(scanner.nextLine());
        PreparedStatement st;
        String query = "UPDATE posts SET likes = likes + 1 where id = ?";
        st = con.prepareStatement(query);
        st.setInt(1, postId);
        st.executeUpdate();
    }

    public static void main(String[] args) throws SQLException {
        int option;
        String host = "jdbc:sqlite:src/main/resources/escuela.sqlite";
        con = java.sql.DriverManager.getConnection(host);
        while (true) {
            printMenu();
            option = getOption();
            if (option == 0) break;
            if (curentScreen == 0) {
                switch (option) {
                    case 1:
                        allSubjects();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        register();
                        break;
                }
            } else if (curentScreen == 1) {
                switch (option) {
                    case 1:
                        mySubjects();
                        break;
                    case 2:
                        addSubject();
                        break;
                    case 3:
                        logout();
                        break;
                }
            } else {
                switch (option) {
                    case 1:
                        mySubjects();
                        break;
                    case 2:
                        addSubject();
                        break;
                    case 3:
                        comment();
                        break;
                    case 4:
                        like();
                        break;
                    case 5:
                        like();
                        break;
                }
            }
        }
    }
}
