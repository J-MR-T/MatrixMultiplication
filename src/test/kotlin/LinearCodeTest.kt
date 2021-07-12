import calculations.Matrix
import calculations.numMod
import codes.LinearCode
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.createTestName
import io.kotest.matchers.shouldBe
import java.util.*

class LinearCodeTest : StringSpec({
    val hamming74 = LinearCode(7, 4, Matrix(arrayOf(arrayOf(0, 1, 1, 1), arrayOf(1, 0, 1, 1), arrayOf(1, 1, 0, 1))))

    (0..5000).asSequence().forEach { index ->
        val infoWord = arrayOf(
            (index numMod 11) numMod 2,
            (index numMod 13) numMod 2,
            (index numMod 17) numMod 2,
            (index numMod 19) numMod 2
        )
        "${infoWord.contentToString()} encoding decoding test" {
            val codeWord = hamming74.encode(infoWord)

            val noisyCodeWord =
                LinearCode.noisyChannelSimulation(
                    codeWord ?: fail("Encoding failed"),
                    probabilityOfFlipPerBit = 0.3,
                    maxFlippedBits = 1
                )
            val decodedNoisyCodeWord = hamming74.decode(noisyCodeWord)
            createTestName(
                "${infoWord.contentToString()} encoding (encoded to: ${codeWord.contentToString()}) " +
                        "noising (noised to: ${noisyCodeWord.contentToString()}) decoding (decoded to: ${decodedNoisyCodeWord.contentToString()})test"
            )
            infoWord shouldBe decodedNoisyCodeWord
        }
    }


    "encoding" {
        withClue("(1,0,1,1) should be encoded to (1,0,1,1,0,1,0)") {
            hamming74.encode(
                arrayOf(1, 0, 1, 1)
            ) shouldBe arrayOf(1, 0, 1, 1, 0, 1, 0)
        }
    }
    "decoding" {
        hamming74.decode(
            arrayOf(1, 0, 1, 1, 0, 1, 0), 2
        ) shouldBe arrayOf(1, 0, 1, 1)
        hamming74.decode(
            arrayOf(1, 0, 1, 0, 0, 1, 0), 2
        ) shouldBe arrayOf(1, 0, 1, 1)
        hamming74.decode(
            arrayOf(1, 0, 1, 1, 1, 1, 0), 2
        ) shouldBe arrayOf(1, 0, 1, 1)
        hamming74.decode(
            arrayOf(1, 0, 1, 1, 0, 1, 1), 2
        ) shouldBe arrayOf(1, 0, 1, 1)
    }

})
