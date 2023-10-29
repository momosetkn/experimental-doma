@file:Suppress("WildcardImport", "NoWildcardImports")

package momosetkn.infras.doma.entities.meta

import momosetkn.infras.doma.entities.InfraCompanies_
import momosetkn.infras.doma.entities.InfraEmployees_
import momosetkn.infras.doma.entities.InfraNews_
import momosetkn.infras.doma.entities.InfraProducts_
import momosetkn.infras.doma.entities.InfraProductDetails_


/**
 * Domaのクエリで使うMeta情報
 */
object Meta

// あえて、Metaだけimportしても使えないようにしています（個別にimportする必要がある）。何がimportされたかが明示的になるようにするためです。
val Meta.companies by lazy { InfraCompanies_() }
val Meta.news by lazy { InfraNews_() }
val Meta.products by lazy { InfraProducts_() }
val Meta.productDetails by lazy { InfraProductDetails_() }
val Meta.employees by lazy { InfraEmployees_() }
