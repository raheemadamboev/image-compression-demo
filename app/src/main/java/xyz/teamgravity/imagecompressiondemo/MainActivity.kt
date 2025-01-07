package xyz.teamgravity.imagecompressiondemo

import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import xyz.teamgravity.imagecompressiondemo.ui.theme.ImageCompressionDemoTheme

class MainActivity : ComponentActivity() {

    private val fileManager: FileManager by lazy { FileManager(applicationContext) }
    private val compressionManager: CompressionManager by lazy { CompressionManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageCompressionDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    val scope = rememberCoroutineScope()

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                            val mimeType = contentResolver.getType(uri)
                            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

                            scope.launch {
                                fileManager.saveImage(
                                    contentUri = uri,
                                    fileName = "uncompressed.$extension"
                                )
                            }

                            scope.launch {
                                compressionManager.compress(
                                    contentUri = uri,
                                    threshold = 200 * 1024
                                )?.let { bytes ->
                                    fileManager.saveImage(
                                        bytes = bytes,
                                        fileName = "compressed.$extension"
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Button(
                            onClick = {
                                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.pick_an_image)
                            )
                        }
                    }
                }
            }
        }
    }
}