package pl.edu.pw.elka.sgalazka.inz.database;

import pl.edu.pw.elka.sgalazka.inz.view.Log;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by ga³¹zka on 2015-10-15.
 */
public class DatabaseManager {
    private EntityManager entityManager = EntityManagerUtil.getEntityManager();

    private static DatabaseManager databaseManager = null;

    public static DatabaseManager getInstance(){
        if(databaseManager==null){
            databaseManager = new DatabaseManager();
           /* Logger.getLogger("org.hibernate").setLevel(Level.INFO);
       */ }
        return databaseManager;
    }

    public void endTransaction(){
        entityManager.close();
    }

    public void add(Product product){
        try{
            entityManager.getTransaction().begin();
            product = entityManager.merge(product);
            entityManager.getTransaction().commit();
            System.out.println("added");
        }
        catch(Exception e){
            entityManager.getTransaction().rollback();
            System.out.println("can't add");
        }
    }
    public List<Product> findByName(String name){
        //todo
        return null;
    }
    public Product findByCode(int code){
        Product product;
        try {
            entityManager.getTransaction().begin();
            List<Product> list = entityManager
                    .createQuery("from PRODUCT where id=" + code).getResultList();
            if(list.isEmpty())
                return  null;
            product = list.get(0);
            if(product!=null)
                return product;
            entityManager.getTransaction().commit();
        } catch (Exception e){
            entityManager.getTransaction().rollback();
        }
        return null;
    }
    public List<Product> getAllProducts(){
        try{
            entityManager.getTransaction().begin();
            List<Product> list = entityManager
                    .createQuery("from Product").getResultList();
            if(!list.isEmpty())
                return list;
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        System.out.println("brak tabeli: product");
        Log.e("brak tabeli: product");
        return null;
    }
}
