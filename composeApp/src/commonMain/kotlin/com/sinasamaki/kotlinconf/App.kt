package com.sinasamaki.kotlinconf

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinasamaki.kotlinconf.logo.KotlinLogo
import kotlinconf.composeapp.generated.resources.Res
import kotlinconf.composeapp.generated.resources.kotlin
import org.jetbrains.compose.resources.painterResource

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
