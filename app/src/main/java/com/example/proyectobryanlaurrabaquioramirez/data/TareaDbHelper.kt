package com.example.proyectobryanlaurrabaquioramirez.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// administra la base de datos local
class TareaDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    // crea la tabla
    override fun onCreate(db: SQLiteDatabase) {
        val crearTabla = """
            CREATE TABLE $TABLA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITULO TEXT NOT NULL,
                $COL_MATERIA TEXT,
                $COL_DESCRIPCION TEXT,
                $COL_FECHA TEXT,
                $COL_PRIORIDAD TEXT,
                $COL_COMPLETADA INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(crearTabla)
    }

    // Se ejecuta si cambias VERSION_BD: borra y recrea
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    // guarda una nueva tarea
    fun insertar(tarea: Tarea): Long {
        return writableDatabase.insert(TABLA, null, aContentValues(tarea))
    }

    //edita una tarea existente
    fun actualizar(tarea: Tarea): Int {
        return writableDatabase.update(
            TABLA, aContentValues(tarea), "$COL_ID = ?", arrayOf(tarea.id.toString())
        )
    }

    // eliminar tarea
    fun eliminar(id: Long): Int {
        return writableDatabase.delete(TABLA, "$COL_ID = ?", arrayOf(id.toString()))
    }

    // marca y desmarca como completada
    fun cambiarCompletada(id: Long, completada: Boolean): Int {
        val valores = ContentValues().apply {
            put(COL_COMPLETADA, if (completada) 1 else 0)
        }
        return writableDatabase.update(TABLA, valores, "$COL_ID = ?", arrayOf(id.toString()))
    }

    // listar todas las tareas pendientes primero las más nuevas arriba
    fun obtenerTodas(): List<Tarea> {
        val lista = mutableListOf<Tarea>()
        val cursor = readableDatabase.query(
            TABLA, null, null, null, null, null,
            "$COL_COMPLETADA ASC, $COL_ID DESC"
        )
        while (cursor.moveToNext()) {
            lista.add(cursorATarea(cursor))
        }
        cursor.close()
        return lista
    }

    // Traer una tarea por id para editarla
    fun obtenerPorId(id: Long): Tarea? {
        val cursor = readableDatabase.query(
            TABLA, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null
        )
        var tarea: Tarea? = null
        if (cursor.moveToFirst()) {
            tarea = cursorATarea(cursor)
        }
        cursor.close()
        return tarea
    }

    private fun aContentValues(tarea: Tarea): ContentValues {
        return ContentValues().apply {
            put(COL_TITULO, tarea.titulo)
            put(COL_MATERIA, tarea.materia)
            put(COL_DESCRIPCION, tarea.descripcion)
            put(COL_FECHA, tarea.fecha)
            put(COL_PRIORIDAD, tarea.prioridad)
            put(COL_COMPLETADA, if (tarea.completada) 1 else 0)
        }
    }

    private fun cursorATarea(c: Cursor): Tarea {
        return Tarea(
            id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
            titulo = c.getString(c.getColumnIndexOrThrow(COL_TITULO)) ?: "",
            materia = c.getString(c.getColumnIndexOrThrow(COL_MATERIA)) ?: "",
            descripcion = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPCION)) ?: "",
            fecha = c.getString(c.getColumnIndexOrThrow(COL_FECHA)) ?: "",
            prioridad = c.getString(c.getColumnIndexOrThrow(COL_PRIORIDAD)) ?: "",
            completada = c.getInt(c.getColumnIndexOrThrow(COL_COMPLETADA)) == 1
        )
    }

    companion object {
        private const val NOMBRE_BD = "tareas.db"
        private const val VERSION_BD = 1

        const val TABLA = "tareas"
        const val COL_ID = "id"
        const val COL_TITULO = "titulo"
        const val COL_MATERIA = "materia"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_FECHA = "fecha"
        const val COL_PRIORIDAD = "prioridad"
        const val COL_COMPLETADA = "completada"

        @Volatile
        private var INSTANCIA: TareaDbHelper? = null

        fun obtener(context: Context): TareaDbHelper {
            return INSTANCIA ?: synchronized(this) {
                val nueva = TareaDbHelper(context.applicationContext)
                INSTANCIA = nueva
                nueva
            }
        }
    }
}