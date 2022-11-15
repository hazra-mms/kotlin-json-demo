import kotlin.test.assertNotNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class JsonSerializationTest {

    private val json: Json = Json { explicitNulls = false }

    @Test
    fun `test for null de-serialization`() {
        assertThrows<MissingFieldException> {
            json.decodeFromString<Person>(File("src/test/resources/nullable_weight.json").readText(Charsets.UTF_8))
        }
    }

    @Test
    fun `test for not null de-serialization`() {
        val person = json.decodeFromString<Person>(File("src/test/resources/non_nullable_weight.json").readText(Charsets.UTF_8))
        assertNotNull(person)
    }
}

@Serializable
data class Person(
    val name: String,
    val height: Int,
    val weight: Int
)