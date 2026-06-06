package com.safecoin.safecoin.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val blocks = parseMarkdownBlocks(markdown)
    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Spacer -> Spacer(modifier = Modifier.height(8.dp))
                is MarkdownBlock.Heading -> MarkdownHeading(block.text, block.level)
                is MarkdownBlock.Bullet -> MarkdownBullet(block.text)
                is MarkdownBlock.Numbered -> MarkdownNumbered(block.number, block.text)
                is MarkdownBlock.Paragraph -> Text(
                    text = parseInlineMarkdown(block.text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                is MarkdownBlock.Table -> MarkdownTable(block.rows, block.hasHeader)
            }
        }
    }
}

private sealed interface MarkdownBlock {
    data object Spacer : MarkdownBlock
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Bullet(val text: String) : MarkdownBlock
    data class Numbered(val number: String, val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    data class Table(val rows: List<List<String>>, val hasHeader: Boolean) : MarkdownBlock
}

private fun parseMarkdownBlocks(markdown: String): List<MarkdownBlock> {
    val lines = markdown.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var index = 0

    while (index < lines.size) {
        val line = lines[index].trim()

        when {
            line.isBlank() -> {
                blocks += MarkdownBlock.Spacer
                index++
            }
            isTableRow(line) -> {
                val tableLines = mutableListOf<String>()
                while (index < lines.size && isTableRow(lines[index].trim())) {
                    tableLines += lines[index].trim()
                    index++
                }
                blocks += parseTableBlock(tableLines)
            }
            line.startsWith("### ") -> {
                blocks += MarkdownBlock.Heading(3, line.removePrefix("### "))
                index++
            }
            line.startsWith("## ") -> {
                blocks += MarkdownBlock.Heading(2, line.removePrefix("## "))
                index++
            }
            line.startsWith("# ") -> {
                blocks += MarkdownBlock.Heading(1, line.removePrefix("# "))
                index++
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                blocks += MarkdownBlock.Bullet(line.drop(2))
                index++
            }
            line.matches(Regex("^\\d+\\.\\s+.*")) -> {
                blocks += MarkdownBlock.Numbered(
                    number = line.substringBefore("."),
                    text = line.substringAfter(". ").trim(),
                )
                index++
            }
            else -> {
                blocks += MarkdownBlock.Paragraph(line)
                index++
            }
        }
    }

    return blocks
}

private fun isTableRow(line: String): Boolean {
    if (!line.contains('|')) return false
    val cells = parseTableCells(line)
    return cells.size >= 2 || (line.startsWith("|") && line.endsWith("|"))
}

private fun isTableSeparator(line: String): Boolean {
    val cells = parseTableCells(line)
    if (cells.isEmpty()) return false
    return cells.all { cell -> cell.matches(Regex("^:?-{1,}:?$")) }
}

private fun parseTableCells(line: String): List<String> {
    var trimmed = line.trim()
    while (trimmed.startsWith('|')) trimmed = trimmed.removePrefix("|")
    while (trimmed.endsWith('|')) trimmed = trimmed.removeSuffix("|")
    if (trimmed.isEmpty()) return emptyList()
    return trimmed.split('|').map { it.trim() }
}

private fun parseTableBlock(lines: List<String>): MarkdownBlock.Table {
    val dataLines = lines.filterNot { isTableSeparator(it) }
    val rows = dataLines.map { parseTableCells(it) }
    val hasHeader = lines.size >= 2 && lines.getOrNull(1)?.let { isTableSeparator(it) } == true
    return MarkdownBlock.Table(rows = rows, hasHeader = hasHeader)
}

@Composable
private fun MarkdownTable(
    rows: List<List<String>>,
    hasHeader: Boolean,
) {
    if (rows.isEmpty()) return

    val columnCount = rows.maxOf { it.size }
    val normalizedRows = rows.map { row ->
        row + List(columnCount - row.size) { "" }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(4.dp),
        ) {
            normalizedRows.forEachIndexed { rowIndex, row ->
                val isHeaderRow = hasHeader && rowIndex == 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isHeaderRow) {
                                Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                )
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    row.forEach { cell ->
                        Text(
                            text = parseInlineMarkdown(cell.ifBlank { " " }),
                            style = if (isHeaderRow) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodySmall
                            },
                            fontWeight = if (isHeaderRow) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .width(120.dp)
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                        )
                    }
                }
                if (isHeaderRow || rowIndex < normalizedRows.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                }
            }
        }
    }
}

@Composable
private fun MarkdownHeading(text: String, level: Int) {
    val style = when (level) {
        1 -> MaterialTheme.typography.titleLarge
        2 -> MaterialTheme.typography.titleMedium
        else -> MaterialTheme.typography.titleSmall
    }
    Text(
        text = parseInlineMarkdown(text),
        style = style,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun MarkdownBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MarkdownNumbered(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(24.dp),
        )
        Text(
            text = parseInlineMarkdown(text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun parseInlineMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    var index = 0
    while (index < text.length) {
        when {
            text.startsWith("**", index) -> {
                val end = text.indexOf("**", index + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(index + 2, end))
                    }
                    index = end + 2
                } else {
                    append(text[index])
                    index++
                }
            }
            text.startsWith("*", index) && !text.startsWith("**", index) -> {
                val end = text.indexOf('*', index + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(index + 1, end))
                    }
                    index = end + 1
                } else {
                    append(text[index])
                    index++
                }
            }
            text.startsWith("`", index) -> {
                val end = text.indexOf('`', index + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(text.substring(index + 1, end))
                    }
                    index = end + 1
                } else {
                    append(text[index])
                    index++
                }
            }
            else -> {
                append(text[index])
                index++
            }
        }
    }
}
