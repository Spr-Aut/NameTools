package com.spraut.tools

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spraut.tools.ui.theme.ToolsTheme

class NameFinderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToolsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NameMatchTools()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NameMatchTools() {
    val context = LocalContext.current
    val fullNames = remember { mutableStateOf(TextFieldValue()) }
    val currentNames = remember { mutableStateOf(TextFieldValue()) }
    val absentList = remember {
        mutableStateOf(listOf<String>())
    }
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        NameInputBox(names = fullNames, label = "全班名单")
        Spacer(modifier = Modifier.height(10.dp))

        NameInputBox(names = currentNames, label = "本次名单")
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val fullList = separateNames(fullNames.value.text.removeNumAndDot())
            val currentList = separateNames(currentNames.value.text.removeNumAndDot())
            absentList.value = currentList.findWhichAreNotExistIn(fullList)
        }) {
            Text(text = "查找缺失的同学")
        }

        Text(text = "缺少以下同学：\n${absentList.value}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputBox(
    names: MutableState<TextFieldValue>,
    label: String = "",
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    TextField(
        value = names.value,
        onValueChange = { names.value = it },
        label = { Text(text = label) },
        modifier = modifier,
        maxLines = 8
    )
}

fun separateNames(names: String): List<String> {
    val splitCharacter = "\\s|,|，|\n"
    val nameList =
        names.split(splitCharacter.toRegex()).filter { it.isNotBlank() && it.isNotEmpty() }
    return nameList.map { extractChineseCharacters(it) }
}

fun String.removeNumAndDot(): String {
    val regex = Regex("[0-9.]+")
    return this.replace(regex, "")
}

fun extractChineseCharacters(str: String): String {
    val regex = Regex("[\\u4E00-\\u9FA5]+")
    val matchResult = regex.find(str)
    return matchResult?.value ?: str
}

fun List<String>.findWhichAreNotExistIn(fullList: List<String>): List<String> {
    return fullList.subtract(this.toSet()).toList()
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}
