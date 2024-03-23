package momosetkn.infras.doma.entities;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;


@Entity(immutable = true, metamodel = @Metamodel)
@Table(name = "companies")
public record InfraCompaniesJavaRecord(
        @Id @Column(name = "id") String id,
        @Column(name = "name") String name,

        @Column(name = "updated_by") String updatedBy,
        @Column(name = "updated_at") java.time.LocalDateTime updatedAt,
        @Column(name = "created_by") String createdBy,
        @Column(name = "created_at") java.time.LocalDateTime createdAt
) {
}
