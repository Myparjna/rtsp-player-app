package com.probe.rtspplayer

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RTSPPlayerScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RTSPPlayerScreen() {
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("1945") }
    var path by remember { mutableStateOf("stream") }
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin") }

    val codecOptions = listOf("h264", "h265")
    var selectedCodec by remember { mutableStateOf(codecOptions[0]) } // 默认 H264

    val resolutionOptions = listOf("720p", "1080p")
    var selectedResolution by remember { mutableStateOf(resolutionOptions[0]) } // 默认 720p

    val fpsOptions = listOf(20, 25, 30, 50, 60)
    var selectedFps by remember { mutableStateOf(30) } // 默认 30

    var isPlaying by remember { mutableStateOf(false) }
    var player: ExoPlayer? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    val rtspUrl = remember(ip, port, path, username, password, selectedCodec, selectedResolution, selectedFps) {
        "rtsp://$username:$password@$ip:${port.ifEmpty { "1945" }}/$path?codec=$selectedCodec&resolution=$selectedResolution&fps=$selectedFps"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RTSP 播放器") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = ip,
                            onValueChange = { ip = it },
                            label = { Text("IP") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = port,
                            onValueChange = { port = it.filter { ch -> ch.isDigit() }.take(5) },
                            label = { Text("端口") },
                            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(120.dp)
                        )
                    }

                    OutlinedTextField(
                        value = path,
                        onValueChange = { path = it },
                        label = { Text("路径") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("账号") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("密码") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 编解码下拉
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("编解码：", modifier = Modifier.width(80.dp))
                        var codecExpanded by remember { mutableStateOf(false) }
                        Box {
                            Button(onClick = { codecExpanded = true }) { Text(selectedCodec.uppercase()) }
                            DropdownMenu(expanded = codecExpanded, onDismissRequest = { codecExpanded = false }) {
                                codecOptions.forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt.uppercase()) }, onClick = { selectedCodec = opt; codecExpanded = false })
                                }
                            }
                        }
                    }

                    // 分辨率下拉
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("分辨率：", modifier = Modifier.width(80.dp))
                        var resExpanded by remember { mutableStateOf(false) }
                        Box {
                            Button(onClick = { resExpanded = true }) { Text(selectedResolution) }
                            DropdownMenu(expanded = resExpanded, onDismissRequest = { resExpanded = false }) {
                                resolutionOptions.forEach { opt ->
                                    DropdownMenuItem(text = { Text(opt) }, onClick = { selectedResolution = opt; resExpanded = false })
                                }
                            }
                        }
                    }

                    // 帧率下拉
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("帧率：", modifier = Modifier.width(80.dp))
                        var fpsExpanded by remember { mutableStateOf(false) }
                        Box {
                            Button(onClick = { fpsExpanded = true }) { Text("$selectedFps fps") }
                            DropdownMenu(expanded = fpsExpanded, onDismissRequest = { fpsExpanded = false }) {
                                fpsOptions.forEach { opt ->
                                    DropdownMenuItem(text = { Text("$opt fps") }, onClick = { selectedFps = opt; fpsExpanded = false })
                                }
                            }
                        }
                    }

                    Text(text = "构造的地址：\n$rtspUrl")

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            if (!isPlaying && ip.isNotBlank()) {
                                player = ExoPlayer.Builder(context).build().also { exo ->
                                    exo.setMediaItem(MediaItem.fromUri(rtspUrl))
                                    exo.prepare()
                                    exo.playWhenReady = true
                                }
                                isPlaying = true
                            }
                        }) { Text("启动播放") }

                        Button(onClick = {
                            player?.release()
                            player = null
                            isPlaying = false
                        }) { Text("停止播放") }
                    }
                }
            }

            if (isPlaying && player != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                useController = true
                                player = this@RTSPPlayerScreen.player
                            }
                        },
                        update = { view -> view.player = player }
                    )
                }
            }
        }
    }
}
