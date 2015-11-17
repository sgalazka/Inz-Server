package pl.edu.pw.elka.sgalazka.inz.database;

import pl.edu.pw.elka.sgalazka.inz.view.Log;
import pl.edu.pw.elka.sgalazka.inz.view.MainDatabasePanel;

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
           /* Logger.getLogger("org.hibernate").setLevel(Level.INFO);
       */
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

    public List<Product> findByName(String name) {
        //todo
        return null;
    }

    public Product findByCode(int code) {
        Product product;
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }

            List<Product> list = entityManager
                    .createQuery("from PRODUCT where id=" + code).getResultList();
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

    public Product findByBarcode(String barcode) {
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
    }

    public List<Product> getAllProducts() {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            List<Product> list = entityManager
                    .createQuery("from Product").getResultList();
            if (!list.isEmpty())
                return list;
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        System.out.println("brak tabeli: product");
        Log.e("brak tabeli: product");
        return null;
    }

    public boolean delete(String barcode) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            Product product = (Product) entityManager.find(Product.class, barcode);
            Log.d("EEntity manager, delete:" + product.getName());
            entityManager.remove(product);
            entityManager.getTransaction().commit();
            Log.d("Deleted name: " + barcode);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println(e);
            Log.e("Cannot delete: " + barcode);
            return false;
        }
        return true;
    }

    public boolean update(Product product) {
        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
            Product temp = (Product) entityManager.find(Product.class, product.getBarcode());

            if (!product.getName().equals(temp.getName()))
                temp.setName(product.getName());
            else if (product.getCode() != temp.getCode())
                temp.setCode(product.getCode());
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
    public boolean addFromBluetooth(String dataString){
        String data[] = dataString.split(":");
        Product product = new Product();
        product.setName(data[1]);
        product.setBarcode(data[2]);
        product.setCode(Integer.parseInt(data[3]));
        product.setQuantity(Integer.parseInt(data[4]));
        Log.d("Adding from bluetooth, name: "+data[1]);
        boolean ret = add(product);
        if(ret){
            //Log.n();
            MainDatabasePanel.fireNotify();
        }

        return ret;
    }
}
