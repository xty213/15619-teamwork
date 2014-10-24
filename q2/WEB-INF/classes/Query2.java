
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import java.math.BigInteger;

import javax.naming.*;
import javax.sql.*;



public class Query2 extends HttpServlet {

    private DataSource dataSource1;
    private DataSource dataSource2;
    private DataSource dataSource3;    

    @Override
    public void init() throws ServletException{
	try {

	    Context initContext = new InitialContext();
	    Context envContext = (Context)initContext.lookup("java:/comp/env");
	    dataSource1 = (DataSource)envContext.lookup("jdbc/mysql1");
	    dataSource2 = (DataSource)envContext.lookup("jdbc/mysql2");
	    dataSource3 = (DataSource)envContext.lookup("jdbc/mysql3");

	} catch (NamingException e) {
	    e.printStackTrace();
	} 
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

	try {

	    StringBuilder str = new StringBuilder(1024);
	    
	    // str
	    String team_id = "ccteam, ";
	    String aws_account_id1 = "6028-0384-4018, ";
	    String aws_account_id2 = "4756-0270-5080, ";
	    String aws_account_id3 = "1894-1779-2072\n";

	    str.append(team_id);
	    str.append(aws_account_id1);
	    str.append(aws_account_id2);
	    str.append(aws_account_id3);

	    // query
	    String userId = request.getParameter("userid");
	    String time = request.getParameter("tweet_time");

	    if (userId == null || time == null) {
		PrintWriter out = response.getWriter();
		out.printf("");
		return;
	    }
		
	    time = time.replace(' ', '+');

	    // random query
	    /*
	    int len = Math.min(userId.length(), time.length());
	    Random rand = new Random();
	    int randPos = rand.nextInt(len-1);
	    char randVal = (char)('0' + rand.nextInt(9));
	    
	    char[] randTime = time.toCharArray();
	    randTime[randPos] = randVal;
	    time = String.valueOf(randTime);
	    
	    char[] randId = userId.toCharArray();
	    randId[randPos] = randVal;
	    userId = String.valueOf(randId);
	    */

	    // database
	    String query = "select tweet_id, score, censored_text from tweets where user_id=? and `time`=?";

	    for (int i = 1; i <=3; i++) {
		
		DataSource dataSource = null;
		if (i == 1)
		    dataSource = dataSource1;
		else if (i == 2)
		    dataSource = dataSource2;
		else
		    dataSource = dataSource3;

		ResultSet resultSet = null;
		Connection connection = null;
		connection = dataSource.getConnection();
		
		PreparedStatement preparedQuery = connection.prepareStatement(query);
		preparedQuery.setLong(1, Long.parseLong(userId));
		preparedQuery.setString(2, time);

		resultSet = preparedQuery.executeQuery();
	    
		//String query = "select * from tweets where user_id="+ userId + " and time='" + time + "'";
		//statement = connection.createStatement();
		//resultSet = statement.executeQuery(query);
	    
		while (resultSet.next()) {
		    str.append(String.valueOf(resultSet.getLong(1)));
		    str.append(':');

		    str.append(String.valueOf(resultSet.getInt(2)));
		    str.append(':');
		
		    str.append(resultSet.getString(3));
		}

		resultSet.close();
		preparedQuery.close();
		connection.close();
		
	    }

	    // one more \n
	    str.append('\n');

	    // 
	    PrintWriter out = response.getWriter();
	    out.printf(str.toString());

	    
	} catch (Exception e) {
	    try {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		String s = writer.toString();

		PrintWriter out = response.getWriter();
		out.printf(s);
	    } catch (Exception e2) {
	    
	    }

	} 
    }


}
