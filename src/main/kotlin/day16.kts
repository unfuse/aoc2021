val input = Utils.readFileAsString("day16")

fun String.hexToBin() : String {
    return this.toInt(16).toString(2).padStart(4, '0')
}

val packetString = input.chunked(1).joinToString("") { it.hexToBin() }

abstract class Packet(val version: Int, val type: Int) {
    abstract fun getValue() : Long

    open fun getVersionSum() : Int {
        return version
    }
}

class LiteralPacket(version: Int, type: Int, val literal: Long) : Packet(version, type) {
    override fun getValue(): Long {
        return literal
    }
}

class OperatorPacket(version: Int, type: Int, val lengthType: Int, val length: Int, val subPackets: List<Packet>) : Packet(version, type) {
    override fun getValue(): Long {
        return when(type) {
            0 -> subPackets.sumOf { it.getValue() }
            1 -> subPackets.fold(1) { acc, packet -> acc * packet.getValue() }
            2 -> subPackets.minOf { it.getValue() }
            3 -> subPackets.maxOf { it.getValue() }
            5 -> if (subPackets[0].getValue() > subPackets[1].getValue()) 1 else 0
            6 -> if (subPackets[0].getValue() < subPackets[1].getValue()) 1 else 0
            7 -> if (subPackets[0].getValue() == subPackets[1].getValue()) 1 else 0
            else -> throw IllegalStateException("operator packet with invalid type $type")
        }
    }

    override fun getVersionSum(): Int {
        return version + subPackets.sumOf { it.getVersionSum() }
    }
}

var parsePosition = 0

fun <T> String.read(len: Int, mapper: (String) -> T) : T {
    parsePosition += len
    return mapper.invoke(this.substring(parsePosition - len, parsePosition))
}

fun String.read(len: Int) : String {
    return this.read(len) { it }
}

fun makePacket() : Packet {
    val version: Int = packetString.read(3) { it.toInt(2) }
    val type: Int = packetString.read(3) { it.toInt(2) }

    // literal
    if (type == 4) {
        var parsedLastSegment: Boolean
        var literal = ""
        do {
            parsedLastSegment = packetString.read(1) == "0"
            literal += packetString.read(4)
        } while (!parsedLastSegment)

        return LiteralPacket(version, type, literal.toLong(2))
    }
    // operator
    else {
        val lengthType = packetString.read(1) { it.toInt() }

        val (length, subPackets) = when (lengthType) {
            0 -> {
                val totalPacketLength = packetString.read(15) { it.toInt(2) }
                val endPosition = parsePosition + totalPacketLength
                val subPackets = mutableListOf<Packet>()
                do {
                    subPackets.add(makePacket())
                } while(parsePosition < endPosition)

                Pair(totalPacketLength, subPackets)
            }
            1 -> {
                val numPackets = packetString.read(11) { it.toInt(2) }
                val subPackets = (1..numPackets).map { makePacket() }

                Pair(numPackets, subPackets)
            }
            else -> throw IllegalStateException("length type $lengthType not recognized at position $parsePosition")
        }

        return OperatorPacket(version, type, lengthType, length, subPackets)
    }
}

val packet: Packet = makePacket()
println(packet.getVersionSum())
println(packet.getValue())