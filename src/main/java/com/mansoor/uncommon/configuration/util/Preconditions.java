package com.mansoor.uncommon.configuration.util;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class Preconditions {
    private Preconditions() {
    }

    public static boolean isNull(Object reference){
       return reference==null;
    }

    public static boolean isNotNull(Object reference){
        return !isNull(reference);
    }
    
    public static void checkNull(final Object reference,final String msg){
        if(isNull(reference)){
            throw new IllegalArgumentException(msg);
        }
    }
}
