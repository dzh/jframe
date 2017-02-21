/**
 * 
 */
package jframe.demo.elasticsearch;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author dzh
 * @date Jul 31, 2016 11:02:35 PM
 * @since 1.0
 */
@Path("test")
public class Test {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("hi")
    public String hi() {
        return "hi";
    }

    @GET
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("html")
    public String html() {
        StringBuilder buf = new StringBuilder();
        buf.append("<html>");
        buf.append("<body><p>html</p></body>");
        buf.append("</html>");
        return buf.toString();
    }

}
