package com.example.jaynoyar.ui.hero

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance 3D Isometric Furniture Workshop Canvas.
 * Renders an interactive 3D architectural craft environment with depth layers,
 * artisan station markers, rotating blade mechanics, and ambient workshop lighting.
 */
@Composable
fun Workshop3DCanvas(
    modifier: Modifier = Modifier,
    activeStationIndex: Int = 0,
    reducedMotion: Boolean = false,
    onStationSelect: (Int) -> Unit = {}
) {
    var rotationAngleX by remember { mutableFloatStateOf(0f) }
    var rotationAngleY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "workshop_anim")
    val bladeRotation by if (reducedMotion) {
        remember { mutableFloatStateOf(0f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "saw_blade"
        )
    }

    val pulseGlow by if (reducedMotion) {
        remember { mutableFloatStateOf(0.6f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ambient_glow"
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationAngleX = (rotationAngleX + dragAmount.y * 0.2f).coerceIn(-25f, 25f)
                    rotationAngleY = (rotationAngleY + dragAmount.x * 0.2f).coerceIn(-35f, 35f)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f + rotationAngleY * 2.5f
        val centerY = h / 2f + rotationAngleX * 2.0f

        // Draw ambient atmospheric glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x3DF59E0B).copy(alpha = pulseGlow * 0.4f),
                    Color(0x103B82F6),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = w.coerceAtLeast(h) * 0.7f
            ),
            radius = w.coerceAtLeast(h) * 0.7f,
            center = Offset(centerX, centerY)
        )

        // Draw Isometric Grid Platform (Workshop Floor)
        drawIsometricWorkshopFloor(centerX, centerY + 40f, w * 0.9f, h * 0.65f)

        // Station 1: Woodcutting & CNC Station (top-left)
        drawStationWorkbench(
            cx = centerX - w * 0.22f,
            cy = centerY - h * 0.12f,
            stationWidth = 110f,
            stationLength = 70f,
            stationHeight = 45f,
            woodTone = Color(0xFF854D0E),
            label = "برش و CNC",
            isHighlighted = activeStationIndex == 0,
            bladeRot = bladeRotation
        )

        // Station 2: Carpentry & Joinery (center-left)
        drawArtisanChairStructure(
            cx = centerX - w * 0.05f,
            cy = centerY + h * 0.08f,
            chairScale = 1.1f,
            isHighlighted = activeStationIndex == 1
        )

        // Station 3: Finishing & Polyester Lacquer (top-right)
        drawPolishingStation(
            cx = centerX + w * 0.22f,
            cy = centerY - h * 0.10f,
            isHighlighted = activeStationIndex == 2,
            pulse = pulseGlow
        )

        // Station 4: Luxury Upholstery & Leather Craft (bottom-right)
        drawUpholsterySofaFrame(
            cx = centerX + w * 0.16f,
            cy = centerY + h * 0.12f,
            isHighlighted = activeStationIndex == 3
        )

        // Draw artisan status beacons / telemetry nodes
        drawArtisanBeacon(
            cx = centerX - w * 0.22f,
            cy = centerY - h * 0.22f,
            name = "استاد داوودی",
            time = "۰۸:۰۲ (حاضر)",
            color = Color(0xFF10B981)
        )

        drawArtisanBeacon(
            cx = centerX - w * 0.05f,
            cy = centerY - h * 0.02f,
            name = "استاد کریمی",
            time = "۰۷:۵۵ (اضافه‌کار)",
            color = Color(0xFFF59E0B)
        )

        drawArtisanBeacon(
            cx = centerX + w * 0.22f,
            cy = centerY - h * 0.20f,
            name = "رضا نوری",
            time = "۰۸:۲۰ (تاخیر مجاز)",
            color = Color(0xFF38BDF8)
        )
    }
}

/**
 * Draws an isometric wooden workshop floor with planks and perspective lines.
 */
