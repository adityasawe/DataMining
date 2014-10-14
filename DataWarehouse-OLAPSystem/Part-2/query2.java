

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class query2{

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


		String query2="SELECT DISTINCT D.DRUG_TYPE FROM DRUG D, CLINICAL_FACT C, DISEASE DI, PATIENT P WHERE D.DRUG_ID=C.DRUG_ID AND DI.DISEASE_ID=C.DISEASE_ID AND P.PATIENT_ID=C.PATIENT_ID AND DI.DESCRIPTION= 'tumor'";      
		Statement s1 = con.createStatement();


		ResultSet rs= s1.executeQuery(query2);

		System.out.println("Type of drugs for patients with tumor: ");
		int count=0;
		while(rs.next())
		{
			String temp= rs.getString(1); 
			System.out.println(temp);
			count++;
		}
		System.out.println("Total count: "+count);





	}



}
