package service;

import dao.ProductDAO;
import model.Product;
import java.util.List;

public class ProductService {

    ProductDAO dao = new ProductDAO();

   public void addProductWithId(int id, String name, int qty, double price) {
    dao.addProduct(new Product(id, name, qty, price));
}

    public void deleteProduct(int id) {
        dao.deleteProduct(id);
    }
    public void updateProduct(int id, String name, int qty, double price) {
    dao.updateProduct(new Product(id, name, qty, price));
}

    // ✅ THIS WAS MISSING
    public List<Product> getAllProducts() {
        return dao.getAllProducts();
    }
}