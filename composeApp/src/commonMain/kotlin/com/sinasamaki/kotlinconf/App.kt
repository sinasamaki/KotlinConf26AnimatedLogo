package com.sinasamaki.kotlinconf

import androidx.compose.animation.core.Animatable
import kotlin.math.PI
import kotlin.math.cos
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.sinasamaki.kotlinconf.utils.lineTo
import com.sinasamaki.kotlinconf.utils.moveTo
import com.sinasamaki.kotlinconf.utils.oscillateToZero
import kotlinconf.composeapp.generated.resources.Res
import kotlinconf.composeapp.generated.resources.kotlin
import org.jetbrains.compose.resources.painterResource
import kotlin.math.absoluteValue

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .safeContentPadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            Image(
                painter = painterResource(Res.drawable.kotlin),
                contentDescription = null,
                modifier = Modifier
                    .size(800.dp)
                    .alpha(0f)
            )

            KotlinLogo(
                modifier = Modifier
                    .size(800.dp)
                    .drawWithContent {

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF3E0850),
                                    Color(0x323E0850),
//                                    Color(0xFF120317),
                                ),
                                center = Offset(size.width, size.height * .5f)
                            ),
                            radius = size.width * 2
                        )

                        val inflatedBounds = size.toRect().inflate(100f)
                        layer(
                            inflatedBounds
                        ) {
                            this@drawWithContent.drawContent()
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFF3421C),
                                        Color(0xFFB935EF),
                                    ),
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, 0f),
                                ),
                                topLeft = inflatedBounds.topLeft,
                                size = inflatedBounds.size,
                                blendMode = BlendMode.SrcIn,
                            )
                        }
                    }
            )
        }
    }
}

private fun DrawScope.layer(
    bounds: Rect = size.toRect(),
    block: DrawScope.() -> Unit,
) =
    drawIntoCanvas { canvas ->
        canvas.withSaveLayer(
            bounds = bounds,
            paint = Paint(),
        ) { block() }
    }

const val strokeWidth = .5f
val brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFF2EF),
        Color(0xFFDAB6EA),
    ),
    start = Offset(0f, 128f),
    end = Offset(128f, 0f),
)

@Composable
fun KotlinLogo(modifier: Modifier = Modifier) {

    val cells = 128
    var componentSize by remember { mutableStateOf(IntSize.Zero) }
    var selectedCell by remember { mutableStateOf(Pair(0, 0)) }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            progress.animateTo(
                1f,
                animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
            )
//            progress.animateTo(0f, animationSpec = tween(durationMillis = 2000, easing = LinearEasing))
            progress.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellX = (offset.x / (componentSize.width.toFloat() / cells)).toInt()
                        .coerceIn(0, cells - 1)
                    val cellY = (offset.y / (componentSize.height.toFloat() / cells)).toInt()
                        .coerceIn(0, cells - 1)
                    selectedCell = Pair(cellX, cellY)
                }
            }
            .drawBehind {
                componentSize = IntSize(size.width.toInt(), size.height.toInt())
                val smoothProgress = FastOutSlowInEasing.transform(progress.value)


                scale(
                    scale = size.width / cells,
                    pivot = Offset.Zero,
                ) {

                    drawBoundary()

                    clipPath(
                        path = Path().apply {
                            moveTo(0, 0)
                            lineTo(0, 128)
                            lineTo(128, 128)
                            lineTo(64, 64)
                            lineTo(128, 0)
                            lineTo(0, 0)
                        }
                    ) {

                        drawTower(progress.value)

                        translate(
                            left = 45f
                        ) {
                            scale(
                                scaleX = -1f,
                                scaleY = 1f,
                                pivot = Offset(40f, 0f)
                            ) {
                                drawTower(progress.value)
                            }
                        }

                        opposingCircles(progress.value.oscillateToZero(3))
                        drawHeart(progress.value)
                        drawOlympicTrack(progress.value)
                        drawParallelLines(
                            vertical = true,
                            topLeft = Offset(97f, 0f),
                            size = Size(32f, 10f),
                            progress = progress.value
                        )

                        drawParallelLines(
                            vertical = false,
                            topLeft = Offset(8f, 44f),
                            size = Size(20f, 20f),
                            clip = Path().apply {
                                moveTo(8, 44)
                                lineTo(8, 52)
                                lineTo(18, 64)
                                lineTo(28, 52)
                                lineTo(28, 44)
                            },
                            progress = -progress.value
                        )

                        clipRect(
                            left = 52f
                        ) {
                            drawParallelLines(
                                vertical = true,
                                topLeft = Offset(52f, 10f),
                                size = Size(21f, 19f),
                                clip = Path().apply {
                                    moveTo(52, 10)
                                    lineTo(73, 29)
                                    lineTo(73, 10)
                                    close()
                                },
                                progress = -progress.value
                            )
                        }

                        drawParallelLines(
                            vertical = true,
                            topLeft = Offset(64f, 64f),
                            size = Size(24f, 21f),
                            clip = Path().apply {
                                moveTo(64, 64)
                                lineTo(64, 85)
                                lineTo(85, 85)
                            },
                            progress = progress.value
                        )

                        drawParallelLines(
                            vertical = false,
                            topLeft = Offset(53f, 85f),
                            size = Size(11f, 21f),
                            progress = progress.value
                        )

                        drawParallelLines(
                            vertical = false,
                            topLeft = Offset(112f, 112f),
                            size = Size(16f, 21f),
                            progress = -progress.value
                        )


                        drawPretzel(progress.value)
                        drawMascot(progress.value)
                        bottomCapsules(progress.value)

                        growingSemiCircles(
                            progress = progress.value,
                            bounds = Rect(
                                offset = Offset(0f, 23f),
                                size = Size(21f, 21f)
                            ),
                            vertical = true,
                        )
                        growingSemiCircles(
                            progress = progress.value,
                            bounds = Rect(
                                offset = Offset(91f, 106f),
                                size = Size(21f, 22f)
                            ),
                            vertical = false,
                        )
                    }
                }



                (0..cells).flatMap { i ->
                    (0..cells).map { j ->
                        Offset(
                            x = i * size.width / cells,
                            y = j * size.height / cells,
                        )
                    }
                }.let {
//                    drawPoints(
//                        points = it,
//                        color = Color.Cyan,
//                        pointMode = PointMode.Points,
//                        strokeWidth = 1f
//                    )
                }
            }
    ) {
    }
}



