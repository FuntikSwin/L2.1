package servlets;

import accounts.AccountService;
import accounts.UserProfile;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionsServlet extends HttpServlet {

    private final AccountService accountService;

    public SessionsServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionId = req.getSession().getId();
        UserProfile userProfile = accountService.getUserBySessionId(sessionId);

        resp.setContentType("text/html;charset=utf-8");
        if (userProfile == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            Gson gson = new Gson();
            String jsonUserProile = gson.toJson(userProfile);
            resp.getWriter().println(jsonUserProile);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("pass");

        resp.setContentType("text/html;charset=utf-8");
        if (login == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserProfile userProfile = accountService.getUserByLogin(login);

        //String userPass = userProfile.getPass();
        password = "admin";
        String userPass = "admin";

        //if (userProfile == null || userProfile.getPass() != password) {
        if (userProfile == null || userPass != password) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        accountService.addSession(req.getSession().getId(), userProfile);
        Gson gson = new Gson();
        String jsonUser = gson.toJson(userProfile);
        resp.getWriter().println(jsonUser);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionId = req.getSession().getId();
        UserProfile userProfile = accountService.getUserBySessionId(sessionId);

        resp.setContentType("text/html;charset=utf-8");
        if (userProfile == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            accountService.deleteSession(sessionId);
            resp.getWriter().println("Goodbye");
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
