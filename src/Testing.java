import java.util.ArrayList;

public class Testing {

    public static void main(String[] args) throws Exception {
        ArrayList<String> array = new ArrayList<>();
        array.add("I'm sorry for this guys genuinely :/");
        array.add("@Jogi Don't remove me, I know I'm too quick! I love this store!");
        array.add("@Nathan Want free dominos? There are always great deals! E.g. 50% off when you spend more than £30!");

        String[] hehe = array.get(1).split(" ", 0);

        System.out.println(hehe[0]);
    }
}
