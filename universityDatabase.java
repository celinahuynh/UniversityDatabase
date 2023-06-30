import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class universityDatabase {

    public static void printDatabases() {
        System.out.println("Welcome to the university database. Queries available:");
        System.out.println("1. Search students by name.");
        System.out.println("2. Search students by year.");
        System.out.println("3. Search for students with a GPA >= threshold.");
        System.out.println("4. Search for students with a GPA <= threshold.");
        System.out.println("5. Get department statistics.");
        System.out.println("6. Get class statistics.");
        System.out.println("7. Execute an abitrary SQL query.");
        System.out.println("8. Exit the application.");
    }
    public static void main(String args[]) {
        String url = args[0], user = args[1], password = args[2];

        ArrayList <Integer> id = new ArrayList <Integer>();
        ArrayList <String> firstName = new ArrayList <String>();
        ArrayList <String> lastName = new ArrayList <String>();
        ArrayList <Double> gpa = new ArrayList <Double>();
        int[] credits = new int[100000]; 
        String invalid = ("Invalid query, try again.");
        String queryRun = ("Which query would you like to run (1-8)?");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next() != false) {
                firstName.add(rs.getString(1));
                lastName.add(rs.getString(2));
                id.add(rs.getInt(3));
            } 
            String s = "SELECT s.id, SUM(CASE WHEN h.grade = 'A' THEN 4 * c.credits WHEN h.grade = 'B' THEN 3 * c.credits WHEN h.grade = 'C' THEN 2 * c.credits WHEN h.grade = 'D' THEN 1 * c.credits WHEN h.grade = 'F' THEN 0 * c.credits END) / SUM(c.credits) AS gpa FROM students s INNER JOIN HasTaken h ON s.id = h.sid INNER JOIN classes c ON h.name = c.name GROUP BY s.first_name, s.last_name, s.id;";
            rs = stmt.executeQuery(s);

            while (rs.next() != false) {
                gpa.add(rs.getDouble(2));
            } 

            s = "SELECT s.id, completed_credits FROM students s JOIN (SELECT h.Sid, SUM(c.credits) AS completed_credits FROM HasTaken h JOIN classes c ON h.name = c.name AND h.Grade NOT IN ('F') GROUP BY h.Sid HAVING SUM(c.credits) >= 0 ) y ON s.id = y.sid ORDER BY s.last_name;";
            rs = stmt.executeQuery(s);
            while (rs.next() != false) {
                int idIndex = 0;
                while (idIndex < id.size()) {
                    if (id.get(idIndex) == rs.getInt(1)) {
                        credits[idIndex] = rs.getInt(2);
                    }
                    idIndex = idIndex + 1;
                }
            }
        }
        catch(Exception e1) {
            System.out.println(e1);
        }

        String dept[] = {"Bio", "Chem", "CS", "Eng", "Math", "Phys", "bio", "chem", "eng", "math", "phys", "cs"}; 
        printDatabases();
    
        Scanner query = new Scanner(System.in);
        System.out.println(queryRun);
        int number = query.nextInt();
        while (number != 8) {
            if(number < 1 || number > 8) {
                System.out.println(invalid);
                System.out.println(queryRun);
                number = query.nextInt();
                break;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, password);
                if (number == 1) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the name.");
                    String name1 = name.nextLine(), name2 = name1;
                    String s = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ?";
                    PreparedStatement p = con.prepareStatement(s);
                    p.clearParameters();
                    p.setString(1, name2);
                    p.setString(2, name2);
                    ResultSet rs = p.executeQuery();
                    
                    int count = 0; 
                    while (rs.next() != false) {
                        count = count + 1;
                    } 
                    System.out.println(count + " student(s) found");
                    rs = p.executeQuery();
                    Statement stmt = con.createStatement();
                    while (rs.next() != false) {
                        for(int i = 0; i < id.size(); i++) {
                            if (id.get(i) == rs.getInt(3)) {
                                System.out.println(lastName.get(i) + ", " + firstName.get(i));
                                System.out.println("ID: " + id.get(i));
                                String s1 = "SELECT * FROM majors";
                                ResultSet rs1 = stmt.executeQuery(s1);
                                ArrayList <String> majors = new ArrayList <String>();
                                ArrayList <String> minors = new ArrayList <String>();
                                while (rs1.next() != false) {
                                    if (rs1.getInt(1) == rs.getInt(3)) {
                                        majors.add(rs1.getString(2));
                                    }
                                }
                                if (majors.size() > 1) {
                                    System.out.print("Majors: ");
                                    for(int j = 0; j < majors.size() - 1; j++) {
                                        System.out.print(majors.get(j) + ", ");
                                    }
                                    System.out.println(majors.get(majors.size() - 1));
                                }
                                else {
                                    System.out.println("Major: " + majors.get(0));
                                }
                                s1 = "SELECT * FROM minors";
                                rs1 = stmt.executeQuery(s1);
                                while (rs1.next() != false) {
                                    if (rs1.getInt(1) == rs.getInt(3)) {
                                        minors.add(rs1.getString(2));
                                    }
                                }
                                if (minors.size() > 1 || minors.size() > 1.0) {
                                    System.out.print("Minors: ");
                                    int minorsIndex = 0;
                                    do {
                                        System.out.print(minors.get(minorsIndex) + ", ");
                                        minorsIndex++;
                                    } while (minorsIndex < (minors.size() - 1));
                                    System.out.println(minors.get(minors.size() - 1));
                                }
                                else if (minors.size() == 1 || minors.size() == 1.0) {
                                        System.out.println("Minor: " + minors.get(0));
                                    }
                                System.out.println("GPA: " + gpa.get(i));
                                System.out.println("Credits: " + credits[i]);
                            }
                        }
                    } 
                }
                if (number == 2) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the year. (Fr, So, Ju, Sr)");
                    String name1 = name.nextLine(), name2 = name1; 

                    while (name2.equalsIgnoreCase("Fr") != true && name2.equalsIgnoreCase("So") != true && name2.equalsIgnoreCase("Ju") != true && name2.equalsIgnoreCase("Sr") != true) {
                        System.out.println("Invalid year, try again.");
                        name1 = name.nextLine();
                        name2 = name1; 
                    }
                    String s = "SELECT s.id, CASE WHEN completed_credits BETWEEN 0 AND 29 THEN 'Fr' WHEN completed_credits BETWEEN 30 AND 59 THEN 'So' WHEN completed_credits BETWEEN 60 AND 89 THEN 'Ju' ELSE 'Sr' END AS Year, completed_credits FROM students s JOIN (SELECT h.Sid, SUM(c.credits) AS completed_credits FROM HasTaken h JOIN classes c ON h.name = c.name AND h.Grade NOT IN ('F') GROUP BY h.Sid HAVING SUM(c.credits) >= 0) y ON s.id = y.sid ORDER BY s.last_name ASC;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(s);
                    Statement stmt1 = con.createStatement();

                    int count = 0; 
                    while (rs.next() != false) {
                        if (rs.getString(2).equals(name2)) {
                            count = count + 1;
                        }
                    } 

                    System.out.println(count + " student(s) found");
                    rs = stmt.executeQuery(s);
                    while (rs.next() != false) {
                        if (rs.getString(2).equals(name2)) {
                            for (int i = 0; i < id.size(); i++) {
                                if (id.get(i) == rs.getInt(1)) {
                                    System.out.println(lastName.get(i) + ", " + firstName.get(i));
                                    System.out.println("ID: " + id.get(i));
                                    String s1 = "SELECT * FROM majors";
                                    ResultSet rs1 = stmt1.executeQuery(s1);
                                    ArrayList <String> majors = new ArrayList <String>();
                                    ArrayList <String> minors = new ArrayList <String>();
                                    while (rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            majors.add(rs1.getString(2));
                                        }
                                    }
                                    if (majors.size() > 1 || majors.size() > 1.0) {
                                        int majorsIndex = 0;
                                        System.out.print("Majors: ");
                                        do {
                                            System.out.print(majors.get(majorsIndex) + ", ");
                                            majorsIndex++;
                                        } while (majorsIndex < (majors.size() - 1));
                                        System.out.println(majors.get(majors.size() - 1));
                                    }
                                    else {
                                        System.out.println("Major: " + majors.get(0));
                                    }
                                    s1 = "SELECT * FROM minors";
                                    rs1 = stmt1.executeQuery(s1);
                                    while (rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            minors.add(rs1.getString(2));
                                        }
                                    }
                                    if (minors.size() > 1 || minors.size() > 1.0) {
                                        System.out.print("Minors: ");
                                        for(int j = 0; j < minors.size() - 1; j++) {
                                            System.out.print(minors.get(j) + ", ");
                                        }
                                        System.out.println(minors.get(minors.size() - 1));
                                    }
                                    else if (minors.size() == 1 || minors.size() == 1.0) {
                                        System.out.println("Minor: " + minors.get(0));
                                    }
                                    System.out.println("GPA: " + gpa.get(i));
                                    System.out.println("Credits: " + credits[i]);
                                }
                            }
                        }              
                    } 
                }
                if (number == 3) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the threshold.");
                    double threshold = name.nextDouble();
                    while (threshold < 0.0 || threshold > 4.0) {
                        System.out.println("Invalid threshold, try again.");
                        threshold = name.nextDouble();
                    }
                    String s = "SELECT s.id, SUM(CASE WHEN h.grade = 'A' THEN 4 * c.credits WHEN h.grade = 'B' THEN 3 * c.credits WHEN h.grade = 'C' THEN 2 * c.credits WHEN h.grade = 'D' THEN 1 * c.credits WHEN h.grade = 'F' THEN 0 * c.credits END) / SUM(c.credits) AS gpa FROM students s INNER JOIN HasTaken h ON s.id = h.sid INNER JOIN classes c ON h.name = c.name GROUP BY s.first_name, s.last_name, s.id;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(s);
                    Statement stmt1 = con.createStatement();

                    int count = 0; 
                    while (rs.next() != false) {
                        if (rs.getDouble(2) >= threshold) {
                            count = count + 1;
                        }
                    } 
                    System.out.println(count + " student(s) found");
                    rs = stmt.executeQuery(s);
                    while (rs.next() != false) {
                        if (rs.getDouble(2) >= threshold) {
                            for (int i = 0; i < id.size(); i++) {
                                if (id.get(i) == rs.getInt(1)) {
                                    System.out.println(lastName.get(i) + ", " + firstName.get(i));
                                    System.out.println("ID: " + id.get(i));
                                    String s1 = "SELECT * FROM majors";
                                    ResultSet rs1 = stmt1.executeQuery(s1);
                                    ArrayList <String> majors = new ArrayList <String>();
                                    ArrayList <String> minors = new ArrayList <String>();
                                    while(rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            majors.add(rs1.getString(2));
                                        }
                                    }
                                    if (majors.size() > 1) {
                                        System.out.print("Majors: ");
                                        for (int j = 0; j < majors.size() - 1; j++) {
                                            System.out.print(majors.get(j) + ", ");
                                        }
                                        System.out.println(majors.get(majors.size() - 1));
                                    }
                                    else {
                                        System.out.println("Major: " + majors.get(0));
                                    }
                                    s1 = "SELECT * FROM minors";
                                    rs1 = stmt1.executeQuery(s1);
                                    while (rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            minors.add(rs1.getString(2));
                                        }
                                    }
                                    if (minors.size() > 1 || minors.size() > 1.0) {
                                        System.out.print("Minors: ");
                                        for (int j = 0; j < minors.size() - 1; j++) {
                                            System.out.print(minors.get(j) + ", ");
                                        }
                                        System.out.println(minors.get(minors.size() - 1));
                                    }
                                    else if (minors.size() == 1 || minors.size() == 1.0) {
                                        System.out.println("Minor: " + minors.get(0));
                                    }
                                    System.out.println("GPA: " + gpa.get(i));
                                    System.out.println("Credits: " + credits[i]);
                                }
                            }
                        }  
                    } 
                }
                if (number == 4) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the threshold");
                    double threshold = name.nextDouble();
                    while (threshold < 0.0 || threshold > 4.0) {
                        System.out.println("Invalid threshold, try again.");
                        threshold = name.nextDouble();
                    }
                    String s = "SELECT s.id, SUM(CASE WHEN h.grade = 'A' THEN 4 * c.credits WHEN h.grade = 'B' THEN 3 * c.credits WHEN h.grade = 'C' THEN 2 * c.credits WHEN h.grade = 'D' THEN 1 * c.credits WHEN h.grade = 'F' THEN 0 * c.credits END) / SUM(c.credits) AS gpa FROM students s INNER JOIN HasTaken h ON s.id = h.sid INNER JOIN classes c ON h.name = c.name GROUP BY s.first_name, s.last_name, s.id;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(s);
                    Statement stmt1 = con.createStatement();
                    int count = 0; 
                    while (rs.next() != false) {
                        if (rs.getDouble(2) <= threshold) {
                            count = count + 1;
                        }
                    } 
                    System.out.println(count + " student(s) found");
                    rs = stmt.executeQuery(s);
                    while (rs.next() != false) {
                        if (rs.getDouble(2) <= threshold) {
                            for (int i = 0; i < id.size(); i++) {
                                if (id.get(i) == rs.getInt(1)) {
                                    System.out.println(lastName.get(i) + ", " + firstName.get(i));
                                    System.out.println("ID: " + id.get(i));
                                    String s1 = "SELECT * FROM majors";
                                    ResultSet rs1 = stmt1.executeQuery(s1);
                                    ArrayList <String> majors = new ArrayList <String>();
                                    ArrayList <String> minors = new ArrayList <String>();
                                    while(rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            majors.add(rs1.getString(2));
                                        }
                                    }
                                    if (majors.size() > 1 || majors.size() > 1.0) {
                                        System.out.print("Majors: ");
                                        for(int j = 0; j < majors.size() - 1; j++) {
                                            System.out.print(majors.get(j) + ", ");
                                        }
                                        System.out.println(majors.get(majors.size() - 1));
                                    }
                                    else {
                                        System.out.println("Major: " + majors.get(0));
                                    }
                                    s1 = "SELECT * FROM minors";
                                    rs1 = stmt1.executeQuery(s1);
                                    while (rs1.next() != false) {
                                        if (rs1.getInt(1) == rs.getInt(1)) {
                                            minors.add(rs1.getString(2));
                                        }
                                    }
                                    if (minors.size() > 1 || minors.size() > 1.0) {
                                        System.out.print("Minors: ");
                                        for (int j = 0; j < minors.size() - 1; j++) {
                                            System.out.print(minors.get(j) + ", ");
                                        }
                                        System.out.println(minors.get(minors.size() - 1));
                                    }
                                    else if (minors.size() == 1 || minors.size() == 1.0) {
                                        System.out.println("Minor: " + minors.get(0));
                                    }
                                    System.out.println("GPA: " + gpa.get(i));
                                    System.out.println("Credits: " + credits[i]);
                                }
                            }
                        } 
                    } 
                }
                if(number == 5) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the department.");
                    String name1 = name.nextLine(), name2 = name1; 

                    boolean isDept = false; 
                    while (isDept != true) {
                        for (int d = 0; d < dept.length; d++) {
                            if (dept[d].equalsIgnoreCase(name2) != false) {
                                isDept = true;
                                break;
                            }
                        }
                        if (isDept != true) {
                            System.out.println("Invalid department, try again.");
                            name1 = name.nextLine();
                            name2 = name1;
                        }
                    }
                    String s = "SELECT d.name, COUNT(DISTINCT s.id) AS num_students, AVG(gpa) AS avg_gpa FROM students s LEFT JOIN majors m ON s.id = m.sid LEFT JOIN minors mn ON s.id = mn.sid LEFT JOIN departments d ON m.dname = d.name OR mn.dname = d.name LEFT JOIN ( SELECT h.sid, AVG(CASE h.grade WHEN 'A' THEN 4  WHEN 'B' THEN 3  WHEN 'C' THEN 2  WHEN 'D' THEN 1  ELSE 0  END) AS gpa FROM HasTaken h GROUP BY h.sid ) AS gpas ON s.id = gpas.sid GROUP BY d.name;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(s);
                    
                    int numStudents = 0;
                    double avg = 0.0;
                    while (rs.next() != false) {
                        if(name2.equalsIgnoreCase(rs.getString(1))) {
                           numStudents = rs.getInt(2);
                           avg = rs.getDouble(3);
                        }
                    }     
                    System.out.println("Num students: " + numStudents);   
                    System.out.println("Average GPA: " + avg);        
                }
                if(number == 6) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the class name.");
                    String name1 = name.nextLine(), name2 = name1; 
                   
                    Statement stmt = con.createStatement();
                    ArrayList <String> classes = new ArrayList <String>();
                    String c1 = "SELECT * FROM classes;";
                    ResultSet rs = stmt.executeQuery(c1);

                    while (rs.next() != false) {
                        classes.add(rs.getString(1));
                    } 
                    boolean isClass = false; 
                    while (isClass != true) {
                        for (int d = 0; d < classes.size(); d++) {
                            if (classes.get(d).equalsIgnoreCase(name2) != false) {
                                isClass = true;
                                break;
                            }
                        }
                        if (isClass != true) {
                            System.out.println("Invalid class, try again.");
                            name1 = name.nextLine();
                            name2 = name1;
                        }
                    }
                    String s = "SELECT * FROM hastaken;";
                    rs = stmt.executeQuery(s);

                    int zero = 0;
                    int a = zero, b = zero, c = zero, d = zero, f = zero;
                    while (rs.next() != false) {
                        if (rs.getString(2).equals(name2)) {
                            if (rs.getString(3).equals("A")) {
                                a++;
                            }
                            else if (rs.getString(3).equals("B")) {
                                b++;
                            }
                            else if (rs.getString(3).equals("C")) {
                                c++;
                            }
                            else if (rs.getString(3).equals("D")) {
                                d++;
                            }
                            else if (rs.getString(3).equals("F")) {
                                f++;
                            }
                        }
                        
                    }  
                    s = "SELECT * FROM istaking;"; 
                    rs = stmt.executeQuery(s);
                    int count = zero; 

                    while (rs.next() != false) {
                        if (rs.getString(2).equals(name2)) {
                            count = count + 1;
                        }
                    }
                    System.out.println(count + " students currently enrolled");
                    System.out.println("Grades of previous enrollees:");  
                    System.out.println("A " + a); 
                    System.out.println("B " + b);      
                    System.out.println("C " + c);      
                    System.out.println("D " + d);      
                    System.out.println("F " + f);         
                }
                if (number == 7) {
                    Scanner name = new Scanner(System.in);
                    System.out.println("Please enter the query.");
                    String name1 = name.nextLine(), name2 = name1; 
                    boolean isValid = false, error = true;
                    Statement stmt = con.createStatement();
                    ResultSet rs; 

                    while (isValid != true) {
                        try {
                                rs = stmt.executeQuery(name2);
                                ResultSetMetaData rsmd = rs.getMetaData();
                                int column_count = rsmd.getColumnCount();
                                int colIndex = 1;

                                do {
                                    System.out.print(rsmd.getColumnLabel(colIndex) + "\t");
                                    colIndex++;
                                } while (colIndex <= column_count);
                                System.out.println();
                                int type = 0;
                                int one = 1;

                                while (rs.next() != false) {
                                    for (int i = one; i <= column_count; i++) {
                                        type = rsmd.getColumnType(i);
                                        if (type == one || type == -16 || type == 12) {
                                            System.out.print(rs.getString(i) + "\t");
                                        }
                                        else if (type == 3 || type == 8 || type == 6) {
                                            System.out.print(rs.getFloat(i) + "\t");
                                        }
                                        else if (type == 4 || type == -6 || type == 5 || type == 7) {
                                            System.out.print(rs.getInt(i) + "\t");
                                        }
                                    }
                                    System.out.println();
                                }
                        }
                        catch(Exception e1) {
                                System.out.println(invalid);
                                System.out.println(e1);
                                name1 = name.nextLine();
                                name2 = name1; 
                                error = false;
                        }
                        if (error != false) {
                            isValid = true; 
                        }
                        error = true; 
                    }
                }
                
            }
            catch(Exception e) {
                System.out.println(e);
            }
            System.out.println(queryRun);
            number = query.nextInt();
        }
        if(number == 8) {
            System.out.println("Goodbye");
        }
        query.close();
    }
}
