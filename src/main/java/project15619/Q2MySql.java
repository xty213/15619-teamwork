
package project15619;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class Q2MySql implements HttpHandler {

    private DataSource[] dataSourceArray = new DataSource[6];

    private final String[] urlArray = new String[]{"jdbc:mysql://ec2-54-173-175-30.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-190-247.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-65-136.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-174-4-253.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-173-229-148.compute-1.amazonaws.com/15619project", "jdbc:mysql://ec2-54-165-205-187.compute-1.amazonaws.com/15619project"};

    private BigInteger modular = new BigInteger("9223372036854775808");

    private AtomicInteger ind = new AtomicInteger(0);
    
    public Q2MySql(){

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
	StringBuilder result = new StringBuilder(1024);
	String team_accounts = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n";	
	result.append(team_accounts);
	
	String userId = exchange.getQueryParameters().get("userid").peek();
	String time = exchange.getQueryParameters().get("tweet_time").peek();
	
	// query
	String query = "select value from q2 where hashed_key=?";
	String hashed_key_str = time.replaceAll("[^0-9]", "") + userId;
	Long hashed_key = (new BigInteger(hashed_key_str).mod(this.modular)).longValue();

	ResultSet resultSet = null;
	PreparedStatement statement = null;
	Connection connection = null;	
	
	try {
	    connection = dataSource.getConnection();

	    statement = connection.prepareStatement(query);
	    statement.setLong(1, hashed_key);
	    
	    resultSet = statement.executeQuery();
	
	    while (resultSet.next()) {
		result.append(resultSet.getString(1));
	    }

	    result.append("\n");

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if(resultSet != null) try{resultSet.close();}catch(Exception e){}
	    if(statement != null) try{statement.close();}catch(Exception e){}
	    if (connection != null) try{connection.close();}catch(Exception e){}
	}

	//
	//long duration = System.currentTimeMillis() - start;
	//System.out.printf("%d\t%d\n", 
	//index, duration);
	
	
	// send back
	exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
	exchange.getResponseSender().send(result.toString());
	
	exchange.endExchange();

    }

}
