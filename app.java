import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class App {
    public static void main(String args[]) {

        String url = args[0];
        String user = args[1];
        String password = args[2];

        
        ArrayList <Integer> id = new ArrayList <Integer>();
        ArrayList <String> classes = new ArrayList <String>();
        String grade[] = {"A", "B", "C", "D", "F"}; 
        int year[] = new int[4];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next() != false) {
                id.add(rs.getInt(3));
            } 

            ResultSet allClasses = stmt.executeQuery("SELECT * FROM Classes");
            while (allClasses.next() != false) {
                classes.add(allClasses.getString(1));
            } 

            int classesCount[] = new int[classes.size()]; 

            double classSize = id.size() * 0.25;
            int fr = 10;
            int so = 20;
            int jr = 30;
            int sr = 40;
            int one = 1;
            
            for(int i = 0; i < id.size(); i++) { 
                Random rn = new Random();
                int randomNum = 0;
                if (year[0] < classSize) {
                    randomNum = rn.nextInt((fr - one) + one) + one;
                    year[0] = year[0] + one;
                }
                else if (year[1] < classSize) {
                    randomNum = rn.nextInt((so - fr) + one) + fr;
                    year[1] = year[1] + one;
                }
                else if (year[2] < classSize) {
                    randomNum = rn.nextInt((jr - so) + one) + so;
                    year[2] = year[2] + one;
                }
                else if (year[3] < classSize) {
                    randomNum = rn.nextInt((sr - jr) + one) + sr;
                    year[3] = year[3] + one;
                }

                int total = 100;

                for (int j = 0; j < randomNum; j++) {
                    int ranClass = rn.nextInt((total - one - one) + one) + one;
                    if (classesCount[ranClass] == 0 || classesCount[ranClass] == 0.0) {
                        classesCount[ranClass] = one;
                    }
                    else if (classesCount[ranClass] == one || classesCount[ranClass] == 1.0) {
                        while(classesCount[ranClass] != one) {
                            ranClass = rn.nextInt((total - one - one) + one) + one;
                        }
                        classesCount[ranClass] = one;
                    }
                    int ranGrade = rn.nextInt(5);
                    
                    String s = "INSERT INTO hastaken " + "VALUES (?,?,?)";
                    PreparedStatement p = con.prepareStatement(s);
                    p.clearParameters();
                    p.setInt(one, id.get(i));
                    p.setString(2, classes.get(ranClass));
                    p.setString(3, grade[ranGrade]);
                    p.executeUpdate();
                }

                int ranIstaking = rn.nextInt((6 - one) + one) + one;

                for(int j = 0; j < ranIstaking; j++) {
                    int ranClass = rn.nextInt((total - one - one) + one) + one;
                    if (classesCount[ranClass] == 0 || classesCount[ranClass] == 0.0) {
                        classesCount[ranClass] = one;
                    }
                    else if (classesCount[ranClass] == one || classesCount[ranClass] == 1.0) {
                        while (classesCount[ranClass] != one){
                            ranClass = rn.nextInt((total - one - one) + one) + one;
                        }
                        classesCount[ranClass] = one;
                    }
                    String s = "INSERT INTO istaking " + "VALUES (?,?)";
                    PreparedStatement p = con.prepareStatement(s);
                    p.clearParameters();
                    p.setInt(one, id.get(i));
                    p.setString(2, classes.get(ranClass));
                    p.executeUpdate();
                }

                for (int j = 0; j < classes.size(); j++) {
                    classesCount[j] = 0; 
                }
            }
            
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
