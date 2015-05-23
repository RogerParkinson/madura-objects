package nz.co.senanque.pizzaorder.externals;

import nz.co.senanque.rules.annotations.Function;

public class MyExternalFunctions {

    @Function
    public static String regex(String source, String pattern)
    {
        return "yes, that's okay";
    }
    @Function
    public static Double combine(Number a, Number b)
    {
        return a.doubleValue() + b.doubleValue();
    }
}
