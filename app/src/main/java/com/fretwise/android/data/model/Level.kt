package com.fretwise.android.data.model

enum class Level(val id: String, val label: String, val description: String, val color: Long) {
    BEGINNER(
        id = "beginner",
        label = "Principiante",
        description = "Acordes abiertos básicos: Em, Am, E, A, D, G, C",
        color = 0xFF22C55E,
    ),
    INTERMEDIATE(
        id = "intermediate",
        label = "Intermedio",
        description = "Acordes de séptima y primera cejilla: Bm, F, acordes 7",
        color = 0xFFF59E0B,
    ),
    ADVANCED(
        id = "advanced",
        label = "Avanzado",
        description = "Acordes de jazz y extendidos: maj7, m7 y más",
        color = 0xFFEF4444,
    );

    companion object {
        fun fromId(id: String): Level = entries.firstOrNull { it.id == id } ?: BEGINNER
    }
}
