/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import entities.Product;
import entities.ProductList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author c0647456
 */
@Path("/product")
@RequestScoped
public class ProductREST {
    
    @Inject
    ProductList productList;
    
    @GET
    @Produces("application/json")
    public Response getAll(){
        return Response.ok(productList.toJSON()).build();
    }
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id){
        try{
            return Response.ok(productList.get(id).toJSON()).build();
        } catch(Exception ex){
            return Response.status(500).build();
        }
    }
    
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json){
        try {
            productList.add(new Product(json));
            return Response.ok().build();
        } catch (Exception ex) {
             return Response.status(500).build();
        }
    }
    
    @PUT
    @Path("{id}")
    @Produces("application/json")
    public Response set(@PathParam("id") int id, JsonObject json){
        try {
            Product p = new Product(json);
            productList.set(id, p);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).build();

        }
    }
    
    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public Response delete(@PathParam("id") int id){
        try {
            productList.remove(id);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }
}