private fun DrawScope.drawIsometricWorkshopFloor(cx: Float, cy: Float, width: Float, height: Float) {
    val floorPath = Path().apply {
        moveTo(cx, cy - height / 2f) // Top
        lineTo(cx + width / 2f, cy) // Right
        lineTo(cx, cy + height / 2f) // Bottom
        lineTo(cx - width / 2f, cy) // Left
        close()
    }

    // Floor fill with walnut gradient
    drawPath(
        path = floorPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF261811), Color(0xFF1E130D), Color(0xFF150D09)),
            start = Offset(cx - width / 2f, cy - height / 2f),
            end = Offset(cx + width / 2f, cy + height / 2f)
        )
    )

    // Floor isometric grid gridlines
    val steps = 8
    for (i in 1 until steps) {
        val fraction = i / steps.toFloat()
        // Left-to-right grid
        val start1 = Offset(cx - width / 2f + (width / 2f) * fraction, cy + (height / 2f) * (fraction - 1f) * -0.5f)
        val end1 = Offset(cx + (width / 2f) * fraction, cy + (height / 2f) * fraction)
        drawLine(
            color = Color(0x25F59E0B),
            start = Offset(
                cx + (width / 2f) * (fraction - 0.5f) * 2f,
                cy - (height / 2f) * (1f - fraction)
            ),
            end = Offset(
                cx + (width / 2f) * (fraction - 0.5f) * 2f,
                cy + (height / 2f) * fraction
            ),
            strokeWidth = 0.8f
        )
    }

    // Outer rim highlight
    drawPath(
        path = floorPath,
        color = Color(0x40D97706),
        style = Stroke(width = 1.5f)
    )
}

/**
 * Station 1: 3D Workbench with rotating circular saw blade.
 */
private fun DrawScope.drawStationWorkbench(
    cx: Float,
    cy: Float,
    stationWidth: Float,
    stationLength: Float,
    stationHeight: Float,
    woodTone: Color,
    label: String,
    isHighlighted: Boolean,
    bladeRot: Float
) {
    val benchTop = Path().apply {
        moveTo(cx, cy - stationLength / 2f)
        lineTo(cx + stationWidth / 2f, cy)
        lineTo(cx, cy + stationLength / 2f)
        lineTo(cx - stationWidth / 2f, cy)
        close()
    }

    // Top face
    drawPath(
        path = benchTop,
        color = if (isHighlighted) Color(0xFFD97706) else woodTone,
        style = Fill
    )
    drawPath(
        path = benchTop,
        color = Color(0xFFFDE68A),
        style = Stroke(width = if (isHighlighted) 2f else 0.8f)
    )

    // Front-right leg/skirt
    val skirtRight = Path().apply {
        moveTo(cx, cy + stationLength / 2f)
        lineTo(cx + stationWidth / 2f, cy)
        lineTo(cx + stationWidth / 2f, cy + stationHeight)
        lineTo(cx, cy + stationLength / 2f + stationHeight)
        close()
    }
    drawPath(skirtRight, color = Color(0xFF1C130D))

    // Front-left leg/skirt
    val skirtLeft = Path().apply {
        moveTo(cx, cy + stationLength / 2f)
        lineTo(cx - stationWidth / 2f, cy)
        lineTo(cx - stationWidth / 2f, cy + stationHeight)
        lineTo(cx, cy + stationLength / 2f + stationHeight)
        close()
    }
    drawPath(skirtLeft, color = Color(0xFF140D08))

    // Rotating Circular Blade above bench
    rotate(bladeRot, pivot = Offset(cx, cy - 10f)) {
        drawCircle(
            color = Color(0xFFE2E8F0),
            radius = 16f,
            center = Offset(cx, cy - 10f),
            style = Stroke(width = 2.5f)
        )
        // Blade teeth
        for (a in 0 until 6) {
            val angleRad = (a * 60f) * PI / 180f
            val px = cx + (cos(angleRad) * 16f).toFloat()
            val py = cy - 10f + (sin(angleRad) * 16f).toFloat()
            drawCircle(color = Color(0xFFF59E0B), radius = 2f, center = Offset(px, py))
        }
    }
}

/**
 * Station 2: Isometric Artisan Armchair Frame.
 */
