import kotlin.math.cos
import kotlin.system.exitProcess

data class Location(val lat: Double, val lon: Double)
data class Point(val location: Location, var name: String = "")
data class Arguments(val location: Location, val step: Double, val letters: String, val columns: Int)

fun main(vararg args: String) {
    val arguments = validateArguments(*args)

    val letters = arguments.letters
    val numbers = arguments.columns

    val start = arguments.location
    val step = arguments.step

    val list = mutableListOf<Point>()
    letters.forEachIndexed { index, c ->
        for (i in 0 until numbers) {
            val element = addMeters(start, -step * index, step * i)
            list.add(Point(element, "$c${i + 1}"))
        }
    }

    printPoints(list)
}

fun addMeters(start: Location, distanceN: Double, distanceE: Double): Location {
    //Position, decimal degrees
    val lat = start.lat
    val lon = start.lon

    //Earth’s radius, sphere
    val R = 6378137

    //Coordinate offsets in radians
    val dLat = distanceN / R
    val dLon = distanceE / (R * cos(Math.PI * lat / 180))

    //OffsetPosition, decimal degrees
    val latO = lat + dLat * 180 / Math.PI
    val lonO = lon + dLon * 180 / Math.PI
    return Location(latO, lonO)
}

fun printPoints(points: List<Point>) {

    print(
        """
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">
    <Document>
        <Folder>
            <name>Grid</name>
        """
    )

    points.forEach { point ->
        print(
            """
            <Placemark>
                <name>${point.name}</name>
                <LookAt>
                    <longitude>${point.location.lon}</longitude>
                    <latitude>${point.location.lat}</latitude>
                    <altitude>0</altitude>
                </LookAt>
                <Point>
                    <coordinates>${point.location.lon},${point.location.lat},0</coordinates>
                </Point>
            </Placemark>
            """
        )
    }

    print(
        """
        </Folder>
    </Document>
</kml>
        """
    )
}

fun validateArguments(vararg args: String): Arguments {
    val help = """
    usage: 
         double -> upper-left point, center of square, lat
         double -> upper-left point, center of square, lon
         double -> step in meters
         string -> set of letters (number of rows)
         int    -> number of columns
    """.trimIndent()

    // todo: rotation
    // todo: set of numbers

    try {
        val startLat = args[0].toDouble()
        val startLon = args[1].toDouble()
        val step = args[2].toDouble()
        val letters = args[3]
        val columns = args[4].toInt()

        return Arguments(Location(startLat, startLon), step, letters, columns)
    } catch (e: Exception) {
        println(help)
        exitProcess(0)
    }
}