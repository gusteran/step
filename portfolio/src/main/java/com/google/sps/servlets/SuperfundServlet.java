

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Scanner;

import com.google.sps.data.SuperfundSite;

/** Servlet that returns a list of all Superfund Sites */
@WebServlet("/superfund")
public class SuperfundServlet extends HttpServlet {

    private ArrayList<SuperfundSite> sites;

    //this is the url to the tab seperated sheet of all the superfund sites
    private static final String superfundSitesSheet = "/WEB-INF/superfund.tsv";

    @Override
    public void init() {
        sites = new ArrayList<>();

        Scanner scanner = new Scanner(getServletContext().getResourceAsStream(superfundSitesSheet));
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] cells = line.split("\t");
            String name= cells[0];
            double score= Double.parseDouble(cells[1]);
            String state= cells[5];
            String city= cells[6];
            String county= cells[7];
            String status= cells[8];
            double lat = Double.parseDouble(cells[9]);
            double lng = Double.parseDouble(cells[10]);

            sites.add(new SuperfundSite(name, score, state, city, county, status, lat, lng));
        }
        scanner.close();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(sites));
    }

}