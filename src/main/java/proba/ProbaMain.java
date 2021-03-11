package proba;

public class ProbaMain {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("hello");
        sb.append(" Bibo");
        Hombre bibi = new Hombre("Bibi", 47);

        System.out.println(sb.toString().equals("hello Bibo"));

        System.out.println("Hello "+ bibi);
    }
}