fun DrawScope.drawBoundary() {
    val boundary = Path().apply {
        moveTo(0, 0)
        lineTo(0, 128)
        lineTo(128, 128)
        lineTo(64, 64)
        lineTo(128, 0)
        close()

        moveTo(0, 64)
        lineTo(64, 64)

        moveTo(28, 0)
        lineTo(28, 128)

        moveTo(52, 0)
        lineTo(52, 64)

        moveTo(73, 0)
        lineTo(73, 55)

        moveTo(97, 0)
        lineTo(97, 31)

        moveTo(0, 106)
        lineTo(106, 106)

        moveTo(28, 85)
        lineTo(85, 85)

        moveTo(52, 39)
        lineTo(73, 39)

        moveTo(52, 29)
        lineTo(73, 29)

        moveTo(0, 44)
        lineTo(28, 44)

        moveTo(8, 44)
        lineTo(8, 106)

        moveTo(0, 85)
        lineTo(8, 85)

        moveTo(0, 23)
        lineTo(28, 23)

        moveTo(21, 23)
        lineTo(21, 44)


        moveTo(97, 10)
        lineTo(118, 10)

        moveTo(64, 64)
        lineTo(64, 106)

        moveTo(112, 112)
        lineTo(112, 128)

        moveTo(53, 85)
        lineTo(53, 106)

        moveTo(43, 85)
        lineTo(53, 96)
        lineTo(43, 106)
    }

    drawPath(
        path = boundary,
        brush = brush,
        style = Stroke(
            width = strokeWidth,
        )
    )
}

