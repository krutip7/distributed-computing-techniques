package IPC;

import java.util.HashMap;

enum Publication {
    WILEY,
    OXFORD,
    MCGRAW_HILL,
    PEARSON,
    SPRINGER
}


enum Category {
    BOOKS,
    ELECTRONICS,
    FURNITURE
}


class ProductWarehouse {
    HashMap<String, Product> productList = new HashMap<>();

    ProductWarehouse() {
        productList.put("BKCO1111", new Product("BKCO1111", "Distributed Computing", Publication.WILEY, Category.BOOKS));
        productList.put("BKCO1234", new Product("BKCO1234", "Distributed Systems", Publication.PEARSON, Category.BOOKS));
        productList.put("BKCO5678", new Product("BKCO5678", "High Performance Computing", Publication.MCGRAW_HILL, Category.BOOKS));
    }

}

public class Product {

    String id;
    String name;
    Publication publisher;
    Category category;

    Product(String id, String name,Publication publisher, Category category) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.category = category;
    }

    public Product(String id) {
        this.id = id;
        try {
            Product sample = new ProductWarehouse().productList.get(id);
            this.name = sample.name;
            this.publisher = sample.publisher;
            this.category = sample.category;
        } catch (Exception e){
            this.id = null;
        }
    }

    @Override
    public String toString() {
        return  (this.id != null)?
                ("PRODUCT{" +
                "\n\tid: " + this.id +
                "\n\tname: " + this.name +
                "\n\tcategory: " + this.category +
                "\n\tpublisher: " + this.publisher +
                "\n}")
                : "Invalid Product ID";
    }
}
