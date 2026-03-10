package com.minhpt.smart_wallet_service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjList extends ArrayList<Object> {
    public static ObjList of(Object object){
        ObjList result = new ObjList();
        result.add(object);
        return result;
    }

    public static ObjList of(Object... objects){
        ObjList result = new ObjList();
        result.addAll(Arrays.asList(objects));
        return result;
    }

    public static ObjList of(List<Object> objects){
        ObjList result = new ObjList();
        result.addAll(objects);
        return result;
    }
}
