import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class query6two{

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



		Statement s1 = con.createStatement();
		ResultSet rs1;


		Statement s2 = con.createStatement();
		ResultSet rs2;

		rs1=s1.executeQuery( "SELECT CLINICAL_FACT.PATIENT_ID, MICROARRAY_FACT.EXPRESSION FROM MICROARRAY_FACT INNER JOIN CLINICAL_FACT on MICROARRAY_FACT.SAMPLE_ID = CLINICAL_FACT.SAMPLE_ID WHERE MICROARRAY_FACT.PROBE_ID IN (SELECT P.PROBE_ID FROM PROBE P INNER JOIN GENE_FACT ON P.UID_1=GENE_FACT.UID_1 WHERE GENE_FACT.GO_ID = 0007154 ) and CLINICAL_FACT.SAMPLE_ID IS NOT NULL AND CLINICAL_FACT.PATIENT_ID IN (SELECT CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID=2) ORDER BY CLINICAL_FACT.PATIENT_ID, MICROARRAY_FACT.PROBE_ID ");
		rs2=s2.executeQuery( "SELECT CLINICAL_FACT.PATIENT_ID, MICROARRAY_FACT.EXPRESSION FROM MICROARRAY_FACT INNER JOIN CLINICAL_FACT on MICROARRAY_FACT.SAMPLE_ID = CLINICAL_FACT.SAMPLE_ID WHERE MICROARRAY_FACT.PROBE_ID IN (SELECT P.PROBE_ID FROM PROBE P INNER JOIN GENE_FACT ON P.UID_1=GENE_FACT.UID_1 WHERE GENE_FACT.GO_ID = 0007154 ) and CLINICAL_FACT.SAMPLE_ID IS NOT NULL AND CLINICAL_FACT.PATIENT_ID IN (SELECT CF1.PATIENT_ID FROM CLINICAL_FACT CF1 WHERE CF1.DISEASE_ID=3) ORDER BY CLINICAL_FACT.PATIENT_ID, MICROARRAY_FACT.PROBE_ID");	

		double all[][]=new double[13][24];
		double aml[][]=new double[14][24];
		int i=0,j=0;
		while(rs1.next()){

			all[i][j]=Double.parseDouble(rs1.getString(2));
			j++;
			if(j%24==0 && j>0)
			{
				i++;
				j=0;
			}
		}  

		i=0;j=0;
		while(rs2.next()){

			aml[i][j]=Double.parseDouble(rs2.getString(2));

			j++;
			if(j%24==0 && j>0)
			{
				i++;
				j=0;
			}
		}

		double sum=0;
		for(int k=0;k<all.length;k++)
		{
			for(int l=0;l<aml.length;l++)
			{

				double temp=calculatecorrelation(all[k],aml[l]);
				sum+=temp;

			}
		}

		System.out.println("Pearson's Correlation Coefficient: "+ (sum/((all.length)*(aml.length))));


	}

	public static double calculatecorrelation(double a[], double b[])
	{
		double meana=0;
		double meanb=0;
		for(int i=0;i<a.length;i++)
		{
			meana+=a[i];
			meanb+=b[i];
		}
		meana=meana/a.length;
		meanb=meanb/a.length;
		double con=0;
		double sa=0;
		double sb=0;
		for(int i=0;i<a.length;i++)
		{
			con+=(meana-a[i])*(meanb-b[i]);	
			sa+=Math.pow((meana-a[i]),2);
			sb+=Math.pow((meanb-b[i]),2);
		}

		//con=con/(a.length-1);
		//sa=Math.sqrt(sa/(a.length-1));
		//sb=Math.sqrt(sb/(a.length-1));

		sa=Math.sqrt(sa);
		sb=Math.sqrt(sb);

		return (con/(sa*sb));
	}

}

