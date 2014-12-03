package project15619;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Q1Handler implements HttpHandler {

    private BigInteger X = new BigInteger("6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153");

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception{
	
	/*
	if (exchange.isInIoThread()) {
	    exchange.dispatch(this);
	    return;
	    }*/

	// get response key
	Deque<String> values = exchange.getQueryParameters().get("key");
	BigInteger XY = new BigInteger(values.peek());
	String YStr= XY.divide(X).toString();

	// other 
	String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

	String response = String.format("%s\nccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n%s\n\n", YStr, date);
	
	
	// send back
	exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	exchange.getResponseSender().send(response);
	
	exchange.endExchange();

    }

}
