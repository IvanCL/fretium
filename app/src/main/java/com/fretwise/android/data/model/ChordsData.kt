package com.fretwise.android.data.model

/**
 * Static chord library migrated 1:1 from the Fretium web app (src/data/chords.ts).
 * Position/finger arrays are indexed [string6=E, string5=A, string4=D, string3=G, string2=B, string1=e].
 */
object ChordsData {

    val ALL: List<Chord> = listOf(
        // ─── BEGINNER ────────────────────────────────────────────────
        Chord(
            id = "Em", name = "Em", fullName = "Mi menor", level = Level.BEGINNER,
            positions = listOf(0, 2, 2, 0, 0, 0),
            fingers = listOf(null, 2, 3, null, null, null),
            startFret = 1,
            tips = listOf(
                "El acorde más fácil para comenzar",
                "Presiona las cuerdas 4 y 5 en el traste 2",
                "Deja sonar todas las cuerdas al aire o pisadas",
            ),
        ),
        Chord(
            id = "Am", name = "Am", fullName = "La menor", level = Level.BEGINNER,
            positions = listOf(-1, 0, 2, 2, 1, 0),
            fingers = listOf(null, null, 2, 3, 1, null),
            startFret = 1,
            tips = listOf(
                "Muta la cuerda 6 (Mi bajo) con la palma",
                "Agrupa los tres dedos en el traste 2 y 1",
                "Junto con Em formas la base del rock",
            ),
        ),
        Chord(
            id = "E", name = "E", fullName = "Mi mayor", level = Level.BEGINNER,
            positions = listOf(0, 2, 2, 1, 0, 0),
            fingers = listOf(null, 2, 3, 1, null, null),
            startFret = 1,
            tips = listOf(
                "Similar a Em pero añade el dedo 1 en la cuerda 3",
                "Sonido brillante y poderoso",
                "Acorde fundamental del rock y blues",
            ),
        ),
        Chord(
            id = "A", name = "A", fullName = "La mayor", level = Level.BEGINNER,
            positions = listOf(-1, 0, 2, 2, 2, 0),
            fingers = listOf(null, null, 1, 2, 3, null),
            startFret = 1,
            tips = listOf(
                "Tres dedos en el traste 2, cuerdas 2-3-4",
                "Puedes también usar el dedo 1 como mini cejilla",
                "Muta la cuerda 6",
            ),
        ),
        Chord(
            id = "D", name = "D", fullName = "Re mayor", level = Level.BEGINNER,
            positions = listOf(-1, -1, 0, 2, 3, 2),
            fingers = listOf(null, null, null, 1, 3, 2),
            startFret = 1,
            tips = listOf(
                "Solo se tocan las cuerdas del 1 al 4",
                "Forma de triángulo con los dedos",
                "Practica la transición D → G es muy común",
            ),
        ),
        Chord(
            id = "G", name = "G", fullName = "Sol mayor", level = Level.BEGINNER,
            positions = listOf(3, 2, 0, 0, 0, 3),
            fingers = listOf(2, 1, null, null, null, 3),
            startFret = 1,
            tips = listOf(
                "Acorde grande que usa las 6 cuerdas",
                "Extiende bien el dedo meñique hasta la cuerda 1",
                "La transición G → C es esencial",
            ),
        ),
        Chord(
            id = "C", name = "C", fullName = "Do mayor", level = Level.BEGINNER,
            positions = listOf(-1, 3, 2, 0, 1, 0),
            fingers = listOf(null, 3, 2, null, 1, null),
            startFret = 1,
            tips = listOf(
                "Muta la cuerda 6 apoyando la palma o evita rasgarla",
                "El dedo 1 en el traste 1 cuerda 2 es el más difícil",
                "Acorde muy usado en pop y rock",
            ),
        ),

        // ─── INTERMEDIATE ────────────────────────────────────────────
        Chord(
            id = "Dm", name = "Dm", fullName = "Re menor", level = Level.INTERMEDIATE,
            positions = listOf(-1, -1, 0, 2, 3, 1),
            fingers = listOf(null, null, null, 2, 3, 1),
            startFret = 1,
            tips = listOf(
                "Similar a D mayor pero un semitono más oscuro",
                "Solo cuerdas 1 a 4",
                "Muy usado en baladas y rock melódico",
            ),
        ),
        Chord(
            id = "A7", name = "A7", fullName = "La séptima", level = Level.INTERMEDIATE,
            positions = listOf(-1, 0, 2, 0, 2, 0),
            fingers = listOf(null, null, 2, null, 3, null),
            startFret = 1,
            tips = listOf(
                "Acorde de séptima dominante, sonido bluesy",
                "Ideal para el blues y el boogie",
                "Transiciona bien hacia D y E",
            ),
        ),
        Chord(
            id = "D7", name = "D7", fullName = "Re séptima", level = Level.INTERMEDIATE,
            positions = listOf(-1, -1, 0, 2, 1, 2),
            fingers = listOf(null, null, null, 2, 1, 3),
            startFret = 1,
            tips = listOf(
                "Muy similar a D pero con la segunda cuerda en traste 1",
                "Crea tensión que resuelve hacia G",
                "Esencial en el blues en G",
            ),
        ),
        Chord(
            id = "G7", name = "G7", fullName = "Sol séptima", level = Level.INTERMEDIATE,
            positions = listOf(3, 2, 0, 0, 0, 1),
            fingers = listOf(3, 2, null, null, null, 1),
            startFret = 1,
            tips = listOf(
                "Como G mayor pero con el meñique libre y dedo 1 en cuerda 1",
                "Resuelve naturalmente hacia C",
                "Muy usado en jazz y blues",
            ),
        ),
        Chord(
            id = "C7", name = "C7", fullName = "Do séptima", level = Level.INTERMEDIATE,
            positions = listOf(-1, 3, 2, 3, 1, 0),
            fingers = listOf(null, 3, 2, 4, 1, null),
            startFret = 1,
            tips = listOf(
                "C mayor con el dedo 4 añadido en cuerda 3 traste 3",
                "Sonido funky y bluesy",
                "Resuelve hacia F",
            ),
        ),
        Chord(
            id = "Bm", name = "Bm", fullName = "Si menor", level = Level.INTERMEDIATE,
            positions = listOf(-1, 2, 4, 4, 3, 2),
            fingers = listOf(null, 1, 3, 4, 2, 1),
            startFret = 2,
            barre = Barre(fret = 2, fromString = 5, toString = 1),
            tips = listOf(
                "Tu primer acorde con cejilla parcial",
                "El dedo 1 cubre las cuerdas 1 y 2 en el traste 2",
                "Practica la cejilla por separado antes de añadir los otros dedos",
            ),
        ),
        Chord(
            id = "F", name = "F", fullName = "Fa mayor", level = Level.INTERMEDIATE,
            positions = listOf(1, 3, 3, 2, 1, 1),
            fingers = listOf(1, 3, 4, 2, 1, 1),
            startFret = 1,
            barre = Barre(fret = 1, fromString = 6, toString = 1),
            tips = listOf(
                "La cejilla completa es el mayor desafío para principiantes",
                "Presiona el dedo 1 bien plano sobre todas las cuerdas en traste 1",
                "Practica 10 minutos al día hasta que salga limpio",
            ),
        ),

        // ─── ADVANCED ────────────────────────────────────────────────
        Chord(
            id = "Em7", name = "Em7", fullName = "Mi menor séptima", level = Level.ADVANCED,
            positions = listOf(0, 2, 2, 0, 3, 0),
            fingers = listOf(null, 2, 3, null, 4, null),
            startFret = 1,
            tips = listOf(
                "Em con el dedo 4 añadido en la segunda cuerda traste 3",
                "Sonido suave y jazzístico",
                "Usado mucho en Bossa Nova y soul",
            ),
        ),
        Chord(
            id = "Am7", name = "Am7", fullName = "La menor séptima", level = Level.ADVANCED,
            positions = listOf(-1, 0, 2, 0, 1, 0),
            fingers = listOf(null, null, 2, null, 1, null),
            startFret = 1,
            tips = listOf(
                "Am sin el dedo de la tercera cuerda",
                "Muy sencillo pero muy expresivo",
                "Combínalo con Dm7 para progresiones suaves",
            ),
        ),
        Chord(
            id = "Dm7", name = "Dm7", fullName = "Re menor séptima", level = Level.ADVANCED,
            positions = listOf(-1, -1, 0, 2, 1, 1),
            fingers = listOf(null, null, null, 2, 1, 1),
            startFret = 1,
            barre = Barre(fret = 1, fromString = 2, toString = 1),
            tips = listOf(
                "Mini cejilla en traste 1 cuerdas 1 y 2",
                "Sonido melancólico y expresivo",
                "Muy usado en jazz, soul y R&B",
            ),
        ),
        Chord(
            id = "Cmaj7", name = "Cmaj7", fullName = "Do mayor séptima", level = Level.ADVANCED,
            positions = listOf(-1, 3, 2, 0, 0, 0),
            fingers = listOf(null, 3, 2, null, null, null),
            startFret = 1,
            tips = listOf(
                "C mayor sin el dedo de la segunda cuerda",
                "Sonido luminoso y etéreo",
                "Icónico en música pop y jazz",
            ),
        ),
        Chord(
            id = "Fmaj7", name = "Fmaj7", fullName = "Fa mayor séptima", level = Level.ADVANCED,
            positions = listOf(-1, -1, 3, 2, 1, 0),
            fingers = listOf(null, null, 3, 2, 1, null),
            startFret = 1,
            tips = listOf(
                "No requiere cejilla a diferencia de F",
                "Solo cuerdas del 1 al 4",
                "Sonido brillante muy usado en indie y pop",
            ),
        ),
        Chord(
            id = "Bm7", name = "Bm7", fullName = "Si menor séptima", level = Level.ADVANCED,
            positions = listOf(-1, 2, 4, 2, 3, 2),
            fingers = listOf(null, 1, 3, 1, 2, 1),
            startFret = 2,
            barre = Barre(fret = 2, fromString = 5, toString = 1),
            tips = listOf(
                "Bm con la cuarta cuerda al aire en traste 2",
                "Versión más rica y moderna de Bm",
                "La cejilla en traste 2 es esencial",
            ),
        ),
    )

    private val levelOrder = listOf(Level.BEGINNER, Level.INTERMEDIATE, Level.ADVANCED)

    /** All chords up to and including [level] (cumulative, matches web `chordsForLevel`). */
    fun chordsUpToLevel(level: Level): List<Chord> {
        val upTo = levelOrder.indexOf(level)
        return ALL.filter { levelOrder.indexOf(it.level) <= upTo }
    }

    /** Only the chords introduced exactly at [level] (matches web `chordsByCurrentLevel`). */
    fun chordsForExactLevel(level: Level): List<Chord> = ALL.filter { it.level == level }

    fun byId(id: String): Chord? = ALL.firstOrNull { it.id == id }
}
