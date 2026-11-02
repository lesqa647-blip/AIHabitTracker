package com.example.aihabittracker

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.widget.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : Activity() {
    private val startDate = LocalDate.of(2026, 11, 9)
    private val prefs by lazy { getSharedPreferences("progress", MODE_PRIVATE) }
    private val cream = Color.rgb(250, 247, 242)
    private val ink = Color.rgb(46, 58, 62)
    private val sage = Color.rgb(106, 137, 119)
    private lateinit var layout: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var progressLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun addText(value: String, size: Float = 17f, bold: Boolean = false) {
        val t = TextView(this)
        t.text = value
        t.textSize = size
        t.setTextColor(ink)
        if (bold) t.setTypeface(null, Typeface.BOLD)
        t.setPadding(0, 10, 0, 12)
        layout.addView(t)
    }

    private fun checkbox(key: String, text: String) {
        val c = CheckBox(this)
        c.text = text
        c.textSize = 16f
        c.setTextColor(ink)
        c.buttonTintList = ColorStateList.valueOf(sage)
        c.setPadding(4, 10, 4, 10)
        c.isChecked = prefs.getBoolean(key, false)
        c.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(key, checked).apply()
            refreshProgress()
        }
        layout.addView(c)
    }

    private fun refreshProgress() {
        val day = ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt()
        val dates = if (day < 0) emptyList() else (0..day.coerceAtMost(364)).map { startDate.plusDays(it.toLong()) }
        val tasks = dates.flatMap { date ->
            buildList {
                add("brain")
                if (date.dayOfWeek.value != 3) add("study")
                if (date.dayOfWeek.value in setOf(1, 3, 6)) add("kettle")
            }.map { date.toString() + ":" + it }
        }
        val done = tasks.count { prefs.getBoolean(it, false) }
        progress.max = tasks.size.coerceAtLeast(1)
        progress.progress = done
        progressLabel.text = "Отмечено привычек: $done из ${tasks.size}"
    }

    private fun showDashboard() {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(cream)
        layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(34, 35, 34, 48)
        scroll.addView(layout)
        setContentView(scroll)
        addText("МОЙ ГОД С ИИ", 29f, true)
        addText("9 ноября 2026 — 8 ноября 2027", 15f)
        val today = LocalDate.now()
        val index = ChronoUnit.DAYS.between(startDate, today)
        val week = (index / 7 + 1).coerceIn(1, 52)
        addText(if (index < 0) "До начала плана: ${-index} дней" else "Неделя $week из 52", 20f, true)
        progressLabel = TextView(this)
        progressLabel.textSize = 15f
        progressLabel.setTextColor(ink)
        layout.addView(progressLabel)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progress.progressTintList = ColorStateList.valueOf(sage)
        layout.addView(progress)
        refreshProgress()
        addText("Задачи на сегодня", 22f, true)
        val date = today.toString()
        checkbox("$date:brain", "Тренажёр мозга — 15 минут")
        val minutes = mapOf(1 to 25, 2 to 30, 3 to 0, 4 to 30, 5 to 15, 6 to 75, 7 to 25)[today.dayOfWeek.value] ?: 0
        if (minutes > 0) checkbox("$date:study", "ИИ и промпты — $minutes минут")
        else addText("Сегодня отдых от занятий ИИ")
        if (today.dayOfWeek.value in setOf(1, 3, 6)) checkbox("$date:kettle", "Гиря — 30 повторений (комфортная нагрузка)")
        addText("Фокус недели", 22f, true)
        val stage = ((week - 1) / 4).toInt().coerceIn(0, 12)
        val topics = listOf("Знакомство с ИИ и базовые запросы", "Чёткие инструкции и роли", "Примеры и структура промптов", "Поиск и проверка информации", "Тексты и редактура", "Таблицы и данные", "Изображения и визуальные задачи", "Автоматизация повседневных задач", "Сбор портфолио", "Работа с заказчиками", "Тестовые заказы и первые отклики", "Углубление навыков", "Портфолио, доход и следующий план")
        addText(topics[stage])
        addText("Режимы энергии", 22f, true)
        addText("Минимум: 5 минут · Норма: по расписанию · Максимум: плюс 15 минут практики")
        addText("Прогресс сохраняется на этом устройстве и работает офлайн.", 13f)
    }
}