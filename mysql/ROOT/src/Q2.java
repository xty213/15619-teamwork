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
 * Servlet implementation class Q1
 */
@WebServlet("/q1")
public class Q2 extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TEAM_ID = "\nccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n";
    private static final String QUERY = "SELECT tweet_id, score, censored_txt from q2 WHERE user_id = ? AND timestamp = ?";

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

        if (request.getParameter("userid") == null || request.getParameter("tweet_time") == null) {
            response.getWriter().print("");
            response.getWriter().flush();
            return;
        }

        Connection conn = null;
        try {
            response.getWriter().print(TEAM_ID);
            conn = q3Source.getConnection();
            PreparedStatement ps = conn.prepareStatement(QUERY);
            ps.setLong(1, Long.parseLong(request.getParameter("userid")));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(request.getParameter("tweet_time"));
            long timestamp = date.getTime() / 1000;
            ps.setLong(2, timestamp);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                response.getWriter().print(rs.getString(1) + ":" + rs.getString(2) + ":" + rs.getString(3) + "\n");
            }
            response.getWriter().flush();
            rs.close();
            ps.close();

        } catch (Exception e) {
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
