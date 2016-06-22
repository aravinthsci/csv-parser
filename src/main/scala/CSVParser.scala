import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter, StringReader}

import au.com.bytecode.opencsv.CSVReader

import scala.io.Source._

object CSVParser {

  def main(args: Array[String]): Unit = {
    // args(0): Operation
    // args(1): CSV separator
    // args(2): Path input CSV
    // args(3): Path output CSV
    // args(4): Encoding of CSV
    val operation = args(0)
    val csvSeparator = args(1).toList(0)
    val pathInputCSV = args(2)
    val pathOutputCSV = args(3)
    val encodingCSV = args(4)

    operation match {
      // Remove from the first csv the rows with a key existing in the second csv
      case "csvminuscsv" =>
        // args(5): Path second input CSV
        // args(6): Key position first input CSV
        // args(7): Key position second input CSV
        val pathSecondInputCSV = args(5)
        val keyPositionFirstInputCSV = args(6).toInt
        val keyPositionSecondInputCSV = args(7).toInt

        val firstSourceMap = csvToMap(pathInputCSV, keyPositionFirstInputCSV, csvSeparator, encodingCSV)
        val secondSourceMap = csvToMap(pathSecondInputCSV, keyPositionSecondInputCSV, csvSeparator, encodingCSV)
        val firstSourceMapMinusSecondSourceMap = firstSourceMap -- secondSourceMap.map(_._1)

        mapToCSV(pathOutputCSV, firstSourceMapMinusSecondSourceMap, encodingCSV)

      // Get from the first csv the rows with a key existing in the second csv
      case "csvintersectcsv" =>
        // args(5): Path second input CSV
        // args(6): Key position first input CSV
        // args(7): Key position second input CSV
        val pathSecondInputCSV = args(5)
        val keyPositionFirstInputCSV = args(6).toInt
        val keyPositionSecondInputCSV = args(7).toInt

        val firstSourceMap = csvToMap(pathInputCSV, keyPositionFirstInputCSV, csvSeparator, encodingCSV)
        val secondSourceMap = csvToMap(pathSecondInputCSV, keyPositionSecondInputCSV, csvSeparator, encodingCSV)
        val firstSourceMapIntersectSecondSourceMap = firstSourceMap.filterKeys(secondSourceMap.keySet)

        mapToCSV(pathOutputCSV, firstSourceMapIntersectSecondSourceMap, encodingCSV)

      // Get a single column
      case "getcolumn" =>
        // args(5): Position of the column
        val columnPosition = args(5).toInt

        csvColumnToFile(pathInputCSV, pathOutputCSV, columnPosition, csvSeparator, encodingCSV)

      // Add a column
      case "addcolumn" =>
        // args(5): Position of the new column
        // args(6): Value of the new column
        val newColumnPosition = args(5).toInt
        val newColumnValue = args(6)

        csvWithNewColumnToFile(pathInputCSV, pathOutputCSV, newColumnPosition, newColumnValue, csvSeparator, encodingCSV)

      case _ =>
    }
  }

  def csvToMap(filePath: String, keyPosition: Int, csvSeparator: Char, encoding: String): Map[String, String] = {
    val file = fromFile(filePath)(encoding)
    val map = file.getLines
      .map(line => {
        try {
          val lineParsed = new CSVReader(new StringReader(line), csvSeparator).readNext
          (
            lineParsed(keyPosition - 1),
            line
          )
        } catch {
          case e: Exception =>
            ("error", "")
        }
      })
      .filter(line => line._1 != "error")
      .toMap
    file.close
    map
  }

  def mapToCSV(filePath: String, map: Map[String, String], encoding: String): Unit = {
    val csv = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding))
    map.foreach(line => csv.write(line._2 + "\n"))
    csv.close
  }

  def csvColumnToFile(fileInPath: String, fileOutPath: String, columnPosition: Int, csvSeparator: Char, encoding: String): Unit = {
    val fileIn = fromFile(fileInPath)(encoding)
    val fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPath), encoding))
    fileIn.getLines.foreach(line => {
      try {
        val lineParsed = new CSVReader(new StringReader(line), csvSeparator).readNext
        fileOut.write(lineParsed(columnPosition - 1) + "\n")
      } catch {
        case e: Exception =>
      }
    })
    fileIn.close
    fileOut.close
  }

  def csvWithNewColumnToFile(fileInPath: String, fileOutPath: String, newColumnPosition: Int, newColumnValue: String, csvSeparator: Char, encoding: String): Unit = {
    val fileIn = fromFile(fileInPath)(encoding)
    val fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPath), encoding))
    fileIn.getLines.foreach(line => {
      try {
        var lineParsed = new CSVReader(new StringReader(line), csvSeparator).readNext
        var idx = lineParsed.size
        while(idx >= newColumnPosition) {
          if(idx == lineParsed.size) lineParsed = lineParsed :+ lineParsed(idx - 1)
          else lineParsed(idx) = lineParsed(idx - 1)
          idx -= 1
        }
        if(idx == lineParsed.size) lineParsed = lineParsed :+ newColumnValue
        else lineParsed(idx) = newColumnValue
        fileOut.write(lineParsed.mkString(csvSeparator.toString) + "\n")
      } catch {
        case e: Exception =>
      }
    })
    fileIn.close
    fileOut.close
  }
}
