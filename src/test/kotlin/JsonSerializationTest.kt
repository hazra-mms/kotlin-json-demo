import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mediamarktsaturn.kotlin.util.validateExternalObject
import kotlin.system.measureTimeMillis
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import net.dongliu.gson.GsonJava8TypeAdapterFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class JsonSerializationTest {

    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())

    private val gson: Gson = GsonBuilder().registerTypeAdapterFactory(GsonJava8TypeAdapterFactory()).create()

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


    @Test
    fun `benchmark between gson and jackson library`() {
        benchmark(2, 1000, false)
        benchmark(10, 10000, true)
    }

    private fun benchmark(iteration: Int, documents: Int, print: Boolean = true) {
        mapper.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
        val jsonString = File("src/test/resources/product.json").readText(Charsets.UTF_8)
        val jacksonAverage = IntArray(iteration).map {
            measureTimeMillis {
                for (i in 1..documents) {
                    mapper.readValue(
                        jsonString,
                        ProductData::class.java
                    )
                }
            }
        }.average()
        if (print) {
            println("Average time taken jackson with $documents documents: $jacksonAverage ms")
        }

        val gsonAverage = IntArray(iteration).map {
            measureTimeMillis {
                for (i in 1..documents) {
                    gson.fromJson(jsonString, ProductData::class.java)
                }
            }
        }.average()
        if (print) {
            println("Average time taken gson without object validation for $documents documents: $gsonAverage ms")
        }
        val gsonAverageWithValidation = IntArray(iteration).map {
            measureTimeMillis {
                for (i in 1..documents) {
                    validateExternalObject(gson.fromJson(jsonString, ProductData::class.java))
                }
            }
        }.average()
        if (print) {
            println("Average time taken gson with object validation for $documents documents: $gsonAverageWithValidation ms")
        }
    }
}

data class Person(
    val fullName: String,
    val height: Int,
    val weight: Int, // non-nullable - should not allow missing value
    val age: Int? // nullable - should be null in case of no value
)