import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.stat.inference.TTest;

public class query2 {

	public static void main(String args[]) throws java.sql.SQLException,
			java.io.IOException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Could not load the driver");
			System.exit(0);
		}

		HashMap<String, ArrayList<String>> comb = new HashMap<String, ArrayList<String>>();

		HashMap<String, ArrayList<String>> comb_all = new HashMap<String, ArrayList<String>>();
	
		System.out.print("Enter your username: ");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String user = br.readLine();
		
		System.out.print("Enter your password: ");
		String passwd = br.readLine();

		Connection con = DriverManager.getConnection("jdbc:oracle:thin:" + user
				+ "/" + passwd + "@aos.acsu.buffalo.edu:1521/aos.buffalo.edu");

		Statement s1 = con.createStatement();
		ResultSet rs1;

		Statement s2 = con.createStatement();
		ResultSet rs2;

		/*
		 * 
		 * 
		 * SELECT MF.EXPRESSION, MF.PROBE_ID FROM MICROARRAY_FACT MF WHERE
		 * MF.SAMPLE_ID IN (SELECT DISTINCT CF.SAMPLE_ID FROM CLINICAL_FACT CF
		 * WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID IN (SELECT distinct
		 * CF.PATIENT_ID FROM CLINICAL_FACT CF WHERE CF.DISEASE_ID = 2)) AND
		 * MF.PROBE_ID IN (SELECT P.PROBE_ID FROM PROBE P WHERE P.UID_1 IN
		 * (SELECT GS.UID_1 FROM GENE_SEQUENCE GS)) ORDER BY PROBE_ID
		 */

		/*
		 * 
		 * SELECT MF.EXPRESSION, MF.PROBE_ID FROM MICROARRAY_FACT MF, PROBE P,
		 * CLINICAL_FACT CF WHERE MF.SAMPLE_ID = CF.SAMPLE_ID AND CF.SAMPLE_ID
		 * IS NOT NULL AND CF.PATIENT_ID IN (SELECT distinct CF1.PATIENT_ID FROM
		 * CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID = 2) AND
		 * MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN(SELECT UID_1 FROM GENE_SEQUENCE
		 * ) order by MF.PROBE_ID, CF.PATIENT_ID
		 */

		rs1 = s1.executeQuery("SELECT MF.EXPRESSION, P.UID_1 FROM MICROARRAY_FACT MF, PROBE P, CLINICAL_FACT CF WHERE MF.SAMPLE_ID = CF.SAMPLE_ID AND CF.SAMPLE_ID <> 0 AND CF.PATIENT_ID IN (SELECT distinct CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID = 2) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1  IN(SELECT UID_1 FROM GENE_SEQUENCE ) ORDER BY P.UID_1");
		rs2 = s2.executeQuery("SELECT MF.EXPRESSION, P.UID_1 FROM MICROARRAY_FACT MF, PROBE P, CLINICAL_FACT CF WHERE MF.SAMPLE_ID = CF.SAMPLE_ID AND CF.SAMPLE_ID <> 0 AND CF.PATIENT_ID IN (SELECT distinct CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID <> 2) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1  IN(SELECT UID_1 FROM GENE_SEQUENCE ) ORDER BY P.UID_1");
		double all[][] = new double[1000][13];
		double aml[][] = new double[1000][40];
		int i = 0, j = 0;
		String uids[] = new String[1000];
		while (rs1.next()) {

			all[i][j] = Double.parseDouble(rs1.getString(1));
			j++;
			if (j % 13 == 0 && j > 0) {
				j = 0;
				uids[i] = rs1.getString(2);
				i++;

			}

		}

		i = 0;
		j = 0;
		while (rs2.next()) {

			aml[i][j] = Double.parseDouble(rs2.getString(1));

			j++;
			if (j % 40 == 0 && j > 0) {
				i++;
				j = 0;
			}
		}

		int zxc = 0;
		TTest tt = new TTest();
		double finalres[] = new double[1000];
		for (int k = 0; k < all.length; k++) {

			finalres[zxc] = tt.tTest(all[k], aml[k]);

			zxc++;

		}
		
		
		ArrayList<String> final_uid= new ArrayList<String>();
		String test= "";

		for (int c = 0; c < (1000); c++)
			if (finalres[c] < 0.01) {
				test+="'" + uids[c]+ "'" + ",";
				final_uid.add(uids[c]);

			}

		//System.out.println(counttemp);
		
		String a= final_uid.toString();
		test=test.substring(0, test.length()-1);

		Statement s11 = con.createStatement();
		ResultSet rs11;

		
		
		Statement s3 = con.createStatement();
		ResultSet rs3;

		

		rs11 = s11.executeQuery("SELECT MF.EXPRESSION, CF.PATIENT_ID FROM MICROARRAY_FACT MF, PROBE P , CLINICAL_FACT CF WHERE MF.SAMPLE_ID = CF.SAMPLE_ID AND CF.SAMPLE_ID<> 0  AND CF.PATIENT_ID IN  (SELECT distinct CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID <> 2) AND MF.PROBE_ID= P.PROBE_ID AND P.UID_1 IN"
				+ "(" + test + ")" +"ORDER BY P.UID_1");
		
		rs3 = s3.executeQuery("SELECT MF.EXPRESSION, CF.PATIENT_ID FROM MICROARRAY_FACT MF, PROBE P , CLINICAL_FACT CF WHERE MF.SAMPLE_ID = CF.SAMPLE_ID AND CF.SAMPLE_ID<> 0  AND CF.PATIENT_ID IN  (SELECT distinct CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID = 2) AND MF.PROBE_ID= P.PROBE_ID AND P.UID_1 IN"
				+ "(" + test + ")" +"ORDER BY P.UID_1");

		while (rs11.next()) {
			if(comb.containsKey(rs11.getString(2))){
				ArrayList<String> arr = comb.get(rs11.getString(2));
				arr.add(rs11.getString(1));
				comb.put(rs11.getString(2), arr);
			}
			else{
				ArrayList<String> abc= new ArrayList<String>();
				abc.add(rs11.getString(1));
				comb.put(rs11.getString(2), abc);
			}
			
		}
		
		while (rs3.next()) {
			//pat.add(rs1.getString(1));
			//exp.add(rs1.getString(2));
			if(comb_all.containsKey(rs3.getString(2))){
				ArrayList<String> arr = comb_all.get(rs3.getString(2));
				arr.add(rs3.getString(1));
				comb_all.put(rs3.getString(2), arr);
			}
			else{
				ArrayList<String> abc= new ArrayList<String>();
				abc.add(rs3.getString(1));
				comb_all.put(rs3.getString(2), abc);
			}
			
		}

		query3_2 o=new query3_2();
		for(int x = 1 ; x <=5 ; x++){
			o.last_query(comb, comb_all, test, ("TEST"+x));
		}

	}

	
}
