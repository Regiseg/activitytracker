package proba;

public class ProbaMain {

    public static int age;

    static {
        kiir();
        age = 45;

    }

    public String printHello(String message){
        return message + kiir() ;
    }

    public static String  kiir(){
        Hombre hombre = new Hombre("Diego", 42);
        return hombre.toString();
    }


    public static void main(String[] args) {
        ProbaMain probaMain = new ProbaMain();
        System.out.println(probaMain.printHello("hello "));


    }
}
