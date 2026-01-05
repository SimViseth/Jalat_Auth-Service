package jalat.api.auth.core;

import java.util.List;
import java.util.Optional;

import static jalat.api.auth.app.AppConstant.*;

public abstract class BaseDBDao<T, ID> implements IBaseDBDao<T, ID> {

    protected IBaseRepository<T, ID> repository;

    public BaseDBDao(IBaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public BaseEntityResponseDto<T> saveEntity(T entity) {
        T saveEntity = repository.saveEntity(entity);
        var entityDto = new BaseEntityResponseDto<T>();
        entityDto.setStatus(SUCCESS);
        entityDto.setEntity(saveEntity);
        return entityDto;
    }

    @Override
    public BaseEntityResponseDto<List<T>> saveEntities(List<T> entities) {
        List<T> saveEntities = repository.saveAll(entities);
        var entityDto = new BaseEntityResponseDto<List<T>>();
        entityDto.setStatus(SUCCESS);
        entityDto.setEntity(saveEntities);
        return entityDto;
    }

    @Override
    public BaseEntityResponseDto<T> findById(ID id) {
        String responseStatus = SUCCESS;
        Optional<T> optionalEntity = repository.getEntityById(id);
        if (!optionalEntity.isPresent()) {
            responseStatus = FAIL;
        }
        T entity = optionalEntity.orElseGet(() -> null);

        var entityDto = new BaseEntityResponseDto<T>();
        entityDto.setCode(optionalEntity.isPresent() ? SUCCESS_CODE : NOT_FOUND);
        entityDto.setStatus(responseStatus);
        entityDto.setEntity(entity);
        return entityDto;
    }

    @Override
    public BaseEntityResponseDto<T> update(T entity) {
        T updatedEntity = repository.updateEntity(entity);
        var entityDto = new BaseEntityResponseDto<T>();
        entityDto.setStatus(SUCCESS);
        entityDto.setEntity(updatedEntity);
        return entityDto;
    }

    @Override
    public BaseEntityResponseDto<T> findAll() {
        List<T> entityLIst = repository.findAll();
        var appModelList = new BaseEntityResponseDto<T>();
        appModelList.setStatus(SUCCESS);
        appModelList.setEntityList(entityLIst);
        return appModelList;
    }

    @Override
    public BaseEntityResponseDto<T> deleteEntity(ID id) {
        repository.deleteEntity(id);
        var deleteEntity = new BaseEntityResponseDto<T>();
        deleteEntity.setStatus(SUCCESS);
        return deleteEntity;
    }
}