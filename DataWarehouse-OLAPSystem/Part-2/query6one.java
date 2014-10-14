
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class query6one{

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

      
      String query1= "SELECT DISTINCT CF.PATIENT_ID FROM CLINICAL_FACT CF, DISEASE D WHERE D.DISEASE_ID=CF.DISEASE_ID AND D.NAME='ALL'";                    

       
      ArrayList<Integer> q1=new ArrayList<Integer>();
      Statement s1 = con.createStatement();
      
      ResultSet rs1= s1.executeQuery(query1);
    
     
      while(rs1.next()){
    	
    	  q1.add(Integer.parseInt(rs1.getString(1)));  
    	  
      }
 Statement[] s2=new Statement[q1.size()];
      
      ResultSet [] rs=new ResultSet[q1.size()];
      
      ArrayList<ArrayList<Integer>> grid =  new ArrayList<ArrayList<Integer>>();    

      Iterator<Integer> it=q1.listIterator();
      
      int z=0;
      while(it.hasNext())
      {	  s2[z]=con.createStatement();
    	  rs[z]=s2[z].executeQuery( "SELECT MF.EXPRESSION FROM MICROARRAY_FACT MF , PROBE P WHERE MF.SAMPLE_ID IN (SELECT CF.SAMPLE_ID FROM CLINICAL_FACT CF WHERE CF.SAMPLE_ID IS NOT NULL AND CF.PATIENT_ID="+it.next()+" AND MF.PROBE_ID=P.PROBE_ID AND P.UID_1 IN (SELECT GF.UID_1 FROM GENE_FACT GF WHERE GF.GO_ID=0007154))");
    	  ArrayList<Integer> temp=new ArrayList<Integer>();
    	  while(rs[z].next()){
    	    	
        	  temp.add(Integer.parseInt(rs[z].getString(1)));  
        	  
          }  
    	  grid.add(temp);
      }
      int ol=0;
      int il=0;
      double finalmat[][]=new double[grid.get(0).size()][grid.size()];
      for(ArrayList<Integer> temp : grid)
      {
    	  
    	 for(Integer a : temp)
    	 {
    	 finalmat[il][ol]=a;
    	 il++;
    	 }
    	 il=0;
    	 ol++;
    		 
      }
      
      PearsonsCorrelation pc=new PearsonsCorrelation();

      RealMatrix rm=pc.computeCorrelationMatrix(finalmat);
      	
      double resultdata[][]=rm.getData();
      
      int tempcount=0;
      double totalsum=0;
      for(int i=0;i<13;i++)
    	  for(int j=i+1;j<13;j++)
    	  {
    		  totalsum+=resultdata[i][j];
    	  tempcount++;
    	  }
      
      
      System.out.println("Pearson's correlation coefficient: "+(totalsum/tempcount));
      
      
}

}

