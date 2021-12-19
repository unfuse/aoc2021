interface SnailNumberNode {
    fun magnitude() : Int
    fun getLeftNode() : SnailNumberNode?
    fun getRightNode() : SnailNumberNode?
    fun getParentNode() : SnailNumberList?
    fun shouldReduce() : Boolean
    fun doReduce() : SnailNumberNode
    fun copy(parent: SnailNumberList?) : SnailNumberNode

    // Reducing a list takes priority over any number reduction
    fun reduce() {
        if (reduceList()) {
            reduce()
        }

        if (reduceNumber()) {
            reduce()
        }
    }

    // traverse condition = should I continue to propagate down (left, right).
    // action condition = once I stop traversing, should I take action.
    // action = the action I should take.
    fun traverse(parent: SnailNumberList?,
                 parentSetter: (p: SnailNumberList, n: SnailNumberNode) -> Unit,
                 actionCondition: (node: SnailNumberNode) -> Boolean,
                 action: (node: SnailNumberNode) -> SnailNumberNode)
    : Boolean {
        getLeftNode()?.let {
            if (it.traverse(this as SnailNumberList, SnailNumberList::setLeftNode, actionCondition, action)) return true
        }

        if (actionCondition.invoke(this)) {
            parentSetter.invoke(parent!!, action.invoke(this))
            return true
        }

        getRightNode()?.let {
            if (it.traverse(this as SnailNumberList, SnailNumberList::setRightNode, actionCondition, action)) return true
        }

        return false
    }

    // find a list to reduce and reduce it.
    // return boolean(if node was found)
    fun reduceList() : Boolean {
        return traverse(getParentNode(),
            { _, _ ->  throw IllegalStateException("should never alter root node as list")},
            { node -> node is SnailNumberList && node.shouldReduce() },
            { node -> node.doReduce() })
    }

    // find a number to reduce and reduce it.
    // return boolean(if node was found)
    fun reduceNumber() : Boolean {
        return traverse(getParentNode(),
            { _, _ ->  throw IllegalStateException("should never alter root node as num")},
            { node -> node is SnailNumber && node.shouldReduce() },
            { node -> node.doReduce() })
    }
}

class SnailNumberList(var parent: SnailNumberList?, var left: SnailNumberNode?, var right: SnailNumberNode?) : SnailNumberNode {
    override fun magnitude(): Int {
        return 3 * getLeftNode().magnitude() + 2 * getRightNode().magnitude()
    }

    override fun getLeftNode(): SnailNumberNode {
        return left!!
    }

    fun setLeftNode(newLeft: SnailNumberNode) {
        left = newLeft
    }

    override fun getRightNode(): SnailNumberNode {
        return right!!
    }

    fun setRightNode(newRight: SnailNumberNode) {
        right = newRight
    }

    override fun getParentNode(): SnailNumberList? {
        return parent
    }

    override fun copy(parent: SnailNumberList?): SnailNumberNode {
        val newList = SnailNumberList(parent, null, null)
        val left: SnailNumberNode = getLeftNode().copy(newList)
        val right: SnailNumberNode = getRightNode().copy(newList)
        newList.setLeftNode(left)
        newList.setRightNode(right)
        return newList
    }

    private fun getDepth() : Int {
        return parent?.let { it.getDepth() + 1 } ?: 0
    }

    override fun shouldReduce(): Boolean {
        return getDepth() >= 4
    }

    override fun doReduce() : SnailNumberNode {
        return explode()
    }

