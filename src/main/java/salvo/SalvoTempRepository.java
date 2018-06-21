package salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface SalvoTempRepository extends JpaRepository<SalvoTemp, Long> {
    List<SalvoTemp> findById (long id);
}