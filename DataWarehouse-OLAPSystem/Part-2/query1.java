

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class query1{

	public static void main(String args[]) throws java.sql.SQLException, java.io.IOException{
		try {
			Class.forName ("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException e){
			System.out.println("Could not load the driver");
			System.exit(0);
		}

		System.out.print("Enter your username: ");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String user = br.readLine();

		System.out.print("Enter your password: ");
		String passwd = br.readLine();


		Connection con = DriverManager.getConnection(
				"jdbc:oracle:thin:"+user+"/"+passwd+"@aos.acsu.buffalo.edu:1521/aos.buffalo.edu"
				);


		String[] query1= {"Select count(C.PATIENT_ID) FROM CLINICAL_FACT C, DISEASE D WHERE C.DISEASE_ID= D.DISEASE_ID AND D.DESCRIPTION= 'tumor'",
				"Select count(C.PATIENT_ID) FROM CLINICAL_FACT C, DISEASE D WHERE C.DISEASE_ID= D.DISEASE_ID AND D.TYPE= 'leukemia'",
		"Select count(C.PATIENT_ID) FROM CLINICAL_FACT C, DISEASE D WHERE C.DISEASE_ID= D.DISEASE_ID AND D.NAME='ALL'"};

		Statement s1 = con.createStatement();
		Statement s2 = con.createStatement();
		Statement s3 = con.createStatement();


		ResultSet rs= s1.executeQuery(query1[0]);
		ResultSet rs1= s2.executeQuery(query1[1]);
		ResultSet rs2= s3.executeQuery(query1[2]);
		int res1=0,res2=0,res3=0;
		while(rs.next())
		{
			res1=Integer.parseInt(rs.getString(1)); 
		}

		while(rs1.next())
		{
			res2=Integer.parseInt(rs1.getString(1)); 
		}

		while(rs2.next())
		{
			res3=Integer.parseInt(rs2.getString(1)); 
		}



		System.out.println("Number of Patients with tumor(disease description): "+res1);
		System.out.println("Number of Patients with leukemia(disease type): "+res2);
		System.out.println("Number of Patients with ALL(disease name): "+res3);

	}



}
