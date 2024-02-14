package Library_Management;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class BookNotFound extends Exception{

}

public class Databasae {
static LinkedList<ArrayList<String>> books = new LinkedList<>();
static LinkedList<ArrayList<String>> issuedbooks = new LinkedList<>();

    static void addBook(String name,String aName)
    {
        ArrayList<String> book=new ArrayList<>();
        book.add(name);
        book.add(aName);
        books.add(book);

    }
    static void issueBook(String bookname)//, String issName, String issDate) //throws BookNotFound
    {
        for(ArrayList<String> a:books){
            if(a.contains(bookname)){
//                a.add(issName);
//                a.add(issDate);
                issuedbooks.add(a);
                books.remove(a);}
//            else
//                throw new BookNotFound();
        }

    }
    static void returnBook(String bookname)
    {
        for(ArrayList<String> a:issuedbooks){
            if(a.contains(bookname)){
//                a.remove(2);
//                a.remove(3);
                books.add(a);
                issuedbooks.remove(a);}
        }

    }
    static void displayAvailable()
    {
        for(ArrayList<String> a:books){
            System.out.println(a.get(0)+"  "+a.get(1));//Why 1
        }
    }
    static void displayIssuedBooks()
    {
        for(ArrayList<String> a:issuedbooks){
            System.out.println(a.get(0));//why 1
//            System.out.println("Issued by :"+a.get(2)+" On:"+a.get(3));
        }
    }
public static void main(String[] args) {

    addBook("Harry Potter 1","J.K.Rowling");
    Scanner sc=new Scanner(System.in);
    boolean exit=false;
    System.out.println("Welcome to Library Management System");
    while(true) {
        System.out.println("Enter 1 to see Available books");
        System.out.println("Enter 2 to see Issued books");
        System.out.println("Enter 3 to Issue book");
        System.out.println("Enter 4 to Return book");
        System.out.println("Enter 5 to Add Book");
        System.out.println("Enter 6 to Exit");
        System.out.println("Enter Choice");
        int choice=sc.nextInt();
        switch (choice) {
            case 1 -> displayAvailable();
            case 2 -> displayIssuedBooks();
            case 3 -> {
                System.out.println("Enter book to be issued");
                String book = sc.nextLine();
                System.out.println("Second input");
                String broom = sc.nextLine();
                issueBook(broom);
            }
            case 4 -> {
                System.out.println("Enter book to be returned");
                String boo = sc.nextLine();
                String booth = sc.nextLine();
                returnBook(booth);
            }
            case 5 -> {
                System.out.println("Enter book to be Added");
                String bookname = sc.nextLine();
                String booname = sc.nextLine();
                System.out.println("Author Name");
                String abo = sc.nextLine();
                addBook(booname, abo);
            }
            case 6 -> exit = true;
            default -> System.out.println("Enter valid Choice");
        }
    if(exit) {
               break;}
        }
    }
}

