package com.example.jaynoyar.excel

import android.content.Context
import android.net.Uri
import com.example.jaynoyar.calculator.AttendanceCalculationBreakdown
import com.example.jaynoyar.calculator.AttendanceCalculator
import com.example.jaynoyar.calendar.DateUtils
import com.example.jaynoyar.calendar.JalaliDate
import com.example.jaynoyar.data.model.AttendanceEntity
import com.example.jaynoyar.data.model.EmployeeEntity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Native OpenXML (.xlsx) generator and parser for Jayno Yar.
 * Generates genuine Microsoft Excel (.xlsx) workbooks with styling, headers, and Persian UTF-8 support.
 * Also parses uploaded .xlsx spreadsheets with row/column error tracking.
 */
object XlsxEngine {

    data class ImportRowPreview(
        val rowNumber: Int,
        val employeeId: String,
        val employeeName: String?,
        val dateString: String,
        val jalaliDateString: String,
        val checkInTime: String?,
        val checkOutTime: String?,
        val breakMinutes: Int,
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    data class ImportResult(
        val totalRows: Int,
        val validRows: List<ImportRowPreview>,
        val invalidRows: List<ImportRowPreview>,
        val duplicateRows: List<ImportRowPreview>
    )

    /**
     * Escapes XML characters and prevents formula injection (=, +, -, @)
     */
    private fun escapeXml(text: String, sanitizeFormula: Boolean = true): String {
        var str = text
        if (sanitizeFormula && (str.startsWith("=") || str.startsWith("+") || str.startsWith("-") || str.startsWith("@"))) {
            str = "'$str"
        }
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    /**
     * Builds standard OpenXML [Content_Types].xml
     */
    private fun buildContentTypesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""
    }

    private fun buildRelsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""
    }

    private fun buildWorkbookXml(sheetName: String): String {
        val safeSheetName = escapeXml(sheetName)
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="$safeSheetName" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>"""
    }

    private fun buildWorkbookRelsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""
    }

    private fun buildStylesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="3">
    <font><name val="Vazirmatn"/><sz val="11"/><color rgb="FF1F2937"/></font>
    <font><b/><name val="Vazirmatn"/><sz val="12"/><color rgb="FFFFFFFF"/></font>
    <font><b/><name val="Vazirmatn"/><sz val="11"/><color rgb="FF1E293B"/></font>
  </fonts>
  <fills count="4">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF1E293B"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFF1F5F9"/></patternFill></fill>
  </fills>
  <borders count="2">
    <border><left/><right/><top/><bottom/><diagonal/></border>
    <border>
      <left style="thin"><color rgb="FFE2E8F0"/></left>
      <right style="thin"><color rgb="FFE2E8F0"/></right>
      <top style="thin"><color rgb="FFE2E8F0"/></top>
      <bottom style="thin"><color rgb="FFE2E8F0"/></bottom>
    </border>
  </borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="4">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="1" applyFont="1" applyBorder="1"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="1" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
      <alignment horizontal="center" vertical="center" readingOrder="2"/>
    </xf>
    <xf numFmtId="0" fontId="0" fillId="3" borderId="1" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
      <alignment horizontal="center" vertical="center" readingOrder="2"/>
    </xf>
    <xf numFmtId="0" fontId="2" fillId="0" borderId="1" applyFont="1" applyBorder="1" applyAlignment="1">
      <alignment horizontal="center" vertical="center" readingOrder="2"/>
    </xf>
  </cellXfs>
</styleSheet>"""
    }

    private fun columnLetter(colIdx: Int): String {
        var n = colIdx
        var result = ""
        while (n >= 0) {
            result = ('A'.code + (n % 26)).toChar() + result
            n = (n / 26) - 1
        }
        return result
    }

