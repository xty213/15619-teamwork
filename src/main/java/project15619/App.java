package project15619;

import io.undertow.Undertow;
import io.undertow.server.*;
import io.undertow.util.Headers;
import io.undertow.Handlers;
 
import io.undertow.UndertowOptions;
import org.xnio.Options;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(final String[] args) throws Exception{
        
	
	Undertow.builder()
	    .addHttpListener(80, "0.0.0.0")
	    //.addHttpListener(8888, "0.0.0.0")
	    //.setBufferSize(1024*16)
	    //.setIoThreads(Runtime.getRuntime().availableProcessors()*2)
	    //.setWorkerThreads(200)
	    //.setSocketOption(Options.BACKLOG, 10000)
	    .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false)	    
	    .setHandler(
			Handlers.path()
			.addPrefixPath("/q1", new Q1Handler())
			.addPrefixPath("/q2", new Q2MySql())
			.addPrefixPath("/q3", new Q3MySql())
			.addPrefixPath("/q4", new Q4MySql())
			.addPrefixPath("/q5", new Q5MySql())
			.addPrefixPath("/q6", new Q6MySql())
			)
	    .build()
	    .start();

    }
    
}
