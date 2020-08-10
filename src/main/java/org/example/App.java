package org.example;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.example.entity.Mentor;
import org.example.entity.Project;
import org.hibernate.*;

import org.example.entity.Developer;
import org.example.util.HibernateUtil;
import org.example.util.RandomString;

import javax.persistence.criteria.*;

public class App 
{
    public final static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args )
    {
//        testAssociation();
//        testSaveOrUpdate();
//        testMerge();
//        testBatchInsert();
//        testBatchUpdate();
        testFetching();
//        testCriteriaJoin();
//        testCriteriaSubQuery();
    }

    public static void testFetching(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            /** EAGER
            * select mentor0_.mid as mid1_1_0_, mentor0_.position as position2_1_0_,
            *        devs1_.Mentor_mid as mentor_m1_2_1_, developer2_.did as devs_did2_2_1_, developer2_.did as did1_0_2_,
            *        developer2_.age as age2_0_2_, developer2_.course as course3_0_2_, developer2_.name as name4_0_2_
            * from Mentor mentor0_
            * left outer join
            *      Mentor_Developer devs1_ on mentor0_.mid=devs1_.Mentor_mid
            * left outer join
            *      Developer developer2_ on devs1_.devs_did=developer2_.did
            * where mentor0_.mid=?
            * */

            /** LAZY
            * select mentor0_.mid as mid1_1_0_,
            *        mentor0_.position as position2_1_0_
            * from Mentor mentor0_ where mentor0_.mid=?
            * */
            Mentor mentor = session.byId( Mentor.class ).load( 1 );
//            Mentor mentor1 = session.byId( Mentor.class ).load( 1 );

            List<Developer> devs = mentor.getDevs();
//            List<Developer> devs1 = mentor1.getDevs();

            /**
            * select devs0_.Mentor_mid as mentor_m1_2_0_, devs0_.devs_did as devs_did2_2_0_,
            *        developer1_.did as did1_0_1_, developer1_.age as age2_0_1_,
            *        developer1_.course as course3_0_1_, developer1_.name as name4_0_1_
            * from Mentor_Developer devs0_
            * inner join
            *      Developer developer1_ on devs0_.devs_did=developer1_.did
            * where devs0_.Mentor_mid=?
            * */

//            logger.info(mentor.toString());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }

    public static void testCriteriaJoin(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            List<Object[]> devs = session.createNativeQuery(
                    " SELECT " +
                            "    d.name, d.age, d.course, p.name AS project_name " +
                            "FROM " +
                            "    Developer d " +
                            "        INNER JOIN " +
                            "    Project_Developer pd ON d.did = pd.did " +
                            "        INNER JOIN " +
                            "    Project p ON p.pid = pd.pid " +
                            "    WHERE p.name = :project ")
                    .setParameter("project", "Tensorflow Java")
                    .getResultList();

            for (Object[] dev: devs) {
                String name = (String) dev[0];
                Integer age = (Integer) dev[1];
                String course = (String) dev[2];
                String project = (String) dev[3];
                System.out.println("Developer: " + name +
                        ", " + age + " years old\n"
                        + "Project: "+ project +
                        " with language: " + course);
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }

    public static void testCriteriaSubQuery(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            List<Object[]> devs = session.createNativeQuery(
                    " SELECT " +
                            "    m.mid AS ID, position " +
                            "FROM " +
                            "    Mentor m " +
                            "WHERE " +
                            "    m.mid IN (SELECT  " +
                            "            did " +
                            "        FROM " +
                            "            Developer " +
                            "        WHERE " +
                            "            course = :course) ")
                    .setParameter("course", "Python")
                    .getResultList();

            for (Object[] dev: devs) {
                Integer id = (Integer) dev[0];
                String position = (String) dev[1];
                System.out.println("Mentor ID: " + id +
                        " as " + position);
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }

    public static void testSaveOrUpdate(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            Transaction t = session.beginTransaction();

//            Developer dev = session.byId( Developer.class ).load( 2 );

            Developer dev = session.byId( Developer.class ).load( 2 );
            Developer dev1 = new Developer("Cake", 20, "PHP");
            dev1.setId(2);

//            dev.setAge(18);

//          Exception here
            session.saveOrUpdate(dev);
            session.saveOrUpdate(dev1);
            t.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }

    public static void testBatchInsert(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        RandomString strGen = new RandomString(8);
        try {
            Transaction t = session.beginTransaction();
            int batchSize = 5;

//            Add some dummy project
            List<Project> projects = new ArrayList<Project>( );
            projects.add(new Project("JavaCore"));
            projects.add(new Project("Hibernate"));
            projects.add(new Project("Spring"));
            projects.add(new Project("Tensorflow Java"));
            projects.add(new Project("CloudSim Java"));

            List<Mentor> mentors = new ArrayList<Mentor>( );
            mentors.add(new Mentor("CTO"));
            mentors.add(new Mentor("Python Senior"));
            mentors.add(new Mentor("Java Senior"));
            mentors.add(new Mentor("AI Reseacher"));
            mentors.add(new Mentor("Cloud Infra Engineer"));

            int[] pids = {0, 2, 3};
            int mid = 0;

            for ( int i = 0; i < 20; i++ ) {
                if ( i == 4 ) {
                    //flush a batch of inserts and release memory
                    session.flush();
                    session.clear();
                    logger.info("Flush batch " +  i);
                    pids = new int[]{1, 4};
                    mid = 1;
                }
                else if ( i == 9 ) {
                    //flush a batch of inserts and release memory
                    session.flush();
                    session.clear();
                    logger.info("Flush batch " +  i);
                    pids = new int[]{0, 4};
                    mid = 2;
                }
                else if ( i == 14 ) {
                    //flush a batch of inserts and release memory
                    session.flush();
                    session.clear();
                    logger.info("Flush batch " +  i);
                    pids = new int[]{0, 1, 2};
                    mid = 3;
                }
                else if ( i == 19 ) {
                    //flush a batch of inserts and release memory
                    session.flush();
                    session.clear();
                    logger.info("Flush batch " +  i);
                    pids = new int[]{0, 3, 4};
                    mid = 4;
                }

                Developer dev = new Developer( strGen.nextString(), 20, "Java" );
                Mentor m = mentors.get(mid);
                m.getDevs().add(dev);
                session.saveOrUpdate(m);

                for (int pid:pids){
                    Project p = projects.get(pid);
                    dev.getProjects().add(p);
                    p.getDevs().add(dev);
                    session.saveOrUpdate(p);
                }

                session.save( dev );
            }

            t.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }

    public static void testBatchUpdate(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        RandomString strGen = new RandomString(8);
        try {
            Transaction t = session.beginTransaction();
            int batchSize = 4;

            ScrollableResults devs = session.getNamedQuery("getDevByCourse")
                    .setParameter("course", "Java")
                    .setCacheMode(CacheMode.IGNORE)
                    .scroll(ScrollMode.FORWARD_ONLY);
            int count=0;
            while ( devs.next() ) {
                Developer dev = (Developer) devs.get(0);
                dev.setAge(12);
                if ( ++count % 20 == 0 ) {
                    //flush a batch of updates and release memory:
                    session.flush();
                    session.clear();
                }
            }
            t.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }

    public static void testMerge(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            Transaction t = session.beginTransaction();

//            Developer dev = session.byId( Developer.class ).load( 2 );

            Developer dev = session.byId( Developer.class ).load( 2 );
            Developer dev1 = new Developer("Cake", 20, "PHP");
            dev1.setId(2);

//            dev.setAge(18);

//          Exception here
            session.merge(dev);
            session.merge(dev1);
            t.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }

    public static void testAssociation(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        BasicConfigurator.configure();
        try {
            Transaction t = session.beginTransaction();

            Developer dev1 = new Developer("Tien", 21, "Java");

            Developer dev2 = new Developer();
            dev2.setName("Long");
            dev2.setAge(20);
            dev2.setCourse("JavaScript");

            Developer dev3 = new Developer();
            dev3.setName("Kien");
            dev3.setAge(20);
            dev3.setCourse("JavaScript");

            Project project1 = new Project();
            project1.setName("JavaCore");
            project1.getDevs().add(dev1);

            Project project2 = new Project();
            project2.setName("Hibernate");
            project2.getDevs().add(dev1);
            project2.getDevs().add(dev3);


            dev1.setProjects(Arrays.asList(project1, project2));
            dev1.setProjects(Arrays.asList(project2));

            session.save(dev1);
            session.save(dev2);
            session.save(dev3);
            session.save(project1);
            session.save(project2);

            t.commit();
            logger.info("successfully saved");

            List<Developer> devs = session.createQuery("FROM Developer").getResultList();
            for (Developer d : devs) {
                logger.info(d.toString());
            }

            List<Project> projects = session.createQuery("FROM Project").getResultList();
            for (Project project : projects) {
                logger.info(projects.toString());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }
}