    /**
     * Generates a complete .xlsx zip archive byte array
     */
    fun createWorkbookBytes(
        sheetName: String,
        headers: List<String>,
        rows: List<List<Any?>>
    ): ByteArray {
        val sheetXml = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
            append("""<sheetViews><sheetView tabSelected="1" workbookViewId="0" rightToLeft="1"/></sheetViews>""")
            append("""<sheetData>""")

            // Header row (style 1: dark blue header, white bold text)
            append("""<row r="1" ht="28" customHeight="1">""")
            headers.forEachIndexed { colIdx, header ->
                val cellRef = "${columnLetter(colIdx)}1"
                append("""<c r="$cellRef" s="1" t="inlineStr"><is><t>${escapeXml(header)}</t></is></c>""")
            }
            append("""</row>""")

            // Data rows
            rows.forEachIndexed { rowIdx, rowData ->
                val rNum = rowIdx + 2
                val styleIdx = if (rowIdx % 2 == 1) 2 else 0
                append("""<row r="$rNum" ht="22" customHeight="1">""")
                rowData.forEachIndexed { colIdx, value ->
                    val cellRef = "${columnLetter(colIdx)}$rNum"
                    when (value) {
                        null -> {
                            append("""<c r="$cellRef" s="$styleIdx"/>""")
                        }
                        is Number -> {
                            append("""<c r="$cellRef" s="$styleIdx"><v>$value</v></c>""")
                        }
                        else -> {
                            val textVal = value.toString()
                            append("""<c r="$cellRef" s="$styleIdx" t="inlineStr"><is><t>${escapeXml(textVal)}</t></is></c>""")
                        }
                    }
                }
                append("""</row>""")
            }

            append("""</sheetData>""")
            append("""</worksheet>""")
        }

