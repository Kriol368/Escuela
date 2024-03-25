import java.util.InputMismatchException;
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
            if (curentScreen == 0 && option > 3 || curentScreen == 1 && option > 3 || curentScreen == 2 && option > 5) {
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
            System.out.println("0 Exit | 1 My Subjects | 2 Add Subject | 3 My Salary | 4 Qualify | 5 Logout " + userName);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void login() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login as a student or as a teacher? (1-Student 2-Teacher)");
        try {
            int opcion = scanner.nextInt();
            if (opcion == 1) {
                loginStudent();
            } else if (opcion == 2) {
                loginTeacher();
            } else {
                System.out.println("Incorrect option");
            }
        } catch (InputMismatchException ime) {
            System.out.println("Incorrect option");
        }

    }

    public static void loginStudent() throws SQLException {
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

    public static void loginTeacher() throws SQLException {
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
        try {
            Scanner scanner = new Scanner(System.in);
            int opcion = scanner.nextInt();
            if (opcion == 1) {
                registerStudent();
            } else if (opcion == 2) {
                registerTeacher();
            } else {
                System.out.println("Incorrect option");
            }
        } catch (InputMismatchException ime) {
            System.out.println("Incorrect option");
        }

    }


    private static void registerStudent() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st;
        System.out.println("Name:");
        String name = scanner.nextLine();
        String query = "INSERT INTO alumno (nombre) VALUES (?)";
        st = con.prepareStatement(query);
        st.setString(1, name);
        st.executeUpdate();
    }

    private static void registerTeacher() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st;
        System.out.println("Name:");
        String name = scanner.nextLine();
        System.out.println("Salary:");
        int salary = scanner.nextInt();
        String query = "INSERT INTO profesor (nombre,salario) VALUES (?,?)";
        st = con.prepareStatement(query);
        st.setString(1, name);
        st.setInt(2, salary);
        st.executeUpdate();
    }

    private static void mySubjects() throws SQLException {
        if (curentScreen == 1) {
            PreparedStatement st;
            String query = "SELECT a.id , a.nombre , COALESCE(aa.nota,0) ,a.horas FROM alumno_asignatura aa ,asignatura a WHERE a.id = aa.id_asignatura AND aa.id_alumno  = ?";
            st = con.prepareStatement(query);
            st.setInt(1, userID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - " + rs.getString(2) + "\n\t" + "Nota: " + rs.getInt(3) + "\n\t" + "Horas: " + rs.getInt(4));
            }
        } else if (curentScreen == 2) {
            PreparedStatement st;
            String query = "SELECT a.id , a.nombre , pa.aula  ,a.horas FROM profesor_asignatura pa ,asignatura a WHERE pa.id_asignatura  = a.id AND pa.id_profesor = ? ";
            st = con.prepareStatement(query);
            st.setInt(1, userID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - " + rs.getString(2) + "\n\t" + "Aula: " + rs.getString(3) + "\n\t" + "Horas: " + rs.getInt(4));
            }
        }
    }

    private static void addSubject() throws SQLException {
        allSubjects();
        if (curentScreen == 1) {
            try {
                Scanner scanner = new Scanner(System.in);
                PreparedStatement st;
                System.out.println("Introduce the subject id:");
                int id_asignatura = scanner.nextInt();
                String query = "INSERT INTO alumno_asignatura (id_alumno,id_asignatura) VALUES (?,?)";
                st = con.prepareStatement(query);
                st.setInt(1, userID);
                st.setInt(2, id_asignatura);
                st.executeUpdate();
            }catch (InputMismatchException ime) {
                System.out.println("Incorrect option");
            }
        } else if (curentScreen == 2) {
            try {
                Scanner scanner = new Scanner(System.in);
                PreparedStatement st;
                System.out.println("Introduce the subject id:");
                int id_asignatura = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Introduce the classoom: ");
                String aula = scanner.nextLine();
                String query = "INSERT INTO profesor_asignatura (id_profesor,id_asignatura,aula) VALUES (?,?,?)";
                st = con.prepareStatement(query);
                st.setInt(1, userID);
                st.setInt(2, id_asignatura);
                st.setString(3, aula);
                st.executeUpdate();
            }catch (InputMismatchException ime) {
                System.out.println("Incorrect option");
            }

        }

    }


    private static void mySalary() throws SQLException {
        PreparedStatement st;
        String query = "SELECT p.salario  FROM profesor p WHERE id = ?";
        st = con.prepareStatement(query);
        st.setInt(1, userID);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println("Salary: " + rs.getInt(1));
        }
    }

    private static void allStudents() throws SQLException {
        PreparedStatement st;
        String query = "SELECT * FROM alumno";
        st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " - " + rs.getString(2));
        }
    }

    public static void qualify() throws SQLException {
        try {
            allStudents();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce the id of the student to qualify: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            allSubjects();
            System.out.println("Select the id of the subject to qualify: ");
            int subjectId = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Introduce the qualification: ");
            int mark = scanner.nextInt();
            if (mark < 0 || mark > 10){
                System.out.println("Mark must be between 0 and 10");
            }else {
                PreparedStatement st;
                String query = "update alumno_asignatura  set nota = ? WHERE id_alumno = ? and id_asignatura = ?";
                st = con.prepareStatement(query);
                st.setInt(1, mark);
                st.setInt(2, studentId);
                st.setInt(3, subjectId);
                st.executeUpdate();
            }
        }catch (InputMismatchException ime) {
            System.out.println("Incorrect option");
        }
    }

    public static void main(String[] args) throws SQLException {
        int option;
        String host = "jdbc:sqlite:src/main/resources/escuela.sqlite";
        con = java.sql.DriverManager.getConnection(host);
        System.out.println("""
                ___________                           .__          \s
                \\_   _____/ ______ ____  __ __   ____ |  | _____   \s
                 |    __)_ /  ___// ___\\|  |  \\_/ __ \\|  | \\__  \\  \s
                 |        \\\\___ \\\\  \\___|  |  /\\  ___/|  |__/ __ \\_\s
                /_______  /____  >\\___  >____/  \\___  >____(____  /\s
                        \\/     \\/     \\/            \\/          \\/
                """);
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
                        mySalary();
                        break;
                    case 4:
                        qualify();
                        break;
                    case 5:
                        logout();
                        break;
                }
            }
        }
    }
}
