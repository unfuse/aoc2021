class Cave(val v: String, a: String?) {
    private val adjacentKeys : MutableSet<String>
    val isSmallCave = v == v.lowercase()

    init {
        adjacentKeys = a?.let { mutableSetOf(it) } ?: mutableSetOf()
    }

    fun addAdjacentKey(a: String) {
        adjacentKeys.add(a)
    }

    fun getAdjacentKeys() : Collection<String> {
        return adjacentKeys.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cave

        if (v != other.v) return false

        return true
    }

    override fun hashCode(): Int {
        return v.hashCode()
    }

    override fun toString(): String {
        return "$v -> $adjacentKeys"
    }
}

class CaveSystem(caves: Collection<Cave>) {
    private val startNodeName = "start"
    private val endNodeName = "end"

    private val graph : Map<String, Cave> = caves.associateBy { it.v }
    private val startNode : Cave = graph[startNodeName] ?: throw IllegalStateException("no start node")
    private val endNode : Cave = graph[endNodeName] ?: throw IllegalStateException("no end node")

    fun getPaths() : Collection<Traversal> {
        return getPathsRecursive(Traversal(null, startNode, false), startNode)
    }

    private fun getPathsRecursive(traversal: Traversal, cur: Cave) : Set<Traversal> {
        // path is complete - go home
        if (cur.v == endNodeName) {
            return setOf(traversal)
        }

        return cur.getAdjacentKeys()
            .filterNot{ it in traversal.visited }
            .mapNotNull { graph[it] }
            .flatMap outer@{ next ->
                return@outer traversal.overrideOptions(next == endNode).flatMap inner@{
                    val nextTraversal = Traversal(traversal, next, it)
                    return@inner getPathsRecursive(nextTraversal, next)
                }
            }.toSet()
    }
}

class Traversal(t: Traversal?, next: Cave, chooseOverride: Boolean) {
    val path : MutableList<String>
    val visited: MutableSet<String>
    var hasOverride: Boolean

    init {
        path = t?.path?.toMutableList() ?: mutableListOf()
        visited = t?.visited?.toMutableSet() ?: mutableSetOf()
        hasOverride = t?.hasOverride ?: false

        path.add(next.v)

        /*
            If Not choosing override - add visited
            Else
              Override false - skip visited, set to true
              Override true - not valid, should not receive this
         */
        if (next.isSmallCave) {
            if (!chooseOverride) {
                visited.add(next.v)
            }
            else {
                if (!hasOverride){
                    hasOverride = true
                }
                else {
                    throw IllegalStateException("Cannot select override if already overridden")
                }
            }
        }
    }

    fun overrideOptions(atEndNode: Boolean) : List<Boolean> {
        // Part 1 - always return false
        //  return listOf(false)

        // Part 2 - allow paver to choose if it is selecting a small cave twice
        if (atEndNode) return listOf(false)
        return if(hasOverride) listOf(false) else listOf(true, false)
    }

    override fun toString(): String {
        return path.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Traversal

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

val input : List<Cave> = Utils.readFileAsList("day12") { line ->
    line.trim().split("-").map(String::trim).windowed(2).flatMap { listOf(Cave(it[0], it[1]), Cave(it[1], it[0])) }
}
.flatten()
.groupBy { it.v }
.map { it.value.reduce{ acc: Cave, cave: Cave -> cave.getAdjacentKeys().forEach(acc::addAdjacentKey); acc } }

val caveSystem = CaveSystem(input)
val paths = caveSystem.getPaths()

println(paths.size)