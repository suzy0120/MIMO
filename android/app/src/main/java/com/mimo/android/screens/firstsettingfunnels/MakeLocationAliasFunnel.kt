package com.mimo.android.screens.firstsettingfunnels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.mimo.android.components.FunnelInput
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Icon
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MakeLocationAliasFunnel(
    location: String,
    goPrev: (() -> Unit)? = null,
    onComplete: ((aliasText: String) -> Unit)? = null
){
    var inputText by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    fun handleChange(newText: String){
        inputText = newText
    }

    fun handleComplete(){
        // TODO: inputText가 없으면 return
        onComplete?.invoke("TODO...")
    }

    LaunchedEffect(key1 = Unit) {
        delay(100)
        keyboard?.show()
    }

    BackHandler {
        goPrev?.invoke()
    }

    Column {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = goPrev)
        Spacer(modifier = Modifier.padding(14.dp))

        HeadingSmall(text = location, color = Teal100)
        Spacer(modifier = Modifier.padding(16.dp))
        HeadingLarge(text = "이 장소의 별칭을 지어주세요", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(16.dp))

        FunnelInput(
            //TODO: text state management
            //TODO: 키보드 올라올때 버튼 모양 바꾸기...
            text = inputText,
            onChange = { newText ->  handleChange(newText) },
            onClear = { handleChange("") },
            placeholder = setPlaceholder(location),
            description = "주소 입력",
        )
    }
}

fun setPlaceholder(location: String): String {
    val locationStringList = location.split(" ")
    if (locationStringList.isEmpty()) {
        return "우리집"
    }
    return "${locationStringList[0]} 집"
}