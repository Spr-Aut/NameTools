package com.spraut.tools

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    Column {
        TextField(
            value = fullNames.value,
            onValueChange = { fullNames.value = it },
            label = { Text(text = "全体名单") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = currentNames.value,
            onValueChange = { currentNames.value = it },
            label = { Text(text = "当次名单") },
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            val fullList = separateNames(fullNames.value.text)
            val currentList = separateNames(currentNames.value.text)
            absentList.value = currentList.findWhichAreNotExistIn(fullList)
        }) {
            Text(text = "查找缺失的名字")
        }

        Text(text = "以下名字缺失：\n${absentList.value}")
    }
}

fun separateNames(names: String): List<String> {
    val splitCharacter = "\\s|,|，|\n"
    return names.split(splitCharacter.toRegex()).filter { it.isNotBlank() && it.isNotEmpty() }
}

fun List<String>.findWhichAreNotExistIn(fullList: List<String>): List<String> {
    return fullList.subtract(this.toSet()).toList()
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}