fun DrawScope.drawTower(
    progress: Float = 0f
) {
    val towerRidgesProgress = progress * .25f
    val tower = Path().apply {

        addArc(
            oval = Rect(
                offset = Offset(39f, 0f),
                size = Size(2f, 2f)
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 360f,
        )

        moveTo(40, 2)
        lineTo(40, 4)

        addArc(
            oval = Rect(
                offset = Offset(31f, 4f),
                size = Size(18f, 14f)
            ),
            startAngleDegrees = 26f,
            sweepAngleDegrees = -180f - 52f,
        )

        moveTo(32, 14)
        lineTo(48, 14)

        moveTo(32, 14)
        lineTo(32, 19)
        lineTo(28, 23)
        lineTo(32, 23)
        lineTo(34, 21)
        lineTo(46, 21)
        lineTo(48, 23)
        lineTo(52, 23)
        lineTo(48, 19)
        lineTo(48, 14)

        moveTo(31, 23)
        lineTo(31, 35)

        moveTo(34, 21)
        lineTo(34, 35)

        moveTo(49, 23)
        lineTo(49, 35)

        moveTo(46, 21)
        lineTo(46, 35)


        moveTo(31, 35)
        lineTo(28, 39)
        lineTo(32, 39)
        lineTo(34, 37)
        lineTo(46, 37)
        lineTo(48, 39)
        lineTo(52, 39)
        lineTo(49, 35)
        lineTo(31, 35)

        moveTo(34, 37)
        lineTo(34, 49)

        moveTo(46, 37)
        lineTo(46, 49)

        moveTo(28, 49)
        lineTo(52, 49)

        moveTo(28, 53)
        lineTo(31, 53)
        lineTo(33, 51)
        lineTo(47, 51)
        lineTo(49, 53)
        lineTo(52, 53)

        moveTo(33, 51)
        lineTo(33, 64)

        moveTo(47, 51)
        lineTo(47, 64)

        moveTo(37, 35)
        lineTo(37, 28)
        quadraticTo(
            37f, 26f,
            40f, 24f
        )
        quadraticTo(
            43f, 26f,
            43f, 28f
        )
        lineTo(43, 35)

        addRect(
            rect = Rect(
                offset = Offset(39f, 51f),
                size = Size(2f, 2f)
            ),
        )

        addRect(
            rect = Rect(
                offset = Offset(39f, 57f),
                size = Size(2f, 2f)
            ),
        )
    }

    clipRect(
        left = 28f,
        right = 52f,
    ) {
        drawPath(
            path = tower,
            brush = brush,
            style = Stroke(
                width = strokeWidth,
            )
        )
    }

    for (i in 0..4) {
        val progress = ((towerRidgesProgress * 2) + (i / 4f)) % 1f
        val ridge = Path().apply {
            moveTo(40, 4)
            relativeCubicTo(
                lerp(.0001f, -.0001f, progress), 0f,
                lerp(11.6f, -11.6f, progress), 0f,
                lerp(8f, -8f, progress), 10f
            )
        }

        drawPath(
            path = ridge,
            brush = brush,
            style = Stroke(
                width = strokeWidth,
            )
        )
    }

    drawOval(
        brush = brush,
        style = Stroke(width = strokeWidth),
        topLeft = Offset(36f, 39f),
        size = Size(8f, 8f)
    )

    rotate(
        degrees = progress * 360f * 3,
        pivot = Offset(40f, 43f)
    ) {
        drawLine(
            brush = brush,
            start = Offset(40f, 43f),
            end = Offset(40f, 40f),
            strokeWidth = strokeWidth,
        )
    }

    rotate(
        degrees = progress * 360f,
        pivot = Offset(40f, 43f)
    ) {
        drawLine(
            brush = brush,
            start = Offset(40f, 43f),
            end = Offset(40f, 41f),
            strokeWidth = strokeWidth,
        )
    }

    drawCircle(
        brush = brush,
        center = Offset(40f, 43f),
        radius = .3f
    )

    drawLine(
        brush = brush,
        start = Offset(32f, 19f),
        end = Offset(48f, 19f),
        strokeWidth = strokeWidth,
    )

    for (i in 0..3) {
        val windowPath = Path().apply {
            moveTo(34.6f + (i * 3.6f), 19f)
            relativeLineTo(0f, -2.8f)
        }.let { path ->
            expandPathCubic(
                source = path,
                padding = 0.5f,
                cornerRadius = RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                )
            )
        }

        drawPath(
            path = windowPath,
            brush = brush,
            style = Stroke(
                width = strokeWidth,
            )
        )
    }

    for (i in 0..1) {
        val windowPath = Path().apply {
            moveTo(38.5f + (i * 3f), 35f)
            relativeLineTo(0f, -6.4f)
        }.let { path ->
            expandPathCubic(
                source = path,
                padding = 1.5f,
                cornerRadius = RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                )
            )
        }

        drawPath(
            path = windowPath,
            brush = brush,
            style = Stroke(
                width = strokeWidth,
            )
        )
    }


}

fun DrawScope.drawOlympicTrack(progress: Float = 1f) {

    clipRect(
        left = 8f,
        top = 64f,
        right = 28f,
        bottom = 106f,
    ) {
        for (i in 0..3) {
            val progress = ((progress * 1f) + i / 3f) % 1f
            val path = Path().apply {
                moveTo(18, 74)
                lineTo(18, 96)
            }.let { path ->
                expandPathCubic(
                    source = path,
                    padding = lerp(2f, 13.5f, progress)
                )

            }

            drawPath(
                path = path,
                brush = brush,
                style = Stroke(
                    width = strokeWidth * (progress / .2f).coerceAtMost(1f),
                )
            )
        }
    }

}


