package v1.rest;

import exceptions.Logging;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import v1.utils.config.ConfigProperties;

@Path("/resources")
public class Resources {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response createReseourcesDump() {
		JSONArray outArray = new JSONArray();
		try {
			String url_string = ConfigProperties.getPropertyParam("dump_res_web") + "resources-latest.json";
			URL url = new URL(url_string);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				// fill objects
				outArray = (JSONArray) new JSONParser().parse(response.toString());
			}
			return Response.ok(outArray.toJSONString()).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.Resources"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

}
