
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import java.math.BigInteger;

public class Query1 extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
	
	try {

	    StringBuilder str = new StringBuilder(1024);

	    // key
	    /*
	    double XY = Double.parseDouble(request.getParameter("key"));
	    double X = 6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153.0;
	    long Y = (long)Math.floor(XY/X);
	    */
	    BigInteger XY = new BigInteger(request.getParameter("key"));
	    BigInteger X = new BigInteger("6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153");
	    BigInteger Y = XY.divide(X);
	    String Ystr = Y.toString();
	    
	    str.append(Ystr);

	    // str
	    String team_id = "\nccteam, ";
	    String aws_account_id1 = "6028-0384-4018, ";
	    String aws_account_id2 = "4756-0270-5080, ";
	    String aws_account_id3 = "1894-1779-2072\n";

	    str.append(team_id);
	    str.append(aws_account_id1);
	    str.append(aws_account_id2);
	    str.append(aws_account_id3);
	    
	    // time stamp
	    Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
	    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
	    str.append(date);

	    // one more \n
	    str.append('\n');

	    // 
	    PrintWriter out = response.getWriter();
	    out.printf(str.toString());
	    
	    /*
	    request.setAttribute("key", str);
	    RequestDispatcher view = request.getRequestDispatcher("result.jsp");
	    view.forward(request, response);
	    */

	} catch (Exception e) {
	    
	}
    }


}
