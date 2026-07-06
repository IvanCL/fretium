package com.fretwise.android.data.model

/**
 * Static song library migrated 1:1 from the Fretium web app (src/data/songs.ts).
 * All progressions are either traditional/public domain arrangements or original
 * practice sequences with no copyright.
 */
object SongsData {

    val ALL: List<Song> = listOf(

        // ─── BEGINNER ────────────────────────────────────────────────
        Song(
            id = "campfire-road", title = "Campfire Road",
            description = "Progresión I-V-vi-IV en Sol, la más usada en pop y folk",
            origin = "Progresión original de práctica",
            level = Level.BEGINNER, defaultBpm = 75, timeSignature = 4,
            sequence = listOf(
                SongChord("G", 4), SongChord("D", 4), SongChord("Em", 4), SongChord("C", 4),
                SongChord("G", 4), SongChord("D", 4), SongChord("Em", 4), SongChord("C", 4),
            ),
            strumHint = "↓ ↓ ↑ ↓ ↑",
            tips = listOf(
                "Esta progresión aparece en cientos de canciones populares",
                "Practica el cambio G→D hasta que sea automático",
                "Mantén un ritmo constante aunque el cambio no sea perfecto",
            ),
        ),
        Song(
            id = "blues-in-em", title = "Blues en Mi menor",
            description = "Patrón de blues de 12 compases adaptado para principiantes",
            origin = "Blues tradicional — dominio público",
            level = Level.BEGINNER, defaultBpm = 65, timeSignature = 4,
            sequence = listOf(
                SongChord("Em", 4), SongChord("Em", 4), SongChord("Em", 4), SongChord("Em", 4),
                SongChord("Am", 4), SongChord("Am", 4), SongChord("Em", 4), SongChord("Em", 4),
                SongChord("D", 4), SongChord("Am", 4), SongChord("Em", 4), SongChord("D", 4),
            ),
            strumHint = "↓ ↑ ↓ ↑",
            tips = listOf(
                "El blues de 12 compases es la base de todo el rock y blues",
                "Toca con sentimiento, no te apresures",
                "Repite varias veces para interiorizar la estructura",
            ),
        ),
        Song(
            id = "folk-waltz", title = "Folk Waltz",
            description = "Vals folk en 3/4, progresión de Mi menor",
            origin = "Progresión original de práctica",
            level = Level.BEGINNER, defaultBpm = 90, timeSignature = 3,
            sequence = listOf(
                SongChord("Em", 3), SongChord("G", 3), SongChord("Am", 3), SongChord("D", 3),
                SongChord("Em", 3), SongChord("G", 3), SongChord("D", 3), SongChord("Em", 3),
            ),
            strumHint = "↓ ↑ ↑",
            tips = listOf(
                "El compás de 3/4 se cuenta 1-2-3, 1-2-3",
                "Golpe fuerte en el tiempo 1 y suaves en 2 y 3",
                "Es el compás de los valses y muchas baladas folk",
            ),
        ),
        Song(
            id = "simple-rock", title = "Simple Rock",
            description = "Power progression de La mayor, base del rock clásico",
            origin = "Progresión original de práctica",
            level = Level.BEGINNER, defaultBpm = 85, timeSignature = 4,
            sequence = listOf(
                SongChord("A", 4), SongChord("D", 4), SongChord("E", 4), SongChord("A", 4),
                SongChord("A", 4), SongChord("D", 4), SongChord("E", 2), SongChord("D", 2),
                SongChord("A", 4),
            ),
            strumHint = "↓ ↓ ↑ ↑ ↓",
            tips = listOf(
                "Progresión I-IV-V, el ADN del rock and roll",
                "Prueba a tocar con más fuerza para sonar más rockero",
                "El cambio de 2 tiempos al final le da más energía",
            ),
        ),

        // ─── INTERMEDIATE ────────────────────────────────────────────
        Song(
            id = "autumn-minor", title = "Autumn Minor",
            description = "La progresión Am-F-C-G, base de miles de baladas",
            origin = "Progresión original de práctica",
            level = Level.INTERMEDIATE, defaultBpm = 72, timeSignature = 4,
            sequence = listOf(
                SongChord("Am", 4), SongChord("F", 4), SongChord("C", 4), SongChord("G", 4),
                SongChord("Am", 4), SongChord("F", 4), SongChord("C", 4), SongChord("G", 4),
            ),
            strumHint = "↓ ↓ ↑ ↓ ↑",
            tips = listOf(
                "La cejilla del F es el mayor reto aquí",
                "Si el F te cuesta, intenta Fmaj7 como alternativa temporal",
                "Esta progresión también funciona empezando en C: C-G-Am-F",
            ),
        ),
        Song(
            id = "blues-in-a", title = "Blues Shuffle en La",
            description = "12 compases de blues en La con séptimas dominantes",
            origin = "Blues tradicional — dominio público",
            level = Level.INTERMEDIATE, defaultBpm = 70, timeSignature = 4,
            sequence = listOf(
                SongChord("A", 4), SongChord("A7", 4), SongChord("A7", 4), SongChord("A7", 4),
                SongChord("D7", 4), SongChord("D7", 4), SongChord("A7", 4), SongChord("A7", 4),
                SongChord("E", 4), SongChord("D7", 4), SongChord("A7", 4), SongChord("E", 4),
            ),
            strumHint = "↓ ↑ ↓ ↑",
            tips = listOf(
                "Los acordes de séptima dan ese sabor blues auténtico",
                "Intenta tocar con feeling, no mecánicamente",
                "El turnaround final (E-D7-A7-E) es clave del blues",
            ),
        ),
        Song(
            id = "rock-anthem", title = "Rock Anthem",
            description = "Progresión de rock en Sol con Si menor y cejilla",
            origin = "Progresión original de práctica",
            level = Level.INTERMEDIATE, defaultBpm = 80, timeSignature = 4,
            sequence = listOf(
                SongChord("G", 4), SongChord("Bm", 4), SongChord("C", 4), SongChord("D", 4),
                SongChord("G", 4), SongChord("Bm", 4), SongChord("C", 2), SongChord("D", 2),
                SongChord("G", 4),
            ),
            strumHint = "↓ ↓ ↑ ↑ ↓ ↑",
            tips = listOf(
                "El Bm con cejilla parcial es el reto de esta progresión",
                "Practica el cambio G→Bm lentamente hasta dominarlo",
                "El cambio rápido C→D (2 tiempos cada uno) añade tensión",
            ),
        ),
        Song(
            id = "latin-groove", title = "Latin Groove",
            description = "Progresión flamenca/latina con séptimas y menor",
            origin = "Progresión original de práctica",
            level = Level.INTERMEDIATE, defaultBpm = 68, timeSignature = 4,
            sequence = listOf(
                SongChord("Am", 4), SongChord("G7", 4), SongChord("F", 4), SongChord("E", 4),
                SongChord("Am", 4), SongChord("G7", 4), SongChord("F", 2), SongChord("E", 2),
                SongChord("Am", 4),
            ),
            strumHint = "↓ ↑ ↓ ↑ ↓",
            tips = listOf(
                "Cadencia andaluza: Am-G-F-E, muy usada en flamenco",
                "El G7 en lugar de G hace la caída más dramática",
                "Prueba rasgueo rítmico con golpe de uñas para sabor flamenco",
            ),
        ),

        // ─── ADVANCED ────────────────────────────────────────────────
        Song(
            id = "jazz-evening", title = "Jazz Evening",
            description = "Progresión ii-V-I en Do con acordes de séptima mayor",
            origin = "Progresión original de práctica",
            level = Level.ADVANCED, defaultBpm = 60, timeSignature = 4,
            sequence = listOf(
                SongChord("Cmaj7", 4), SongChord("Am7", 4), SongChord("Dm7", 4), SongChord("G7", 4),
                SongChord("Cmaj7", 4), SongChord("Am7", 4), SongChord("Dm7", 2), SongChord("G7", 2),
                SongChord("Cmaj7", 4),
            ),
            strumHint = "↓ . ↑ . ↓",
            tips = listOf(
                "La progresión I-vi-ii-V es el ciclo de acordes del jazz",
                "Toca con suavidad y deja que los acordes respiren",
                "Intenta hacer las transiciones lo más fluidas posible",
            ),
        ),
        Song(
            id = "bossa-feel", title = "Bossa Feel",
            description = "Groove de bossa nova con acordes menores de séptima",
            origin = "Progresión original de práctica",
            level = Level.ADVANCED, defaultBpm = 65, timeSignature = 4,
            sequence = listOf(
                SongChord("Em7", 4), SongChord("Am7", 4), SongChord("Dm7", 4), SongChord("G7", 4),
                SongChord("Cmaj7", 4), SongChord("Fmaj7", 4), SongChord("Bm7", 4), SongChord("Em7", 4),
            ),
            strumHint = "↓ . ↑ ↓ . ↑",
            tips = listOf(
                "El ritmo de bossa nova tiene un \"salto\" característico",
                "Practica el patrón rítmico sin acordes primero",
                "Deja las notas sonar completamente antes de cambiar",
            ),
        ),
        Song(
            id = "minor-seventh-walk", title = "Minor Seventh Walk",
            description = "Descenso por menores de séptima, sonido modal",
            origin = "Progresión original de práctica",
            level = Level.ADVANCED, defaultBpm = 58, timeSignature = 4,
            sequence = listOf(
                SongChord("Am7", 4), SongChord("Em7", 4), SongChord("Dm7", 4), SongChord("Am7", 4),
                SongChord("Am7", 4), SongChord("Fmaj7", 4), SongChord("Bm7", 4), SongChord("Em7", 4),
            ),
            strumHint = "↓ ↓ . ↑ ↓",
            tips = listOf(
                "Sonido modal con escala dórica de La",
                "Los acordes de séptima aportan riqueza armónica",
                "Intenta arpegiar los acordes en lugar de rasguear",
            ),
        ),
    )

    private val levelOrder = listOf(Level.BEGINNER, Level.INTERMEDIATE, Level.ADVANCED)

    fun songsUpToLevel(level: Level): List<Song> {
        val upTo = levelOrder.indexOf(level)
        return ALL.filter { levelOrder.indexOf(it.level) <= upTo }
    }

    fun byId(id: String): Song? = ALL.firstOrNull { it.id == id }
}
