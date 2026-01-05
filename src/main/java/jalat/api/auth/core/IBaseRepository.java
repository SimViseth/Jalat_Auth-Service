package jalat.api.auth.core;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean // to prevent spring confuse create bean for repository
public interface IBaseRepository<T, ID> extends JpaRepository<T, ID> {

    // Insert
    public default T saveEntity(T entity) {
        T saveEntity = this.save(entity);
        return saveEntity;
    }

    // Get by Id
    public default Optional<T> getEntityById(ID id) {
        Optional<T> entity = this.findById(id);
        return entity;
    }

    // Update
    public default T updateEntity(T entity) {
        T updateEntity = this.save(entity);
        return updateEntity;
    }

    // Get all
    public default List<T> listAll(Sort sort) {
        List<T> entityList = this.findAll();
        return entityList;
    }

    // Delete
    public default boolean deleteEntity(ID id) {
        this.deleteById(id);
        return true;
    }
}
