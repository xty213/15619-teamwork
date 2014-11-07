import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Q1
 */
@WebServlet("/q1")
public class Q1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final BigInteger X = new BigInteger(
			"6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153");
	private static final String TEAM_ID = "\nccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("key") == null) {
			response.getWriter().print("");
			response.getWriter().flush();
			return;
		}

		try {
			StringBuilder str = new StringBuilder(1024);

			BigInteger XY = new BigInteger(request.getParameter("key"));
			BigInteger Y = XY.divide(X);
			str.append(Y.toString());

			str.append(TEAM_ID);

			str.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(Calendar.getInstance().getTime()));

			str.append('\n');

			response.getWriter().print(str.toString());
			response.getWriter().flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
