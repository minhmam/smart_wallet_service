package com.minhpt.smart_wallet_service.constant;

public class Constant {
    public static class CODE {
        public static final Integer OK = 200;
        public static final String VALIDATION_ERROR = "6001-error.validation";

        private CODE() {
        }
    }

    public static final int DEFAULT_PAGE_SIZE_MAX = 10000;
    public static final String USER_DEFAULT = "system";
    public static final String SUCCESS = "Thành công";
    public static final int DELETED = 0;
    public static final int NOT_DELETE = 1;

    public enum SortOrderEnum {
        ASC, DESC
    }

    public enum StateDelelete {
        DELETED(1), NOT_DELETED(0);
        private int value;

        StateDelelete(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
