package dat.daos;

import java.util.List;

public interface IDAO<T, I> {

    T read(I i);
    List<T> readAll();
    T create(T t);
    T update(I i, T t);
    T delete(I i);

    //void delete(Integer integer);

    boolean validatePrimaryKey(I i);

}