    // TODO: Can probably make the graph traversal more generic and combine but I will not be doing that right now
    private fun explode() : SnailNumber {
        val parent : SnailNumberList = getParentNode() ?: throw IllegalStateException("tried exploding a list at depth ${getDepth()} without a parent")

        // find a parent with a {direction} node
        // take that node's {direction}x1 {other_direction} until there are no more {other_direction}
        // if no parent with a {direction} is found (shoots up to null) do not add to anything

        var leftParent : SnailNumberList? = parent
        var leftLastNode = this
        var rightParent : SnailNumberList? = parent
        var rightLastNode = this

        while (leftParent?.getLeftNode() == leftLastNode) {
            leftLastNode = leftParent
            leftParent = leftParent.parent
        }

        while (rightParent?.getRightNode() == rightLastNode) {
            rightLastNode = rightParent
            rightParent = rightParent.parent
        }

        var left = leftParent?.getLeftNode()
        var right = rightParent?.getRightNode()

        while (left?.getRightNode()?.let {  left = it; true } == true) { Unit }
        while (right?.getLeftNode()?.let { right = it; true } == true) { Unit }

        left?.let {   (left as SnailNumber).number +=  (getLeftNode() as SnailNumber).number }
        right?.let { (right as SnailNumber).number += (getRightNode() as SnailNumber).number }

        return SnailNumber(parent, 0)
    }

    override fun toString(): String {
        return "[$left,$right]"
    }

    fun add(that: SnailNumberList) : SnailNumberList {
        val addList = SnailNumberList(null, null, null);
        val left = this.copy(addList)
        val right = that.copy(addList)
        addList.setLeftNode(left)
        addList.setRightNode(right)
        addList.reduce()
        return addList
    }
}

class SnailNumber(var parent: SnailNumberList?, var number: Int) : SnailNumberNode {
    override fun magnitude(): Int {
        return number
    }

    override fun getLeftNode(): SnailNumberNode? {
        return null
    }

    override fun getRightNode(): SnailNumberNode? {
        return null
    }

    override fun getParentNode(): SnailNumberList? {
        return parent
    }

    override fun shouldReduce(): Boolean {
        return number >= 10
    }

    override fun copy(parent: SnailNumberList?): SnailNumberNode {
        return SnailNumber(parent, number)
    }

    override fun doReduce() : SnailNumberNode {
        return split()
    }

    private fun split() : SnailNumberList {
        val left = SnailNumber(null, number/2)
        val right = SnailNumber(null, if (number % 2 == 0) number/2 else (number/2) + 1 )
        val nextList = SnailNumberList(getParentNode(), left, right)
        left.parent = nextList
        right.parent = nextList
        return nextList
    }

    override fun toString(): String {
        return "$number"
    }
}

// Process an input string into a SnailNumberList. Executed recursively for sublists in substrings
// Easiest way i could find is to treat the string as immutable and track an index that grows over each operation
fun makeList(parent: SnailNumberList?, line: String, curIndex : Int) : Pair<SnailNumberList, Int> {
    // start a list
    var nextIndex = curIndex
    if (line[nextIndex++] == '[') {
        val list = SnailNumberList(parent, null, null)

        // Process the node element as list (recursive) or a regular number
        val craftNode: () -> Pair<SnailNumberNode, Int> = {
            if (line[nextIndex] == '[') {
                makeList(list, line, nextIndex)
            }
            else {
                Pair(SnailNumber(list, line[nextIndex++].toString().toInt()), nextIndex)
            }
        }

        val (left : SnailNumberNode, leftIndex) = craftNode.invoke()
        nextIndex = leftIndex

        assert(line[nextIndex++] == ',')

        val (right: SnailNumberNode, rightIndex) = craftNode.invoke()
        nextIndex = rightIndex

        assert(line[nextIndex++] == ']')

        list.left = left
        list.right = right
        return Pair(list, nextIndex)
    }

    throw IllegalStateException("could not process $line")
}

val nums : List<SnailNumberList> = Utils.readFileAsStringList("day18")
    .map{ makeList(null, it, 0).first }

// Add 'em all up
println(nums.drop(1).fold(nums[0]) { a, b -> a.add(b) }.magnitude())

// Find the maximum piecewise-pair addition magnitude
var MAXIMAGNITUDE : Int = 0
nums.forEachIndexed {
        index, a -> nums.drop(index).forEach {
            b -> MAXIMAGNITUDE = maxOf(MAXIMAGNITUDE, a.add(b).magnitude(), b.add(a).magnitude())
        }
}
println(MAXIMAGNITUDE)