private fun DrawScope.drawArtisanChairStructure(
    cx: Float,
    cy: Float,
    chairScale: Float,
    isHighlighted: Boolean
) {
    val s = chairScale * 30f

    // Cushion seat
    val seat = Path().apply {
        moveTo(cx, cy - s * 0.4f)
        lineTo(cx + s * 0.8f, cy)
        lineTo(cx, cy + s * 0.4f)
        lineTo(cx - s * 0.8f, cy)
        close()
    }
    drawPath(seat, color = if (isHighlighted) Color(0xFFF59E0B) else Color(0xFFB45309))
    drawPath(seat, color = Color(0xFFFEF3C7), style = Stroke(width = 1.2f))

    // Backrest
    val back = Path().apply {
        moveTo(cx - s * 0.6f, cy - s * 0.2f)
        lineTo(cx, cy - s * 0.6f)
        lineTo(cx, cy - s * 1.4f)
        lineTo(cx - s * 0.6f, cy - s * 1.0f)
        close()
    }
    drawPath(back, color = Color(0xFF92400E))
    drawPath(back, color = Color(0xFFFDE68A), style = Stroke(width = 1f))

    // Four Legs
    drawLine(Color(0xFF78350F), Offset(cx, cy + s * 0.4f), Offset(cx, cy + s * 0.9f), strokeWidth = 3f)
    drawLine(Color(0xFF78350F), Offset(cx + s * 0.8f, cy), Offset(cx + s * 0.8f, cy + s * 0.5f), strokeWidth = 3f)
    drawLine(Color(0xFF78350F), Offset(cx - s * 0.8f, cy), Offset(cx - s * 0.8f, cy + s * 0.5f), strokeWidth = 3f)
}

/**
 * Station 3: Finishing & Polyester Polishing Booth.
 */
private fun DrawScope.drawPolishingStation(
    cx: Float,
    cy: Float,
    isHighlighted: Boolean,
    pulse: Float
) {
    // Glass booth outline
    val booth = Path().apply {
        moveTo(cx, cy - 35f)
        lineTo(cx + 45f, cy)
        lineTo(cx, cy + 35f)
        lineTo(cx - 45f, cy)
        close()
    }
    drawPath(booth, color = Color(0x3038BDF8))
    drawPath(
        booth,
        color = if (isHighlighted) Color(0xFF06B6D4) else Color(0x8038BDF8),
        style = Stroke(width = 1.5f)
    )

    // Glow core
    drawCircle(
        color = Color(0xFF06B6D4).copy(alpha = pulse * 0.5f),
        radius = 20f,
        center = Offset(cx, cy)
    )
}

/**
 * Station 4: Luxury Chesterfield Sofa in Progress.
 */
private fun DrawScope.drawUpholsterySofaFrame(
    cx: Float,
    cy: Float,
    isHighlighted: Boolean
) {
    val sw = 70f
    val sl = 35f

    val sofaBase = Path().apply {
        moveTo(cx, cy - sl / 2f)
        lineTo(cx + sw / 2f, cy)
        lineTo(cx, cy + sl / 2f)
        lineTo(cx - sw / 2f, cy)
        close()
    }
    drawPath(sofaBase, color = if (isHighlighted) Color(0xFF4338CA) else Color(0xFF312E81))
    drawPath(sofaBase, color = Color(0xFFA5B4FC), style = Stroke(width = 1f))

    // Sofa backrest
    val backrest = Path().apply {
        moveTo(cx - sw / 2f, cy)
        lineTo(cx, cy - sl / 2f)
        lineTo(cx, cy - sl / 2f - 24f)
        lineTo(cx - sw / 2f, cy - 24f)
        close()
    }
    drawPath(backrest, color = Color(0xFF1E1B4B))
    drawPath(backrest, color = Color(0xFF818CF8), style = Stroke(width = 1f))

    // Button tufting dots (لمسه دوزی)
    drawCircle(Color(0xFFC7D2FE), radius = 2f, center = Offset(cx - sw * 0.25f, cy - 12f))
    drawCircle(Color(0xFFC7D2FE), radius = 2f, center = Offset(cx - sw * 0.1f, cy - 15f))
}

/**
 * Draws an interactive status telemetry beacon for active artisans.
 */
private fun DrawScope.drawArtisanBeacon(
    cx: Float,
    cy: Float,
    name: String,
    time: String,
    color: Color
) {
    // Pulse ring
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = 14f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = color,
        radius = 6f,
        center = Offset(cx, cy)
    )

    // Vertical indicator pin
    drawLine(
        color = color.copy(alpha = 0.6f),
        start = Offset(cx, cy),
        end = Offset(cx, cy - 25f),
        strokeWidth = 1.2f
    )

    // Floating micro card
    drawRoundRect(
        color = Color(0xE60F172A),
        topLeft = Offset(cx - 40f, cy - 48f),
        size = Size(80f, 22f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = color.copy(alpha = 0.8f),
        topLeft = Offset(cx - 40f, cy - 48f),
        size = Size(80f, 22f),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 0.8f)
    )
}
