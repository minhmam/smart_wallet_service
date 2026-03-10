package com.minhpt.smart_wallet_service.util;

import java.util.HashMap;

public class Dictionary extends HashMap<String, String> {
    public String put(String key){
        return put(key, key);
    }
}
