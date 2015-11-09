package pl.edu.pw.elka.sgalazka.inz.database;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;



public class JPAExample {

  private EntityManager entityManager = EntityManagerUtil.getEntityManager();

  /*public static void main(String[] args) {
    JPAExample example = new JPAExample();
    System.out.println("insert");
    example.addPosition(1, "chleb", 0.23);
    example.addPosition(2, "bu³ka", 0.23);
    example.addPosition(67, "piwo", 0.23);
    System.out.println("List: ");
    example.listPosition();
    System.out.println("end list ");
    EntityManagerUtil.closeConnection();
   

  }*/

  public void endTransaction(){
	  entityManager.close();
  }
  
  public Position addPosition(int code, String name, double vat){
	  Position temp = new Position();
	  temp.setName(name);
	  temp.setCode(code);
	  temp.setVat(vat);
	  try{
		  entityManager.getTransaction().begin();
		  temp = entityManager.merge(temp);
		  entityManager.getTransaction().commit();
	  }
	  catch(Exception e){
		  entityManager.getTransaction().rollback();
	  }
	  return temp;
  }
  
  public void listPosition() {
	    try {
	      entityManager.getTransaction().begin();
	      @SuppressWarnings("unchecked")
	      List<Position> Students = entityManager.createQuery("from Position").getResultList();
	      for (Iterator<Position> iterator = Students.iterator(); iterator.hasNext();) {
	        Position position = (Position) iterator.next();
	        System.out.println(position.getCode()+"\t"+position.getName()+"\t"+position.getVat());
	      }
	      entityManager.getTransaction().commit();
	    } catch (Exception e) {
	      entityManager.getTransaction().rollback();
	    }
	  }
  /*public Student saveStudent(String studentName) {
    Student student = new Student();
    try {
      entityManager.getTransaction().begin();
      student.setStudentName(studentName);
      student = entityManager.merge(student);
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      entityManager.getTransaction().rollback();
    }
    return student;
  }

  public void listStudent() {
    try {
      entityManager.getTransaction().begin();
      @SuppressWarnings("unchecked")
      List<Student> Students = entityManager.createQuery("from Student").getResultList();
      for (Iterator<Student> iterator = Students.iterator(); iterator.hasNext();) {
        Student student = (Student) iterator.next();
        System.out.println(student.getStudentName());
      }
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      entityManager.getTransaction().rollback();
    }
  }

  public void updateStudent(Long studentId, String studentName) {
    try {
      entityManager.getTransaction().begin();
      Student student = (Student) entityManager.find(Student.class, studentId);
      student.setStudentName(studentName);
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      entityManager.getTransaction().rollback();
    }
  }

  public void deleteStudent(Long studentId) {
    try {
      entityManager.getTransaction().begin();
      Student student = (Student) entityManager.find(Student.class, studentId);
      entityManager.remove(student);
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      entityManager.getTransaction().rollback();
    }
  }*/
}
