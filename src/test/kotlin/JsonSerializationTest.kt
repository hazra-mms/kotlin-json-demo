import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class JsonSerializationTest {

    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModule(KotlinModule.Builder().build())

    @Test
    fun `test for null de-serialization`() {
        assertThrows<MismatchedInputException> {
            mapper.readValue(
                File("src/test/resources/nullable_weight.json").readText(Charsets.UTF_8),
                Person::class.java
            )
        }
    }

    @Test
    fun `test for not null de-serialization`() {
        val person: Person =
            mapper.readValue(
                File("src/test/resources/non_nullable_weight.json").readText(Charsets.UTF_8),
                Person::class.java
            )
        assertNotNull(person)
        assertNull(person.age)
    }
}

data class Person(
    val fullName: String,
    val height: Int,
    val weight: Int, // non-nullable - should not allow missing value
    val age: Int? // nullable - should be null in case of no value
)