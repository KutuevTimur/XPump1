package com.timur.xpump.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class TimeMaskWatcher(private val editText: EditText) : TextWatcher {
    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting || s == null) return
        isFormatting = true

        // 1. Вырезаем всё, кроме цифр (защита от багов при стирании двоеточия)
        var digits = s.toString().replace("\\D".toRegex(), "")

        // 2. Ограничиваем длину (максимум 4 цифры -> 99:59)
        if (digits.length > 4) {
            digits = digits.substring(0, 4)
        }

        // 3. Защита от дурака: десятки секунд не могут быть больше 5 (чтобы не было 12:60)
        if (digits.length >= 3) {
            val secTens = digits[2].digitToIntOrNull() ?: 0
            if (secTens > 5) {
                // Если ввели фигню типа 12:7, заменяем 7 на 5
                digits = digits.substring(0, 2) + "5" + (if (digits.length == 4) digits[3] else "")
            }
        }

        // 4. Собираем обратно, вставляя двоеточие
        val formatted = StringBuilder()
        for (i in digits.indices) {
            if (i == 2) formatted.append(":")
            formatted.append(digits[i])
        }

        // 5. Записываем текст и ставим курсор всегда в конец (самый кайфовый UX для коротких полей)
        editText.setText(formatted.toString())
        editText.setSelection(formatted.length)

        isFormatting = false
    }
}
