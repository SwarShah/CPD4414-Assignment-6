/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import credentials.Credentials;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
/**
 *
 * @author c0647456
 */
public class ProductList {
    private List<Product> productList;
    
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
