<#-- See also org.seasar.doma.gradle.codegen.desc.EntityDesc -->
<#import "/lib.ftl" as lib>
<#if lib.copyright??>
${lib.copyright}
</#if>
<#if packageName??>
package ${packageName}
</#if>

<#list importNames as importName>
import ${importName}
</#list>

/**
<#if showDbComment && comment??>
 * ${comment}
</#if>
<#if lib.author??>
 * @author ${lib.author}
</#if>
 */
@Entity(immutable = true<#if useListener || namingType != "NONE" || useMetamodel>, </#if><#if useListener>listener = ${listenerClassSimpleName}::class</#if><#if namingType != "NONE"><#if useListener>, </#if>naming = ${namingType.referenceName}</#if><#if useMetamodel><#if useListener || namingType != "NONE">, </#if>metamodel = Metamodel()</#if><#if useListener || namingType != "NONE" || useMetamodel>)</#if>
<#if showCatalogName && catalogName?? || showSchemaName && schemaName?? || showTableName && tableName??>
@Table(<#if showCatalogName && catalogName??>catalog = "${catalogName}"</#if><#if showSchemaName && schemaName??><#if showCatalogName && catalogName??>, </#if>schema = "${schemaName}"</#if><#if showTableName><#if showCatalogName && catalogName?? || showSchemaName && schemaName??>, </#if>name = "${tableName}"</#if>)
</#if>
data class <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if>(
<#list ownEntityPropertyDescs as property>

<#if showDbComment && property.comment?? && property.comment?length != 0 >
    /** ${property.comment} */
</#if>
<#if property.id>
    @Id
<#if property.generationType??>
    @GeneratedValue(strategy = ${property.generationType.referenceName})
  <#if property.generationType == "SEQUENCE">
    @SequenceGenerator(sequence = "${tableName}_${property.columnName}"<#if property.initialValue??>, initialValue = ${property.initialValue}</#if><#if property.allocationSize??>, allocationSize = ${property.allocationSize}</#if>)
  <#elseif property.generationType == "TABLE">
    @TableGenerator(pkColumnValue = "${tableName}_${property.columnName}"<#if property.initialValue??>, initialValue = ${property.initialValue}</#if><#if property.allocationSize??>, allocationSize = ${property.allocationSize}</#if>)
  </#if>
</#if>
</#if>
<#if property.version>
    @Version
</#if>
<#if property.showColumnName && property.columnName??>
    @Column(name = "${property.columnName}")
</#if>
    val ${property.name}: ${property.languageClassSimpleName}<#if property.nullable>? = null</#if><#if property_has_next || originalStatesPropertyName??>,</#if>
</#list>
<#if originalStatesPropertyName??>

    @OriginalStates
    val ${originalStatesPropertyName}: <#if entityPrefix??>${entityPrefix}</#if>${simpleName}<#if entitySuffix??>${entitySuffix}</#if>
</#if>
)
