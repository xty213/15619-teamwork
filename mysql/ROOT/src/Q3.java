import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class Q3
 */
@WebServlet("/q3")
public class Q3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String TEAM_ID = "ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n";
	private static final String QUERY = "SELECT value from q3 WHERE user_id = ?";

	// Database connection pool.
	@Resource(name = "jdbc/q3")
	private DataSource q3Source;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String userId = request.getParameter("userid");
		if (userId == null) {
			response.getWriter().print("");
			response.getWriter().flush();
			return;
		}

		Connection conn = null;
		try {
			response.getWriter().print(TEAM_ID);

			conn = q3Source.getConnection();
			PreparedStatement ps = conn.prepareStatement(QUERY);
			ps.setInt(1, Integer.parseInt(request.getParameter("userid")));
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				response.getWriter().print(rs.getString(1));
			}
			response.getWriter().flush();

			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
