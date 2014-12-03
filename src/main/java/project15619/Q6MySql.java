
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


public class Q6MySql implements HttpHandler {

    private DataSource[] dataSourceArray = new DataSource[6];

    private final String[] urlArray = new String[]{"jdbc:mysql://ec2-54-173-175-30.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-190-247.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-65-136.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-174-4-253.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-229-148.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-165-205-187.compute-1.amazonaws.com/15619project"};


    private BigInteger modular = new BigInteger("9223372036854775808");

    private AtomicInteger ind = new AtomicInteger(0);
    

    public Q6MySql() {

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
	String team_accounts = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072";	
	String userIdA = exchange.getQueryParameters().get("m").peek();
	String userIdB = exchange.getQueryParameters().get("n").peek();
	String result = null;

	ResultSet resultSet = null;
	PreparedStatement statementA = null;
	PreparedStatement statementB = null;
	Connection connection = null;	

	int valueA = 0;
	int valueB = 0;

	try {

	    connection = dataSource.getConnection();
	    
	    // user A
	    statementA = connection.prepareStatement("select value from q6 where user_id<? order by user_id desc limit 1");
	    
	    long userIdLongA = Long.parseLong(userIdA);
	    statementA.setLong(1, userIdLongA);

	    resultSet = statementA.executeQuery(); 
	    
	    while (resultSet.next()) {
		valueA = resultSet.getInt(1);
	    }


	    // user B
	    statementB = connection.prepareStatement("select value from q6 where user_id<=? order by user_id desc limit 1");

	    long userIdLongB = Long.parseLong(userIdB);
	    statementB.setLong(1, userIdLongB);

	    resultSet = statementB.executeQuery();
	    while (resultSet.next()) {
		valueB = resultSet.getInt(1);
	    }

	    	   	
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (resultSet != null) try {resultSet.close();} catch(Exception e) {}
	    if (statementA != null) try {statementA.close();} catch(Exception e) {}
	    if (statementB != null) try {statementB.close();} catch(Exception e) {}
	    if (connection != null) try {connection.close();} catch(Exception e) {}
	}

	int sum = valueB - valueA;
	if (sum < 0)
	    sum = 0;

	result = String.format("%s\n%d\n", team_accounts, sum);

	//
	//long duration = System.currentTimeMillis() - start;
	//System.out.printf("%d\t%d\n", index, duration);

	// send back
	exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	exchange.getResponseSender().send(result);
	
	exchange.endExchange();
	
    }

}

