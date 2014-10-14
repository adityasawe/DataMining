import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.inference.TTest;

public class cluster {

	public void last_query(HashMap<String, ArrayList<String>> comb, HashMap<String, ArrayList<String>> comb_all,String finaluids, String test_pt) throws SQLException {
		
		ArrayList<String> test2 = new ArrayList<String>();
		System.out.print("Enter your username: ");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String user = br.readLine();
		
		System.out.print("Enter your password: ");
		String passwd = br.readLine();
		
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:" + user
				+ "/" + passwd + "@aos.acsu.buffalo.edu:1521/aos.buffalo.edu");

		Statement s2 = con.createStatement();
		ResultSet rs2;
		rs2 = s2.executeQuery("SELECT "+" " +test_pt +" FROM PART3 WHERE UID_1 in " + "("
				+ finaluids + ")");
		while (rs2.next()) {
			test2.add(rs2.getString(1));
		}
		
		double[] d = new double[40];
		int cnt = 0;
		for(Entry<String, ArrayList<String>> e: comb.entrySet()){
			d[cnt] = calculatecorrelation(e.getValue().toArray(new String[e.getValue().size()]), test2.toArray(new String[test2.size()]));
			cnt++;
		}
		
		double[] d_all = new double[13];
		cnt = 0;
		for(Entry<String, ArrayList<String>> e: comb_all.entrySet()){
			d_all[cnt] = calculatecorrelation(e.getValue().toArray(new String[e.getValue().size()]), test2.toArray(new String[test2.size()]));
			cnt++;
		}
		
		TTest tt=new TTest();
		double finalres= tt.tTest(d, d_all);
		System.out.println("For "+test_pt);
		if(finalres<0.01)
		System.out.println(finalres + "\tHence ALL");
		else
		System.out.println(finalres + "\tHence NOT ALL");
		

	}

	public static double calculatecorrelation(String[] a, String[] b) {
		double meana = 0;
		double meanb = 0;

		for (int i = 0; i < a.length; i++) {
			meana += Double.parseDouble(a[i]);
			meanb += Double.parseDouble(b[i]);
		}

		meana = meana / a.length;
		meanb = meanb / a.length;
		double con = 0;
		double sa = 0;
		double sb = 0;
		for (int i = 0; i < a.length; i++) {
			con += (meana - Double.parseDouble(a[i]))
					* (meanb - Double.parseDouble(b[i]));
			sa += Math.pow((meana - Double.parseDouble(a[i])), 2);
			sb += Math.pow((meanb - Double.parseDouble(b[i])), 2);
		}

		sa = Math.sqrt(sa);
		sb = Math.sqrt(sb);

		return (con / (sa * sb));
	}

}
