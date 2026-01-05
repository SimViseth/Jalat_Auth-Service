package jalat.api.auth.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import static jalat.api.auth.app.AppConstant.*;

@Getter
@Setter
public class BaseEntityResponseDto <T> {
    String code = FAIL_CODE;
    String status = FAIL;
    String msgDev; // message for developer
    T entity;
    List<T> entityList;
    List<Map<String, String>> dataRows;
    public void success() {
        this.status = SUCCESS;
        this.code = SUCCESS_CODE;
    }

    public void notFound() {
        this.code = NOT_FOUND;
        this.status = FAIL;
        this.msgDev = NOT_FOUND;
    }

    @JsonIgnore
    public boolean isSucceed() {
        return status.equals(SUCCESS);
    }

    @JsonIgnore
    public boolean isNotFound() {
        return code.equals(NOT_FOUND);
    }
}