fun DrawScope.opposingCircles(
    progress: Float = 0f
) {
    val delta = progress * 10f
    val left = expandPathCubic(
        source = Path().apply {
            moveTo(28f, 74.5f)
            lineTo(30f + delta, 74.5f)
        },
        padding = 10.5f,
        cornerRadius = RoundedCornerShape(
            topStart = CornerSize(50),
            topEnd = CornerSize(50),
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0),
        )
    )

    val right = expandPathCubic(
        source = Path().apply {
            moveTo(51f + delta, 74.5f)
            lineTo(64f, 74.5f)
        },
        padding = 10.5f,
        cornerRadius = RoundedCornerShape(
            topStart = CornerSize(0),
            topEnd = CornerSize(0),
            bottomStart = CornerSize(50),
            bottomEnd = CornerSize(50),
        )
    )

    drawPath(
        path = left,
        brush = brush,
        style = Stroke(
            width = strokeWidth
        )
    )

    drawPath(
        path = right,
        brush = brush,
        style = Stroke(
            width = strokeWidth
        )
    )


}

fun DrawScope.drawHeart(progress: Float = 0f) {
    val t = (progress * 6f) % 1f
    val heartbeatScale = when {
        t < 0.10f -> lerp(0.85f, 1.00f, t / 0.10f)
        t < 0.20f -> lerp(1.00f, 0.90f, (t - 0.10f) / 0.10f)
        t < 0.30f -> lerp(0.90f, 1.00f, (t - 0.20f) / 0.10f)
        t < 0.40f -> lerp(1.00f, 0.85f, (t - 0.30f) / 0.10f)
        else -> 0.85f
    }
    val pivot = Offset(14.25f, 12.5f)

    scale(scale = heartbeatScale, pivot = pivot) {
        val path1 = expandPathCubic(
            source = Path().apply {
                val x = 12f
                moveTo(18.5f - x, 18.5f - x)
                lineTo(18.5f, 18.5f)
            },
            padding = 6f,
            cornerRadius = RoundedCornerShape(
                topStart = CornerSize(0),
                topEnd = CornerSize(0),
                bottomStart = CornerSize(50),
                bottomEnd = CornerSize(50),
            )
        )
        val path = expandPathCubic(
            source = Path().apply {
                val x = 12f
                moveTo(10f + x, 18.5f - x)
                lineTo(10f, 18.5f)
            },
            padding = 6f,
            cornerRadius = RoundedCornerShape(
                topStart = CornerSize(0),
                topEnd = CornerSize(0),
                bottomStart = CornerSize(50),
                bottomEnd = CornerSize(50),
            )
        )

        drawPath(
            path = path.or(path1),
            brush = brush,
            style = Stroke(
                width = strokeWidth,
            )
        )
    }
}

fun DrawScope.drawParallelLines(
    vertical: Boolean,
    topLeft: Offset,
    size: Size,
    clip: Path = Path().apply {
        addRect(
            rect = Rect(topLeft, size)
        )
    },
    progress: Float = 1f,
) {

    drawPath(
        path = clip,
        brush = brush,
        style = Stroke(width = strokeWidth)
    )
    clipPath(
        path = clip
    ) {
        translate(
            left = topLeft.x,
            top = topLeft.y,
        ) {
            for (i in 0..10) {
                if (vertical) {
                    val length = size.width + (4 - (size.width % 4))
                    val x = (4f * i + progress * length) % length
                    drawLine(
                        brush = brush,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokeWidth,
                    )
                } else {
                    val length = size.height + (4 - (size.height % 4))
                    val y = (4f * i + progress * length) % length
                    drawLine(
                        brush = brush,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth,
                    )
                }
            }
        }
    }
}

fun DrawScope.drawPretzel(progress: Float) {
    val path = Path().apply {
        moveTo(26, 125)
        lineTo(14, 110)
        cubicTo(
            8f, 104f,
            2f, 112f,
            2f, 115f,
        )
        cubicTo(
            0f, 126f,
            14f, 126f,
            14f, 126f,
        )

    }.let { path ->
        expandPathCubic(
            source = path,
            padding = 1.5f,
        )
    }

    val left = path.copy()
    left.transform(
        matrix = Matrix().apply {
            scale(x = -1f)
        }
    )
    left.transform(
        matrix = Matrix().apply {
            translate(x = 28f)
        }
    )

    clipRect(
        left = 0f,
        right = 28f,
    ) {
        rotate(
            degrees = lerp(-4f, 4f, progress.oscillateToZero(5)),
            pivot = Offset(15f, 117f)
        ) {
            translate(
                top = lerp(-.25f, .5f, progress.oscillateToZero(2))
            ) {
                drawPath(
                    path = path.or(left),
                    brush = brush,
                    style = Stroke(
                        width = strokeWidth,
                    )
                )
            }
        }
    }
}

