/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import credentials.Credentials;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.enterprise.context.ApplicationScoped;
/**
 *
 * @author c0647456
 */
@ApplicationScoped
public class ProductList {
    private List<Product> productList = new ArrayList<>();
    
    public ProductList(){
        try(Connection conn = Credentials.getConnection()){
            String query = "SELECT * FROM product";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getInt("Quantity"));
                productList.add(p);
            }
        } catch (SQLException ex){
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public JsonArray toJSON(){
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Product p : productList)
            json.add(p.toJSON());
        return json.build();
    }
    
    public Product get(int ProductId) {
        Product result = null;
        for (int i = 0; i < productList.size() && result == null; i++) {
            Product p = productList.get(i);
            if (p.getProductId() == ProductId) {
                result = p;
            }
        }
        return result;
    }
    
    public void remove(Product p) throws Exception {
        remove(p.getProductId());
    }

    public void remove(int ProductId) throws Exception {
        int result = doUpdate("DELETE FROM product WHERE productId = ?",
                String.valueOf(ProductId));
        if (result > 0) {
            Product original = get(ProductId);
            productList.remove(original);

        } else {
            throw new Exception("Delete Failed");
        }

    }
    
    public void set(int ProductId, Product Product) throws Exception {
        int result = doUpdate("UPDATE product SET name = ?, description = ?, quantity = ? WHERE productId = ?",
                Product.getName(),
                Product.getDescription(),
                String.valueOf(Product.getQuantity()),
                String.valueOf(ProductId)
        );
        if (result == 1) {
            Product original = get(ProductId);
            original.setName(Product.getName());
            original.setDescription(Product.getDescription());
            original.setQuantity(Product.getQuantity());
        } else {
            throw new Exception("Error while updating");
        }
    }
    
    public void add(Product p) throws Exception {
        int result = doUpdate(
                "INSERT into product (productId, name, description, quantity) values (?, ?, ?, ?)",
                String.valueOf(p.getProductId()),
                p.getName(),
                p.getDescription(),
                String.valueOf(p.getQuantity()));
        if (result > 0) {
            productList.add(p);
        } else {
            throw new Exception("Error Inserting");
        }
    }
    
    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("%s\t%s\t%s\n", rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
}
