package example.alchemyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Servlet implementation class Sentiment
 */
@WebServlet("/Sentiment")
public class Sentiment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String APIKEY = "<APIKEY>";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Sentiment() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		//Map<String, String[]> map = request.getParameterMap();
		out.println("<html>");
        out.println("<head>");
        out.println("<title>Sentiment Analysis Results</title>");
        out.println("</head>");
        out.println("<body>");
        String result = makeRestCall("http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey="+APIKEY+
        		"&outputMode=json&showSourceText=1" + 
        		"&text="+URLEncoder.encode(request.getParameter("text")));
        JsonParser parser = new JsonParser();
        JsonElement tree = parser.parse(result);
        if(tree.getAsJsonObject().get("status").getAsString().equalsIgnoreCase("OK")){
        	JsonObject sentiment = tree.getAsJsonObject().get("docSentiment").getAsJsonObject();
        	String sentimentType = sentiment.get("type").getAsString();
        	out.println("<h1> Sentiment of the text is " + sentimentType + "</h1>");
        	if(!sentimentType.equalsIgnoreCase("neutral")){
        		out.println("with confidance score = "+ sentiment.get("score").getAsDouble());
        	}
        }else{
        	out.println("<h1> It looks like devil is at work !! chekc back later</h1>");
        }
        out.println("</body>");
        //out.println(result);
        out.println("</html>");
        out.close();
	}
	
	public static String makeRestCall(String urlString){
		URL url;
		HttpURLConnection conn=null;
		StringBuffer result = new StringBuffer();
		try {
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String line;

			while ((line = br.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		conn.disconnect();
		return result.toString();
	}

}
