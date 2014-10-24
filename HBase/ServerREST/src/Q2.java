import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.misc.BASE64Decoder;

public class Q2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String TWEETS_DB_URL = "http://ec2-54-173-66-253.compute-1.amazonaws.com:8080/tweets/";
	private static final String Q2_FORMAT_STR = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n%s:%s:%s\n";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// get the response writer
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
		
		// prepare parameters
		String userId = request.getParameter("userid");
		String time = request.getParameter("tweet_time");
		if (userId == null || time == null) {
			return;
		}
		time = time.replace(' ', '+');

		// connect to the REST server
		URL url = new URL(TWEETS_DB_URL + URLEncoder.encode(String.format("%s %s", userId, time), "UTF-8"));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// try to get the response
		if (conn.getResponseCode() == 200) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(conn.getInputStream());
				NodeList cells = doc.getElementsByTagName("Cell");
				BASE64Decoder decoder = new BASE64Decoder();
				writer.print(String.format(
								Q2_FORMAT_STR,
								new String(decoder.decodeBuffer(cells.item(2).getFirstChild().getNodeValue()), "UTF-8"),
								new String(decoder.decodeBuffer(cells.item(1).getFirstChild().getNodeValue()), "UTF-8"),
								new String(decoder.decodeBuffer(cells.item(0).getFirstChild().getNodeValue()), "UTF-8") ));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}

		conn.disconnect();
		writer.flush();
		return;
	}
}
