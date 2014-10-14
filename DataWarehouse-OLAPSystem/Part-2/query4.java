
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class query4{

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


		String ab="SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID "+
				" IN (SELECT DISTINCT CF.PATIENT_ID"+ 
				" FROM CLINICAL_FACT CF, DISEASE D"+
				" WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='ALL' ))"+
				" AND MF.PROBE_ID=P.PROBE_ID"+
				" AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF"+
				" WHERE GF.GO_ID=0012502)";
		double blank=0;
		int count=0;
		Statement s = con.createStatement();
		ResultSet rs= s.executeQuery(ab);
		Statement s2 = con.createStatement();
		ResultSet rs2= s2.executeQuery(ab);
		while(rs.next()){
			blank+=Double.parseDouble(rs.getString(1));  
			count++;
		}

		double mean=blank/count;
		double variance=0;
		while(rs2.next()){
			variance+=Math.pow((Double.parseDouble(rs2.getString(1))-mean),2);  
		}


		variance=variance/(count-1);

		ab="SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID NOT IN (SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='ALL' )) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF WHERE GF.GO_ID=0012502)";       

		Statement s3 = con.createStatement();
		ResultSet rs3= s3.executeQuery(ab);
		Statement s4 = con.createStatement();
		ResultSet rs4= s4.executeQuery(ab);

		blank=0;
		int count2=0;

		while(rs3.next()){
			blank+=Double.parseDouble(rs3.getString(1));  
			count2++;
		}

		double mean2=blank/count2;
		double variance2=0;
		while(rs4.next()){
			variance2+=Math.pow((Double.parseDouble(rs4.getString(1))-mean2),2);  
		}


		variance2=variance2/(count2-1);
		Double t=(mean-mean2)/(Math.sqrt((variance/count)+(variance2/count2)));

		System.out.println("t-statistics: "+t);

	}

}
