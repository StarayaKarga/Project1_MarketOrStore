package org.example;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static SessionFactory sessionFactory;
    private static MultiMap<String, Float> prod = new MultiValueMap<>();

    public static void main(String[] args) {
        productsList();
        System.out.println("\n" + "This is our product list, choose what you need)");
        Scanner scanner = new Scanner(System.in);
        chooseTheProducts(scanner.nextLine());
        System.out.println("Do u want something else? (Yes/No)");
        for (;;){
            String userAskYesOrNo = scanner.nextLine();
            if (userAskYesOrNo.equals("Yes")){
                System.out.println("Something else, ok. Let`s go");
                chooseTheProducts(scanner.nextLine());
                System.out.println("Do u want something else?");
            }
            if (userAskYesOrNo.equals("No")){
                break;
            }
        }

        System.out.println("\n" + "Ваш список товарів: ");
        for (String key : prod.keySet()) {
            Collection<Float> values = (Collection<Float>) prod.get(key);
            for (Float value : values) {
                System.out.println("Name: " + key + ", Price: " + value);
            }
        }

        System.out.println("До сплати: " + sumOfProducts());
    }

    static {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    public static Session createSession() {
        return sessionFactory.openSession();
    }


    public static void productsList(){
        Session session = createSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<Products> markers = session.createQuery("FROM Products", Products.class).list();

            for (Products marker : markers) {
                if (marker.getQuantity() == null)
                    continue;
                System.out.println("PRODUCT NAME: " + marker.getProductName() + ", QUANTITY: " + marker.getQuantity() + ", PRICE: " + marker.getPrize());
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void chooseTheProducts(String nameOfProduct){
        Session session = createSession();

        List<Products> markers = session.createQuery("FROM Products", Products.class).list();
        for (Products market : markers){
            if (nameOfProduct.equals(market.getProductName())){
                prod.put(market.getProductName(), market.getPrize());
            }
        }
    }

    private static float sumOfProducts(){
        float sum = 0.0f;
        for (String key : prod.keySet()) {
            Collection<Float> values = (Collection<Float>) prod.get(key);
            for (Float value : values) {
                sum += value;
            }
        }
        return sum;
    }

}

