
package project15619;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Clob;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class Q5MySql implements HttpHandler {

    private DataSource[] dataSourceArray = new DataSource[6];

    
    private final String[] urlArray = new String[]{"jdbc:mysql://ec2-54-173-175-30.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-190-247.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-65-136.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-174-4-253.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-229-148.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-165-205-187.compute-1.amazonaws.com/15619project"};

    private AtomicInteger ind = new AtomicInteger(0);


    public Q5MySql() {
        
	for (int i = 0; i <=5; i++) {

	    GenericObjectPool connectionPool = new GenericObjectPool();
	    connectionPool.setMaxActive(100);
	    connectionPool.setMaxIdle(100);
	    ConnectionFactory connectionFactory = 
		new DriverManagerConnectionFactory(urlArray[i], "ccteam", "password");
	
	    new PoolableConnectionFactory(connectionFactory, connectionPool, 
					  null, null, false, true);
	
	    this.dataSourceArray[i] = new PoolingDataSource(connectionPool);
	}
	
	
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception{

	if (exchange.isInIoThread()) {
	    exchange.dispatch(this);
	    return;
	}

	// 
	//long start = System.currentTimeMillis();

	//
	DataSource dataSource = null;
	int temp = this.ind.incrementAndGet();
	int index = Math.abs(temp % 6);
	dataSource = this.dataSourceArray[index];

	// 
	String team_accounts = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n";	
	String userIdA = exchange.getQueryParameters().get("m").peek();
	String userIdB = exchange.getQueryParameters().get("n").peek();
	
	StringBuilder result = new StringBuilder();

	ResultSet resultSet = null;
	PreparedStatement statement = null;
	Connection connection = null;	

	int[] scoreA = {0,0,0,0};
	int[] scoreB = {0,0,0,0};

	try {

	    // select table
	    long userIdALong = Long.parseLong(userIdA);
	    long userIdBLong = Long.parseLong(userIdB);

	    connection = dataSource.getConnection();
	    statement = connection.prepareStatement("select score1, score2, score3, total from q5 where user_id=?");

	    // user A

	    statement.setLong(1, userIdALong);
	    resultSet = statement.executeQuery();

	    while (resultSet.next()) {
		scoreA[0] = resultSet.getInt(1);
		scoreA[1] = resultSet.getInt(2);
		scoreA[2] = resultSet.getInt(3);
		scoreA[3] = resultSet.getInt(4);
	    }
	    
	    // user B
	    statement.setLong(1, userIdBLong);
	    resultSet = statement.executeQuery();

	    while (resultSet.next()) {
		scoreB[0] = resultSet.getInt(1);
		scoreB[1] = resultSet.getInt(2);
		scoreB[2] = resultSet.getInt(3);
		scoreB[3] = resultSet.getInt(4);
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (resultSet != null) try {resultSet.close();} catch(Exception e) {}
	    if (statement != null) try {statement.close();} catch(Exception e) {}
	    if (connection != null) try {connection.close();} catch(Exception e) {}
	}


	// compare A and B
	String header = String.format("%s\t%s\tWINNER\n", userIdA, userIdB);
	
	String winner0 = "X";
	if (scoreA[0] > scoreB[0])
	    winner0 = userIdA;
	else if (scoreA[0] < scoreB[0])
	    winner0 = userIdB;
	String row0 = String.format("%d\t%d\t%s\n", scoreA[0], scoreB[0], winner0);
	
	String winner1 = "X";
	if (scoreA[1] > scoreB[1])
	    winner1 = userIdA;
	else if (scoreA[1] < scoreB[1])
	    winner1 = userIdB;
	String row1 = String.format("%d\t%d\t%s\n", scoreA[1], scoreB[1], winner1);

	String winner2 = "X";
	if (scoreA[2] > scoreB[2])
	    winner2 = userIdA;
	else if (scoreA[2] < scoreB[2])
	    winner2 = userIdB;
	String row2 = String.format("%d\t%d\t%s\n", scoreA[2], scoreB[2], winner2);

	String winner3 = "X";
	if (scoreA[3] > scoreB[3])
	    winner3 = userIdA;
	else if (scoreA[3] < scoreB[3])
	    winner3 = userIdB;
	String row3 = String.format("%d\t%d\t%s\n", scoreA[3], scoreB[3], winner3);

	result.append(team_accounts);
	result.append(header);
	result.append(row0);
	result.append(row1);
	result.append(row2);
	result.append(row3);

	
	//
	//long duration = System.currentTimeMillis() - start;
	//System.out.printf("%d\t%d\n", index, duration);

	// send back
	exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	exchange.getResponseSender().send(result.toString());
	
	exchange.endExchange();
	
    }

}