        val bos = ByteArrayOutputStream()
        ZipOutputStream(bos).use { zos ->
            // [Content_Types].xml
            zos.putNextEntry(ZipEntry("[Content_Types].xml"))
            zos.write(buildContentTypesXml().toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // _rels/.rels
            zos.putNextEntry(ZipEntry("_rels/.rels"))
            zos.write(buildRelsXml().toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // xl/workbook.xml
            zos.putNextEntry(ZipEntry("xl/workbook.xml"))
            zos.write(buildWorkbookXml(sheetName).toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // xl/_rels/workbook.xml.rels
            zos.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
            zos.write(buildWorkbookRelsXml().toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // xl/styles.xml
            zos.putNextEntry(ZipEntry("xl/styles.xml"))
            zos.write(buildStylesXml().toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // xl/worksheets/sheet1.xml
            zos.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
            zos.write(sheetXml.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        return bos.toByteArray()
    }

    /**
     * Exports attendance report with comprehensive breakdown (net, overtime, undertime, breaks)
     */
    fun exportAttendanceReportXlsx(
        context: Context,
        breakdowns: List<Pair<EmployeeEntity, AttendanceCalculationBreakdown>>,
        fileNamePrefix: String = "گزارش_تردد_جاینو_یار"
    ): File {
        val headers = listOf(
            "کد پرسنلی",
            "نام و نام‌خانوادگی",
            "بخش کارگاه",
            "تاریخ شمسی",
            "تاریخ میلادی",
            "زمان ورود",
            "زمان خروج",
            "استراحت (دقیقه)",
            "کارکرد ناخالص (دقیقه)",
            "کارکرد خالص (دقیقه)",
            "ساعات کارکرد",
            "موظفی روز (دقیقه)",
            "اضافه‌کار (دقیقه)",
            "کسرکار (دقیقه)",
            "تاخیر ورود (دقیقه)",
            "وضعیت تایید اضافه‌کار",
            "توضیحات و قوانین اعمال‌شده"
        )

        val rows = breakdowns.map { (emp, b) ->
            val localDate = try { LocalDate.parse(b.dateString) } catch (e: Exception) { LocalDate.now() }
            val jDate = JalaliDate.fromLocalDate(localDate)
            listOf(
                emp.id,
                emp.fullName,
                emp.department,
                jDate.formatFormatted(false),
                b.dateString,
                b.checkInTime ?: "-",
                b.checkOutTime ?: "-",
                b.unpaidBreakMinutes + b.paidBreakMinutes,
                b.grossMinutes,
                b.netWorkingMinutes,
                String.format("%.2f", b.netHoursDecimal),
                b.expectedMinutes,
                b.overtimeMinutes,
                b.undertimeMinutes,
                b.latenessMinutes,
                if (b.isOvertimeApproved) "تایید شده" else "رد شده",
                b.appliedRuleDescription
            )
        }

        val bytes = createWorkbookBytes("گزارش تردد کارگاه مبلمان جاینو", headers, rows)
        val file = File(context.cacheDir, "${fileNamePrefix}_${System.currentTimeMillis()}.xlsx")
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    /**
     * Exports blank attendance import template
     */
    fun exportBlankTemplateXlsx(context: Context, employees: List<EmployeeEntity>): File {
        val headers = listOf(
            "کد پرسنلی",
            "نام پرسنل (راهنما)",
            "بخش",
            "تاریخ شمسی (مثال: ۱۴۰۵/۰۸/۰۷)",
            "تاریخ میلادی (مثال: 2026-10-28)",
            "ساعت ورود (HH:mm)",
            "ساعت خروج (HH:mm)",
            "مدت استراحت ناهار (دقیقه)",
            "توضیحات"
        )

        val today = LocalDate.now()
        val jToday = JalaliDate.fromLocalDate(today)

        val sampleRows = employees.map { emp ->
            listOf(
                emp.id,
                emp.fullName,
                emp.department,
                jToday.formatFormatted(false),
                today.toString(),
                "08:00",
                "17:00",
                45,
                ""
            )
        }

        val bytes = createWorkbookBytes("قالب ثبت تردد جاینو یار", headers, sampleRows)
        val file = File(context.cacheDir, "قالب_اکسل_تردد_جاینو_یار.xlsx")
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    /**
     * Exports employee directory
     */
    fun exportEmployeeDirectoryXlsx(context: Context, employees: List<EmployeeEntity>): File {
        val headers = listOf(
            "کد پرسنلی",
            "نام",
            "نام خانوادگی",
            "بخش کارگاه",
            "سمت / تخصص",
            "کد ملی",
            "شماره تماس",
            "تاریخ استخدام",
            "وضعیت",
            "دستمزد ساعتی (تومان)",
            "توضیحات"
        )

        val rows = employees.map { emp ->
            listOf(
                emp.id,
                emp.firstName,
                emp.lastName,
                emp.department,
                emp.role,
                emp.nationalId,
                emp.phone,
                emp.hireDate,
                when (emp.status) {
                    "ACTIVE" -> "فعال"
                    "ON_LEAVE" -> "مرخصی"
                    "ARCHIVED" -> "بایگانی شده"
                    else -> emp.status
                },
                emp.baseHourlyWage,
                emp.notes
            )
        }

        val bytes = createWorkbookBytes("لیست پرسنل کارگاه جاینو", headers, rows)
        val file = File(context.cacheDir, "لیست_پرسنل_کارگاه_جاینو_${System.currentTimeMillis()}.xlsx")
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    /**
     * Parses an .xlsx file input stream and extracts attendance rows for preview and validation
     */
    fun parseXlsxAttendance(
        inputStream: InputStream,
        existingEmployees: List<EmployeeEntity>
    ): ImportResult {
        val empMap = existingEmployees.associateBy { it.id.trim().uppercase() }
        val allRows = mutableListOf<Map<Int, String>>()
        val sharedStrings = mutableListOf<String>()

        try {
            val zis = ZipInputStream(inputStream)
            var entry: ZipEntry? = zis.nextEntry
            var sheetBytes: ByteArray? = null

            while (entry != null) {
                when (entry.name) {
                    "xl/sharedStrings.xml" -> {
                        sharedStrings.addAll(parseSharedStrings(zis.readBytes()))
                    }
                    "xl/worksheets/sheet1.xml" -> {
                        sheetBytes = zis.readBytes()
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }

            if (sheetBytes != null) {
                allRows.addAll(parseSheetXml(sheetBytes, sharedStrings))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val validList = mutableListOf<ImportRowPreview>()
        val invalidList = mutableListOf<ImportRowPreview>()
        val duplicateList = mutableListOf<ImportRowPreview>()
        val seenKeys = mutableSetOf<String>()

        // Row 1 is header, so rows start at index 1 (Excel row 2)
        for (i in 1 until allRows.size) {
            val rMap = allRows[i]
            val rowNum = i + 1

            val rawEmpId = rMap[0]?.trim()?.uppercase() ?: ""
            val rawJalaliDate = rMap[3]?.trim() ?: ""
            val rawGregorianDate = rMap[4]?.trim() ?: ""
            val rawCheckIn = rMap[5]?.trim()
            val rawCheckOut = rMap[6]?.trim()
            val rawBreakMin = rMap[7]?.toIntOrNull() ?: 45

            val emp = empMap[rawEmpId]
            var errorMsg: String? = null

            if (rawEmpId.isBlank()) {
                errorMsg = "کد پرسنلی خالی است"
            } else if (emp == null) {
                errorMsg = "پرسنل با کد $rawEmpId در سیستم یافت نشد"
            }

            // Determine Gregorian date
            var targetDate: LocalDate? = null
            if (rawGregorianDate.isNotBlank()) {
                try {
                    targetDate = LocalDate.parse(DateUtils.toEnglishDigits(rawGregorianDate))
                } catch (e: Exception) {
                    // Try Jalali fallback
                }
            }
            if (targetDate == null && rawJalaliDate.isNotBlank()) {
                val parsedJalali = JalaliDate.parse(rawJalaliDate)
                if (parsedJalali != null) {
                    targetDate = parsedJalali.toLocalDate()
                } else if (errorMsg == null) {
                    errorMsg = "فرمت تاریخ شمسی نامعتبر است: $rawJalaliDate"
                }
            }
            if (targetDate == null && errorMsg == null) {
                errorMsg = "تاریخ معتبر یافت نشد"
            }

            val finalDateStr = targetDate?.toString() ?: LocalDate.now().toString()
            val finalJalaliStr = targetDate?.let { JalaliDate.fromLocalDate(it).formatFormatted(false) } ?: ""

            // Time validation
            val cinMin = AttendanceCalculator.parseTimeToMinutes(rawCheckIn)
            val coutMin = AttendanceCalculator.parseTimeToMinutes(rawCheckOut)
            if (rawCheckIn != null && rawCheckIn != "-" && cinMin == null && errorMsg == null) {
                errorMsg = "فرمت ساعت ورود نامعتبر است: $rawCheckIn"
            }
            if (rawCheckOut != null && rawCheckOut != "-" && coutMin == null && errorMsg == null) {
                errorMsg = "فرمت ساعت خروج نامعتبر است: $rawCheckOut"
            }

            val item = ImportRowPreview(
                rowNumber = rowNum,
                employeeId = rawEmpId,
                employeeName = emp?.fullName,
                dateString = finalDateStr,
                jalaliDateString = finalJalaliStr,
                checkInTime = if (cinMin != null) AttendanceCalculator.minutesToTimeString(cinMin) else null,
                checkOutTime = if (coutMin != null) AttendanceCalculator.minutesToTimeString(coutMin) else null,
                breakMinutes = rawBreakMin,
                isValid = errorMsg == null,
                errorMessage = errorMsg
            )

            val uniqueKey = "$rawEmpId-$finalDateStr"
            if (seenKeys.contains(uniqueKey)) {
                duplicateList.add(item.copy(isValid = false, errorMessage = "رکورد تکراری برای همین تاریخ در ردیف دیگر فایل وجود دارد"))
            } else {
                seenKeys.add(uniqueKey)
                if (item.isValid) {
                    validList.add(item)
                } else {
                    invalidList.add(item)
                }
            }
        }

        return ImportResult(
            totalRows = allRows.size - 1,
            validRows = validList,
            invalidRows = invalidList,
            duplicateRows = duplicateList
        )
    }

    private fun parseSharedStrings(bytes: ByteArray): List<String> {
        val result = mutableListOf<String>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(ByteArrayInputStream(bytes), "UTF-8")

        var eventType = parser.eventType
        var inText = false
        val currentStr = StringBuilder()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "t") inText = true
                    if (parser.name == "si") currentStr.clear()
                }
                XmlPullParser.TEXT -> {
                    if (inText) currentStr.append(parser.text)
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "t") inText = false
                    if (parser.name == "si") result.add(currentStr.toString())
                }
            }
            eventType = parser.next()
        }
        return result
    }

    private fun parseSheetXml(bytes: ByteArray, sharedStrings: List<String>): List<Map<Int, String>> {
        val rows = mutableListOf<Map<Int, String>>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(ByteArrayInputStream(bytes), "UTF-8")

        var eventType = parser.eventType
        var currentRowMap = mutableMapOf<Int, String>()
        var currentColIndex = 0
        var cellType: String? = null
        var inV = false
        var inT = false
        var inIs = false
        var cellText = StringBuilder()

        fun colIndexFromRef(ref: String): Int {
            var col = 0
            for (ch in ref) {
                if (ch in 'A'..'Z') {
                    col = col * 26 + (ch - 'A' + 1)
                } else break
            }
            return (col - 1).coerceAtLeast(0)
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "row" -> {
                            currentRowMap = mutableMapOf()
                        }
                        "c" -> {
                            val r = parser.getAttributeValue(null, "r") ?: "A1"
                            currentColIndex = colIndexFromRef(r)
                            cellType = parser.getAttributeValue(null, "t")
                            cellText.clear()
                        }
                        "v" -> inV = true
                        "t" -> inT = true
                        "is" -> inIs = true
                    }
                }
                XmlPullParser.TEXT -> {
                    if (inV || inT || inIs) {
                        cellText.append(parser.text)
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "v" -> inV = false
                        "t" -> inT = false
                        "is" -> inIs = false
                        "c" -> {
                            val raw = cellText.toString().trim()
                            val resolved = if (cellType == "s") {
                                val sIndex = raw.toIntOrNull()
                                if (sIndex != null && sIndex in sharedStrings.indices) {
                                    sharedStrings[sIndex]
                                } else raw
                            } else {
                                raw
                            }
                            currentRowMap[currentColIndex] = resolved
                        }
                        "row" -> {
                            rows.add(currentRowMap)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return rows
    }
}
