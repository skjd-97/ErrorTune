package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.NeonCyan

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayerView(
    videoId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, NeonCyan.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()

                    val cleanVideoId = if (videoId.isNotBlank()) videoId else "jfKfPfyJRdk"
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                            <style>
                                * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                                html, body { width: 100%; height: 100%; overflow: hidden; }
                                iframe { width: 100%; height: 100%; border: none; }
                            </style>
                        </head>
                        <body>
                            <iframe 
                                src="https://www.youtube-nocookie.com/embed/$cleanVideoId?autoplay=1&playsinline=1&modestbranding=1&rel=0&controls=1" 
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                allowfullscreen>
                            </iframe>
                        </body>
                        </html>
                    """.trimIndent()

                    loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                val cleanVideoId = if (videoId.isNotBlank()) videoId else "jfKfPfyJRdk"
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                            html, body { width: 100%; height: 100%; overflow: hidden; }
                            iframe { width: 100%; height: 100%; border: none; }
                        </style>
                    </head>
                    <body>
                        <iframe 
                            src="https://www.youtube-nocookie.com/embed/$cleanVideoId?autoplay=1&playsinline=1&modestbranding=1&rel=0&controls=1" 
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                            allowfullscreen>
                        </iframe>
                    </body>
                    </html>
                """.trimIndent()
                webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
