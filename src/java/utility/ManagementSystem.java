package utility;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

//import org.h2.tools.Server;

public class ManagementSystem {
	private static Connection con;
	private static ManagementSystem instance;
	// private static DataSource dataSource;

	boolean ascending = true;

	private ManagementSystem() {
	}

	public static synchronized ManagementSystem getInstance() {
		if (instance == null) {
			try {
				instance = new ManagementSystem();
				// Context initContext = new InitialContext();
				// Context envContext = (Context) initContext.lookup("java:comp/env");
				// ManagementSystem.dataSource = (DataSource)
				// envContext.lookup("jdbc/StudentsDS");
				// if (dataSource == null) throw new Exception("Data source not found!");
				// con = dataSource.getConnection();

				Class.forName("org.h2.Driver");
				con = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/devDb", "sa", "");

				DatabaseMetaData meta = con.getMetaData();
				ResultSet res = meta.getTables(null, null, "PRODUCTS", null);
				if (!res.next()) {
					Statement stmt = con.createStatement();
				      String sql = "create table products\r\n" + 
				      		"(\r\n" + 
				      		"  id long unsigned not null auto_increment,\r\n" + 
				      		"  product_id long unsigned not null,\r\n" + 
				      		"  title varchar(255) not null,\r\n" + 
				      		"  description longvarchar(5000),\r\n" + 
				      		"  rating float,\r\n" + 
				      		"  price long,\r\n" + 
				      		"  inet_price long,\r\n" + 
				      		"  image longvarchar(600),\r\n" + 
				      		"  primary key (id)\r\n" + 
				      		") engine=InnoDB;";
				      stmt.executeUpdate(sql);
				}

				// } catch (NamingException e) {
				// e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	public Collection<Product> getAllProducts() throws SQLException {
		return getAllProducts("id, title");
	}

	public Collection<Product> getAllProducts(String sort) throws SQLException {
		Collection<Product> products = new ArrayList<>();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id, product_id, title, description, rating, price, "
				+ "inet_price, image FROM products ORDER BY " + sort + switchOrder());
		while (rs.next()) {
			Product product = new Product(rs);
			products.add(product);
		}
		rs.close();
		stmt.close();
		return products;
	}

	private String switchOrder() {
		if (ascending) {
			ascending = false;
			return " ASC";
		} else {
			ascending = true;
			return " DESC";
		}
	}

	public Product getProductById(long id) throws SQLException {
		Product product = null;
		PreparedStatement stmt = con.prepareStatement("SELECT id, product_id, title, description, rating, price, "
				+ "inet_price, image FROM products WHERE id = ?");
		stmt.setLong(1, id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			product = new Product(rs);
		}
		rs.close();
		stmt.close();
		return product;
	}

	public void insertProduct(Product product) throws SQLException {
		PreparedStatement stmt = con.prepareStatement(
				"INSERT INTO products " + "(product_id, title, description, rating, price, inet_price, image)"
						+ "VALUES( ?,  ?,  ?,  ?,  ?,  ?,  ?)");
		stmt.setLong(1, product.getProduct_id());
		stmt.setString(2, product.getTitle());
		stmt.setString(3, product.getDescription());
		stmt.setFloat(4, product.getRating());
		stmt.setLong(5, product.getPrice());
		stmt.setLong(6, product.getInet_price());
		stmt.setString(7, product.getImage());
		stmt.execute();
	}

	public void updateProduct(Product product) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("UPDATE products "
				+ "SET product_id = ?, title = ?, description = ?, rating = ?, price = ?, inet_price = ?,"
				+ "image = ? WHERE id = ?");
		stmt.setLong(1, product.getProduct_id());
		stmt.setString(2, product.getTitle());
		stmt.setString(3, product.getDescription());
		stmt.setFloat(4, product.getRating());
		stmt.setLong(5, product.getPrice());
		stmt.setLong(6, product.getInet_price());
		stmt.setString(7, product.getImage());
		stmt.setLong(8, product.getId());
		stmt.executeUpdate();

	}

	public boolean deleteProduct(Product product) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("DELETE FROM products WHERE id =  ?");
		stmt.setLong(1, product.getId());
		return stmt.execute();
	}

}