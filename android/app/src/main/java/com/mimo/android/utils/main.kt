package com.mimo.android.utils

import android.widget.Toast
import com.mimo.android.MainActivity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun showToast(text: String){
    Toast.makeText(
        MainActivity.getMainActivityContext(),
        text,
        Toast.LENGTH_SHORT
    ).show()
}

fun alertError(){
    Toast.makeText(
        MainActivity.getMainActivityContext(),
        "오류가 발생했습니다",
        Toast.LENGTH_SHORT
    ).show()
}

val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")
    .withZone(ZoneId.of("Asia/Seoul"))

fun convertStringDateToKoreaTime(stringDate: String): String {
    // 주어진 문자열을 LocalDateTime으로 파싱
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val dateTime = LocalDateTime.parse(stringDate, formatter)

    // 요일을 얻기 위해 Locale을 설정하고 해당 요일을 가져옴
    val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

    // 포맷팅된 문자열 반환
    return "${dateTime.year}년 ${dateTime.monthValue}월 ${dateTime.dayOfMonth}일 $dayOfWeek"
}