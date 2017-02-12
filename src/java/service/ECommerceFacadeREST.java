package service;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

@Path("commerce")
public class ECommerceFacadeREST {

    @Context
    private UriInfo context;

    public ECommerceFacadeREST() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    @PUT
    @Path("createECommerceTransactionRecord")
    @Consumes({"application/json"})
    public Response createECommerceTransactionRecord(@QueryParam("memberID") Long memberID, @QueryParam("amountPaid") double amountPaid,
    @QueryParam("storeID") Long storeID) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "INSERT INTO salesrecordentity (AMOUNTDUE, AMOUNTPAID, AMOUNTPAIDUSINGPOINTS, CREATEDDATE, CURRENCY, LOYALTYPOINTSDEDUCTED, POSNAME, RECEIPTNO, SERVEDBYSTAFF, MEMBER_ID, STORE_ID) VALUES (?, ?, 0.0, ?, \"SGD\", 0, \"Counter 1\", ?, \"Cashier 1\", ?, ?);";
            PreparedStatement ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            
            ps.setDouble(1, amountPaid);
            ps.setDouble(2, amountPaid);
            ps.setString(3, dateFormat.format(date));
            ps.setString(4, Long.toString(timestamp.getTime()));
            ps.setLong(5, memberID);
            ps.setLong(6, storeID);
            int result = ps.executeUpdate();
            long key = -1L;
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
            key = rs.getLong(1);
            }
            if (result > 0) {
                return Response.status(Response.Status.CREATED).build();

            }
            else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @PUT
    @Path("updateQuantity")
    @Consumes({"application/json"})
    public Response updateQuantity(@QueryParam("countryID") Long countryID, @QueryParam("SKU") String SKU, @QueryParam("Quantity") int Quantity) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "UPDATE `islandfurniture-it07`.lineitementity as lii, (Select li.*  from `islandfurniture-it07`.country_ecommerce c, `islandfurniture-it07`.warehouseentity w, `islandfurniture-it07`.storagebinentity sb, `islandfurniture-it07`.storagebinentity_lineitementity sbli, `islandfurniture-it07`.lineitementity li, `islandfurniture-it07`.itementity i where li.ITEM_ID=i.ID and sbli.lineItems_ID=li.ID and sb.ID=sbli.StorageBinEntity_ID and w.id=sb.WAREHOUSE_ID and c.warehouseentity_id=w.id and c.countryentity_id=? and i.SKU = ? and sb.type<>'Outbound')\n" +
"as liii SET lii.QUANTITY = lii.QUANTITY - ? where lii.ID = liii.ID; ";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setLong(1, countryID);
            ps.setString(2, SKU);
            ps.setInt(3, Quantity);
            
            int result = ps.executeUpdate();
            if (result > 0) {
                return Response.status(Response.Status.OK).build();

            }
            else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
}
