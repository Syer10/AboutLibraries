import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.strippedLicenseContent

@Composable
fun SampleContent() {
    SampleTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("AboutLibraries Compose Apple Sample") }) }
        ) {
            val openDialog = remember { mutableStateOf<String?>(null) }

            LibrariesContainer(
                librariesBlock = ::getJson,
                modifier = Modifier.fillMaxSize()
            ) {
                openDialog.value = it.licenses.firstOrNull()?.strippedLicenseContent ?: ""
            }

            if (!openDialog.value.isNullOrBlank()) {
                val scrollState = rememberScrollState()
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = openDialog.value ?: "",
                        )
                        TextButton(
                            onClick = { openDialog.value = null },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

// TODO: Change to org.jetbrains.compose.components:components-resources resource when possible
expect fun getJson(): Libs

internal class MissingResourceException(path: String) :
    Exception("Missing resource with path: $path")