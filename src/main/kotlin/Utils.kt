import java.io.File

class Utils {

    companion object {

        // There's probably a snazzier way of doing this directly with collectors, but I can't quite get the types
        //   to work out correctly, so I am relying on future me to get it.
        fun <F, L> readFile(
            fileName: String,
            lineMapper: (line: String) -> L,
            collectorSupplier: () -> F,
            lineCollector: (item: L, collector: F) -> Unit
        ): F {
            val result = collectorSupplier.invoke()
            File("/Users/david.kennedy/code/hacks/aoc2021/src/main/resources/$fileName.txt")
                .readLines()
                .map(lineMapper)
                .stream()
                .forEach {
                    lineCollector.invoke(it, result)
                }
            return result
        }

        fun readFileAsIntList(fileName: String) : List<Int> {
            return readFile(fileName, String::toInt, ::ArrayList) { item, collector -> collector += item }
        }

        fun readFileAsStringList(fileName: String) : List<String> {
            return readFile(fileName, { s -> s }, ::ArrayList) { item, collector -> collector += item }
        }
    }
}