fun DrawScope.drawMascot(
    progress: Float = 1f,
) {
    val circle = Path().apply {
        addOval(
            oval = Rect(
                offset = Offset(71f, 85f),
                size = Size(21f, 21f)
            )
        )
    }

    val corner = Path().apply {
        moveTo(86f, 85f)
        lineTo(95.5f, 96f)
        lineTo(88.75f, 103f)
    }

    drawPath(
        path = circle.or(corner),
        brush = brush,
        style = Stroke(width = strokeWidth)
    )


    if (((progress * 37).toInt() % 17) != 0) {
        drawPath(
            path = Path().apply {
                addOval(
                    oval = Rect(
                        offset = Offset(83f, 93f),
                        size = Size(5f, 5f)
                    )
                )
            },
            brush = brush,
            style = Stroke(width = strokeWidth)
        )
    } else {
        drawLine(
            brush = brush,
            start = Offset(83f, 95.5f),
            end = Offset(88f, 95.5f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }

}

fun DrawScope.bottomCapsules(
    progress: Float = 0f
) {
    val progress = 1f - progress.oscillateToZero(3)
    val delta = progress * 30f
    val left = expandPathCubic(
        source = Path().apply {
            moveTo(28f, 117f)
            relativeLineTo(delta, 0f)
        },
        padding = 11f,
        cornerRadius = RoundedCornerShape(
            topStart = CornerSize(50),
            topEnd = CornerSize(50),
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0),
        )
    )

    val right = expandPathCubic(
        source = Path().apply {
            moveTo(50f + delta, 117f)
            lineTo(80f, 117f)
        },
        padding = 11f,
        cornerRadius = RoundedCornerShape(
            topStart = CornerSize(50),
            topEnd = CornerSize(50),
            bottomStart = CornerSize(50),
            bottomEnd = CornerSize(50),
        )
    )

    drawPath(
        path = left,
        brush = brush,
        style = Stroke(
            width = strokeWidth
        )
    )

    drawPath(
        path = right,
        brush = brush,
        style = Stroke(
            width = strokeWidth
        )
    )

    clipPath(
        path = left,
    ) {
        translate(
            delta
        ) {
            for (i in 0..2) {
                val x = 28f + 4 * i
                drawLine(
                    brush = brush,
                    start = Offset(x, 102f),
                    end = Offset(x, 128f),
                    strokeWidth = strokeWidth,
                )
            }
        }
    }
}

fun DrawScope.growingSemiCircles(
    progress: Float,
    bounds: Rect,
    vertical: Boolean,
) {
    val progress = (progress * 4f) % 1f
    clipRect(
        left = bounds.left,
        top = bounds.top,
        right = bounds.right,
        bottom = bounds.bottom,
    ) {
        rotate(
            degrees = if (vertical) 0f else -90f,
            pivot = bounds.center,
        ) {
            for (i in 0..2) {
                val y = (-(bounds.center.y - bounds.top) * i) + bounds.height / 2 * progress
                val halfH = bounds.height / 2f
                val scale = when {
                    y < -bounds.height -> .8f
                    y < -halfH -> lerp(.8f, 0f, (y + bounds.height) / halfH)
                    y < 0f -> 0f
                    else -> lerp(0f, .8f, y / halfH)
                }.coerceIn(0f, 1f).let {
                    FastOutSlowInEasing.transform(it)
                }
                translate(
                    top = y
                ) {
                    drawLine(
                        brush = brush,
                        strokeWidth = strokeWidth,
                        start = Offset(bounds.left, bounds.center.y),
                        end = Offset(bounds.right, bounds.center.y)
                    )
                    drawPath(
                        path = Path().apply {
                            addArc(
                                oval = bounds.copy(
                                    left = bounds.left + bounds.width / 2 * scale,
                                    bottom = bounds.bottom - bounds.height / 2 * scale,
                                    right = bounds.right - bounds.width / 2 * scale,
                                    top = bounds.top + bounds.height / 2 * scale,
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = 180f
                            )
                        },
                        brush = brush,
                        style = Stroke(strokeWidth),
                    )
                }
            }
        }
    }
    drawRect(
        brush = brush,
        topLeft = bounds.topLeft,
        size = bounds.size,
        style = Stroke(strokeWidth)
    )
}


