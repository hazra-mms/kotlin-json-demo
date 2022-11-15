import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.OffsetDateTime

typealias ProductRefId = String
typealias GlobalProductRefId = String

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductData(
    val productId: ProductRefId,
    val globalId: GlobalProductRefId? = null,
    // Timestamp as epoch millis (milliseconds since 1970-01-01T00:00:00Z), because we initially received such timestamps from the product API
    val timestamp: Long,
    val locale: String,
    val name: String,
    // Lifecycle status is actually nullable as well but we are not the ones to decide how to proceed with
    // missing values, so we are treating it as mandatory for us.
    val lifecycleStatus: String,
    val mdmCreatedAt: OffsetDateTime?,
    val mdmUpdatedAt: OffsetDateTime?,
    // Product API does not provide a departmentId, only the leaf node (which is the groupId)
    // The [productGroupId] is actually an [Int?] but we have strings everywhere else in our model so we decided
    // to be a bit inconsistent with the source type here.
    val productGroupId: String?,
    val manufacturerRefId: String,
    // Product API does not provide category paths, only the leaf nodes
    val categories: List<String>?,
    val salesPriceMaintenanceByMarket: Boolean?
)