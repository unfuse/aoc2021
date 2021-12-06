import java.io.File

class Utils {

    companion object {

        private fun openFile(fileName: String): File {
            return File("/Users/david.kennedy/code/hacks/aoc2021/src/main/resources/$fileName.txt")
        }

        // There's probably a snazzier way of doing this directly with collectors, but I can't quite get the types
        //   to work out correctly, so I am relying on future me to get it.
        fun <F, L> readFile(
            fileName: String,
            lineMapper: (line: String) -> L,
            collectorSupplier: () -> F,
            lineCollector: (item: L, collector: F) -> Unit
        ): F {
            val result = collectorSupplier.invoke()
            openFile(fileName)
                .readLines()
                .map(lineMapper)
                .stream()
                .forEach {
                    lineCollector.invoke(it, result)
                }
            return result
        }

        fun readFileSplitByNewlines(fileName: String): List<String> {
            return openFile(fileName).readText().split("\n\n")
        }


        fun <L> readFileAsList(fileName: String, lineMapper: (line: String) -> L) : List<L> {
            return readFile(fileName, lineMapper, ::ArrayList ) { item, collector -> collector += item }
        }

        fun readFileAsIntList(fileName: String) : List<Int> {
            return readFileAsList(fileName, String::toInt)
        }

        fun readFileAsStringList(fileName: String) : List<String> {
            return readFileAsList(fileName) { it }
        }

        // ridiculous that something like this isn't built in to "range" in the base package.
        // a..b and b..a should work regardless of which one is smaller
        // stackoverflow eventually educated me for why it wasn't working, so I stole this
        // https://stackoverflow.com/questions/9562605/in-kotlin-can-i-create-a-range-that-counts-backwards
        infix fun Int.toward(to: Int): IntProgression {
            val step = if (this > to) -1 else 1
            return IntProgression.fromClosedRange(this, to, step)
        }
    }
}