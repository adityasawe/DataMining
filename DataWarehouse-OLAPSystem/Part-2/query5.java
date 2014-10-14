
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class query5{

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



		String query1= "SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID IN (SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='ALL' )) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF WHERE GF.GO_ID=0007154)";                    

		String query2=" SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID IN (SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='AML' )) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF WHERE GF.GO_ID=0007154)";

		String query3= "SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID IN (SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='Colon tumor' )) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF  WHERE GF.GO_ID=0007154)";

		String query4= "SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID IN (SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='Breast tumor' )) AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF WHERE GF.GO_ID=0007154)";


		ArrayList<Double> q1=new ArrayList<Double>();
		ArrayList<Double> q2=new ArrayList<Double>();
		ArrayList<Double> q3=new ArrayList<Double>();
		ArrayList<Double> q4=new ArrayList<Double>();

		Statement s1 = con.createStatement();
		Statement s2 = con.createStatement();
		Statement s3 = con.createStatement();
		Statement s4 = con.createStatement();

		ResultSet rs1= s1.executeQuery(query1);
		ResultSet rs2= s2.executeQuery(query2);
		ResultSet rs3= s3.executeQuery(query3);
		ResultSet rs4= s4.executeQuery(query4);



		while(rs1.next()){

			q1.add(Double.parseDouble(rs1.getString(1)));  

		}
		while(rs2.next()){
			q2.add(Double.parseDouble(rs2.getString(1)));  
		}
		while(rs3.next()){
			q3.add(Double.parseDouble(rs3.getString(1)));  

		}
		while(rs4.next()){
			q4.add(Double.parseDouble(rs4.getString(1)));  

		}

		Iterator<Double> it=q1.listIterator();

		double mean[]=new double[4];
		double globalmean=0;
		int size[]=new int[4];


		while(it.hasNext())
		{
			mean[0]+=it.next();

		}
		globalmean=globalmean+mean[0];
		mean[0]=mean[0]/q1.size();

		it=q2.listIterator();
		while(it.hasNext())
		{
			mean[1]+=it.next();
		}
		globalmean=globalmean+mean[1];
		mean[1]=mean[1]/q2.size();

		it=q3.listIterator();
		while(it.hasNext())
		{
			mean[2]+=it.next();
		}
		globalmean=globalmean+mean[2];
		mean[2]=mean[2]/q3.size();

		it=q4.listIterator();
		while(it.hasNext())
		{
			mean[3]+=it.next();
		}
		globalmean=globalmean+mean[3];
		mean[3]=mean[3]/q4.size();

		int sizeall=q1.size()+q2.size()+q3.size()+q4.size();
		size[0]=q1.size();
		size[1]=q2.size();
		size[2]=q3.size();
		size[3]=q4.size();


		globalmean=globalmean/(sizeall);

		double dss=0;

		for(int i=0;i<4;i++)
			for(int j=0;j<size[i];j++)
			{

				dss+=Math.pow((globalmean-mean[i]),2);
			}


		it=q1.listIterator();

		double ss=0;
		while(it.hasNext())
		{
			ss+=Math.pow((mean[0]-it.next()),2);

		}
		it=q2.listIterator();
		while(it.hasNext())
		{
			ss+=Math.pow((mean[1]-it.next()),2);
		}
		it=q3.listIterator();
		while(it.hasNext())
		{
			ss+=Math.pow((mean[2]-it.next()),2);
		}
		it=q4.listIterator();
		while(it.hasNext())
		{
			ss+=Math.pow((mean[3]-it.next()),2);
		}


		double msd=dss/3;
		double mse=ss/(sizeall-4);

		double f=msd/mse;
		System.out.println("F-Statistics: "+f);
	}



}
