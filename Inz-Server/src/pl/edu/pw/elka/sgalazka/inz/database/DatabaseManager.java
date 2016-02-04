package pl.edu.pw.elka.sgalazka.inz.database;

import pl.edu.pw.elka.sgalazka.inz.Log.Log;
import pl.edu.pw.elka.sgalazka.inz.view.ProductsPanel;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by ga��zka on 2015-10-15.
 */
public class DatabaseManager {
    private EntityManager entityManager = EntityManagerUtil.getEntityManager();

    private static DatabaseManager databaseManager = null;

    public static DatabaseManager getInstance() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    public void endTransaction() {
        entityManager.close();
    }

    public boolean add(Product product) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            entityManager.persist(product);
            entityManager.getTransaction().commit();

            Log.d("Added to database: " + product.getName());

        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println(e);
            Log.e("Cannot add to database");
            return false;
        }
        return true;
    }

    public Product findByBarcode(String barcode) {
        Product product;
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }

            /*List<Product> list = entityManager
                    .createQuery("select p from Product p where p.barcode='" + barcode + "'", Product.class).getResultList();*/

            List<Product> list = entityManager.createQuery(
                    "SELECT p FROM Product p WHERE p.barcode = :bcode", Product.class)
                    .setParameter("bcode", barcode)
                    .getResultList();


            if (list.isEmpty())
                return null;
            product = list.get(0);
            if (product != null)
                return product;
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        return null;
    }

    /*public Product findByBarcode(String barcode) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }

            Product product = (Product) entityManager.find(Product.class, barcode);
            if (product != null) {
                entityManager.getTransaction().commit();
                return product;
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        return null;
    }*/

    public List<Product> getAllProducts() {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            List<Product> list = entityManager
                    .createQuery("from Product order by id").getResultList();
            if (!list.isEmpty())
                return list;
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
        System.out.println("brak tabeli: product");
        Log.e("brak tabeli: product");
        return null;
    }

    public boolean delete(long id) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            Product product = (Product) entityManager.find(Product.class, id);
            Log.d("EEntity manager, delete:" + product.getName());
            entityManager.remove(product);
            entityManager.getTransaction().commit();
            Log.d("Deleted name: " + id);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println(e);
            Log.e("Cannot delete: " + id);
            return false;
        }
        return true;
    }

    public boolean update(Product product) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            Product temp = (Product) entityManager.find(Product.class, product.getId());

            if (!product.getName().equals(temp.getName()))
                temp.setName(product.getName());
            /*else if (product.getCode() != temp.getCode())
                temp.setCode(product.getCode());*/
            else if (product.getQuantity() != temp.getQuantity())
                temp.setQuantity(product.getQuantity());
            else if (!product.getBarcode().equals(temp.getBarcode()))
                temp.setBarcode(product.getBarcode());

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            Log.e("Cannot update");
            return false;
        }
        return true;
    }

    public String getAllAsString() {
        List<Product> list;
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            list = entityManager
                    .createQuery("from Product order by name", Product.class).getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return "";
        }
        if (!list.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 0; i < list.size(); i++) {
                Product product = list.get(i);
                if (i != 0)
                    stringBuilder.append(";");
                stringBuilder.append(product.getName());
                stringBuilder.append("#");
                stringBuilder.append(product.getBarcode());
                stringBuilder.append("#");
                stringBuilder.append(product.getQuantity());
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public String getByQuantityAsString(int quantity) {
        List<Product> list;
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            list = entityManager.createQuery(
                    "SELECT p FROM Product p WHERE p.quantity < :quan", Product.class)
                    .setParameter("quan", quantity)
                    .getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return "";
        }
        if (!list.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 0; i < list.size(); i++) {
                Product product = list.get(i);
                if (i != 0)
                    stringBuilder.append(";");
                stringBuilder.append(product.getName());
                stringBuilder.append("#");
                stringBuilder.append(product.getBarcode());
                stringBuilder.append("#");
                stringBuilder.append(product.getQuantity());
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public boolean addFromBluetooth(String dataString) {
        String data[] = dataString.split(":");
        Product product = new Product();
        product.setName(data[1]);
        product.setBarcode(data[2]);
        product.setPrice((int)(Double.parseDouble(data[3].replace(',','.'))*100));
        product.setPackaging(data[6].equals("true") ? 1 : 0);
        product.setVat(data[5].toUpperCase());
        product.setQuantity(Integer.parseInt(data[4]));


        if(findByBarcode(data[2])!=null){
            return false;
        }
        Log.d("Adding from bluetooth, name: " + data[1]);

        return add(product);
    }
}
