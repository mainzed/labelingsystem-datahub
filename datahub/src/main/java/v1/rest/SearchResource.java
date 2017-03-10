package v1.rest;

import exceptions.Logging;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

@Path("/search")
public class SearchResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getAPIpage() {
        try {
            JSONObject outObject = new JSONObject();
            return Response.ok(outObject).header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.SearchResource"))
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        }
    }

}
