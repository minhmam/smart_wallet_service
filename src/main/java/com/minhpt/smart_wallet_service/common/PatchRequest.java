//package com.minhpt.smart_wallet_service.dto.common;
//
//
//import java.util.List;
//
//@FieldExists(
//        dataFieldName = "data",
//        listFieldName = "updateFields"
//)
//public class PatchRequest<T> {
//    @NotNull(message = "{common.error.must.be.not.null}")
//    T data;
//    @NotNull(message = "{common.error.must.be.not.null}")
//    @Size(min = 1, message = "{common.error.update.field.must.has.at.least.one.element}")
//    List<String> updateFields;
//
//    public PatchRequest() {
//        // Default constructor
//    }
//
//    public T getData() {
//        return this.data;
//    }
//
//    public List<String> getUpdateFields() {
//        return this.updateFields;
//    }
//
//    public void setData(final T data) {
//        this.data = data;
//    }
//
//    public void setUpdateFields(final List<String> updateFields) {
//        this.updateFields = updateFields;
//    }
//}
