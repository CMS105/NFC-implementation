package com.example.nfclpgreader.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.GasSevaNavyDark
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** What the scanner is showing. Map your ScanUiState onto this (see ScanStage.kt). */
enum class ScanPhase { Idle, Reading, Success, Error }

private const val JETS = 56            // burner holes around the ring
private const val IGNITE_MS = 700      // spark lap time
private val FlameBlue = Color(0xFF3DA9FF)
private val FlameCore = Color(0xFFD6F1FF)
private val FlameTip = Color(0xFFFFA63D)
private val Ember = Color(0xFFE5484D)

/**
 * "Burner ring" NFC scanner. Idle: a gas-stove plate with an NFC field breathing in the middle.
 * Reading: a spark laps the ring and lights the jets (like a piezo igniter).
 * Success: the flames die out completely and a green badge with a white tick pops in (nothing covers it).
 * Error: flames sputter out in red.
 *
 * [onResolved] fires once the ignition lap has finished and the result starts to show,
 * so play the success/error sound there, not when the state first changes.
 */
@Composable
fun FlameRingScanner(
    phase: ScanPhase,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    onResolved: (ScanPhase) -> Unit = {},
) {
    val ignite = remember { Animatable(0f) }
    val settle = remember { Animatable(0f) }
    val fail = remember { Animatable(0f) }
    val resolved by rememberUpdatedState(onResolved)
    val clock by rememberInfiniteTransition(label = "clock").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "clockValue",
    )

    LaunchedEffect(phase) {
        suspend fun finishLap(to: Float) {
            val left = (to - ignite.value).coerceAtLeast(0f)
            if (left > 0f) ignite.animateTo(to, tween((left * IGNITE_MS).toInt().coerceAtLeast(1), easing = LinearEasing))
        }
        when (phase) {
            ScanPhase.Idle -> { settle.snapTo(0f); fail.snapTo(0f); ignite.animateTo(0f, tween(400)) }
            ScanPhase.Reading -> { settle.snapTo(0f); fail.snapTo(0f); finishLap(1f) }
            ScanPhase.Success -> { fail.snapTo(0f); finishLap(1f); resolved(phase); settle.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
            ScanPhase.Error -> { settle.snapTo(0f); finishLap(0.3f); resolved(phase); fail.animateTo(1f, tween(900)) }
        }
    }

    Canvas(modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val c = center
        val ringR = r * 0.74f
        val lit = ignite.value
        val f = fail.value
        val s = settle.value
        val shakeX = if (f > 0f && f < 1f) (sin(f * 40f) * (1f - f) * r * 0.03f) else 0f
        val cx = c.x + shakeX

        // plate
        drawCircle(Brush.radialGradient(listOf(GasSevaNavy, GasSevaNavyDark), Offset(cx, c.y), r), r, Offset(cx, c.y))
        // flames are fully gone by s = 0.4 so the tick is never covered
        val flameAlpha = (1f - s / 0.4f).coerceIn(0f, 1f)
        val glow = lit * (1f - f) * 0.30f * flameAlpha
        if (glow > 0f) drawCircle(Brush.radialGradient(listOf(FlameBlue.copy(alpha = glow), Color.Transparent), Offset(cx, c.y), r), r, Offset(cx, c.y))
        if (s > 0f) drawCircle(Brush.radialGradient(listOf(GasSevaGreen.copy(alpha = 0.22f * s), Color.Transparent), Offset(cx, c.y), r), r, Offset(cx, c.y))

        // jets
        val tau = (2 * PI).toFloat()
        for (i in 0 until JETS) {
            val a = -tau / 4f + i * tau / JETS
            val dx = cos(a); val dy = sin(a)
            val p0 = Offset(cx + dx * ringR, c.y + dy * ringR)
            val gone = f > ((i * 37) % JETS) / JETS.toFloat() * 0.8f
            val on = (((lit * JETS) - i) / 4f).coerceIn(0f, 1f) * (if (gone) 0f else 1f) * flameAlpha
            val breathe = 0.5f + 0.5f * sin(tau * (clock * 3f - i / JETS.toFloat() * 2f))
            if (on <= 0.01f) {
                drawCircle(FlameBlue.copy(alpha = (0.12f + 0.30f * breathe * (1f - lit)) * flameAlpha), r * 0.013f, p0)
                continue
            }
            val amp = 0.25f
            val len = r * (0.03f + 0.10f * on * (1f - amp + amp * sin(tau * clock * 9f + i * 1.9f)))
            val p1 = Offset(p0.x + dx * len, p0.y + dy * len)
            val col = lerp(lerp(FlameBlue, FlameCore, on), Ember, f)
            drawLine(col, p0, p1, strokeWidth = r * 0.028f, cap = StrokeCap.Round)
            if (f == 0f) drawCircle(FlameTip.copy(alpha = on * 0.9f), r * 0.012f, p1)
        }

        // spark
        if (lit in 0.01f..0.99f && f == 0f) {
            val a = -tau / 4f + lit * tau
            val sp = Offset(cx + cos(a) * ringR, c.y + sin(a) * ringR)
            drawCircle(Brush.radialGradient(listOf(Color.White, Color.Transparent), sp, r * 0.10f), r * 0.10f, sp)
        }

        // idle: NFC field (three arcs + soft pulse)
        val idleA = (1f - lit * 1.6f).coerceIn(0f, 1f)
        if (idleA > 0f) {
            val ox = cx - r * 0.16f
            for (k in 0..2) {
                val w = (clock * 4f + k / 3f) % 1f
                val rad = r * (0.12f + 0.08f * k)
                drawArc(FlameBlue.copy(alpha = idleA * (0.35f + 0.65f * (1f - w))), -38f, 76f, false,
                    Offset(ox - rad, c.y - rad), Size(rad * 2, rad * 2),
                    style = Stroke(r * 0.03f, cap = StrokeCap.Round))
            }
            drawCircle(FlameBlue.copy(alpha = idleA), r * 0.025f, Offset(ox, c.y))
        }

        // success: flames are gone; green ring + solid green badge + bold white tick on top
        if (s > 0f) {
            // soft green shockwave
            drawCircle(GasSevaGreen.copy(alpha = 0.45f * (1f - s)), r * (0.25f + 0.65f * s), Offset(cx, c.y), style = Stroke(r * 0.02f))
            // steady ring where the jets were
            val ringA = ((s - 0.15f) / 0.35f).coerceIn(0f, 1f)
            drawCircle(GasSevaGreen.copy(alpha = ringA), ringR, Offset(cx, c.y), style = Stroke(r * 0.03f))
            // badge pops in (ease-out)
            val t = (s / 0.5f).coerceIn(0f, 1f)
            val pop = 1f - (1f - t) * (1f - t) * (1f - t)
            val br = r * 0.40f * pop
            drawCircle(GasSevaGreenDark, br * 1.08f, Offset(cx, c.y))
            drawCircle(GasSevaGreen, br, Offset(cx, c.y))
            // tick draws itself in white
            val p = ((s - 0.30f) / 0.45f).coerceIn(0f, 1f)
            if (p > 0f) {
                val path = Path().apply {
                    moveTo(cx - r * 0.17f, c.y + r * 0.01f); lineTo(cx - r * 0.05f, c.y + r * 0.13f); lineTo(cx + r * 0.19f, c.y - r * 0.12f)
                }
                val pm = PathMeasure().apply { setPath(path, false) }
                val seg = Path()
                pm.getSegment(0f, pm.length * p, seg, true)
                drawPath(seg, Color.White, style = Stroke(r * 0.075f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }

        // error: cross
        if (f > 0.4f) {
            val a = ((f - 0.4f) / 0.6f).coerceIn(0f, 1f)
            val d = r * 0.11f
            drawLine(Ember.copy(alpha = a), Offset(cx - d, c.y - d), Offset(cx + d, c.y + d), r * 0.045f, StrokeCap.Round)
            drawLine(Ember.copy(alpha = a), Offset(cx + d, c.y - d), Offset(cx - d, c.y + d), r * 0.045f, StrokeCap.Round)
        }
    }
}
