package com.jingcai.apps.common.lang.exception;

public interface BusinessMessage {

    class Impl implements BusinessMessage {
        private String code;
        private String message;

        public Impl(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object obj) {
            if(null == obj || !(obj instanceof Impl)){
                return false;
            }
            Impl obj2 = (Impl)obj;
            if(null != code && code.equals(obj2.getCode()) && null != message && message.equals(obj2.getMessage())){
                return true;
            }
            return false;
        }
    }

    String getCode();

    String getMessage();
}
