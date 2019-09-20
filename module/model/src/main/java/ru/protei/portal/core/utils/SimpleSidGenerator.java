package ru.protei.portal.core.utils;

/**
 * Created by michael on 16.06.16.
 */
public class SimpleSidGenerator implements SessionIdGen{

    public static final double RND_DIG_LEN = Math.pow(10, 10);

    @Override
    public String generateId() {

        StringBuilder b = new StringBuilder(String.format("%014d", System.currentTimeMillis()));

        b.reverse();

        Number r = Math.random()* RND_DIG_LEN;

        String a = String.format("%08d", r.longValue());

        return b.insert(14, a).toString();
    }



//    public static void main (String argv[]){
//        System.out.println(new SimpleSidGenerator().generateId());
//        System.out.println(new SimpleSidGenerator().generateId());
//        System.out.println(new SimpleSidGenerator().generateId());
//    }
}
