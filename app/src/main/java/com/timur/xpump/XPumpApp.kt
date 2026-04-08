package com.timur.xpump

import android.app.Application
import com.timur.xpump.data.db.XPumpDatabase
import com.timur.xpump.data.repository.WorkoutRepository

// Наследуемся от базового Application
class XPumpApp : Application() {

    // lazy означает "ленивая инициализация". База данных не будет
    // жрать память телефона, пока к ней кто-нибудь реально не обратится.
    val database by lazy { XPumpDatabase.getDatabase(this) }

    // Создаем наш репозиторий (тот самый "мост" к базе данных)
    val repository by lazy { WorkoutRepository(database.workoutDao()) }
}