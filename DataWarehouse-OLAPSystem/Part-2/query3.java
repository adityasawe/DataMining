
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class query3{

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


		String query2="SELECT  M.SAMPLE_ID, M.EXPRESSION  FROM MICROARRAY_FACT M WHERE M.PROBE_ID IN (SELECT P.PROBE_ID FROM PROBE P WHERE P.UID_1 IN(SELECT GS.UID_1 FROM GENE_FACT GF , GENE_SEQUENCE GS, CLUSTER_1 C1 WHERE GF.UID_1=GS.UID_1 AND GF.CLUSTER_ID= C1.CLUSTER_ID AND C1.CLUSTER_ID=00002)) AND M.MEASUREMENT_UNIT_ID=001 AND M.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID  IN (SELECT DISTINCT CF.PATIENT_ID   FROM CLINICAL_FACT CF, DISEASE D  WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='ALL' ))";      
		Statement s1 = con.createStatement();


		ResultSet rs= s1.executeQuery(query2);
		int count=0;
		while(rs.next())
		{
			String temp= "Sample ID: "+rs.getString(1)+"\t"+"mRNA values(expression): "+rs.getString(2); 
			System.out.println(temp);
			count++;
		}
		System.out.println("Total count: "+count);





	}



}
