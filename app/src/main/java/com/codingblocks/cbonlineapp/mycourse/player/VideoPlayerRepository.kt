package com.codingblocks.cbonlineapp.mycourse.player

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.SectionDao
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class VideoPlayerRepository(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao,
    private val sectionDao: SectionDao
) {
    suspend fun fetchCourseNotes(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId) }

    suspend fun deleteNote(noteId: String) = safeApiCall { Clients.onlineV2JsonApi.deleteNoteById(noteId) }

    suspend fun addNote(note: Note) = safeApiCall { Clients.onlineV2JsonApi.createNote(note) }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach {
            val contentTitle = GlobalScope.async { contentDao.getContentTitle(it.content?.id ?: "") }
            val model = NotesModel(
                it.id,
                it.duration,
                it.text,
                it.content?.id ?: "",
                it.runAttempt?.id ?: "",
                it.createdAt ?: "",
                it.deletedAt,
                contentTitle.await()
            )
//            val sum = notes + networkList
//            sum.groupBy { it.nttUid }
//                .filter { it.value.size == 1 }
//                .flatMap { it.value }
//                .forEach {
//                    notesDao.deleteNoteByID(it.nttUid)
//                }
            notesDao.insert(model)
        }
    }

    fun updateNoteInDb(newNote: Note) {
        notesDao.updateBody(newNote.id, newNote.text)
    }

    suspend fun addNewNote(newNote: Note) {
        val contentTitle = GlobalScope.async { contentDao.getContentTitle(newNote.content?.id ?: "") }
        val model = NotesModel(
            newNote.id,
            newNote.duration,
            newNote.text,
            newNote.content?.id ?: "",
            newNote.runAttempt?.id ?: "",
            newNote.createdAt ?: "",
            newNote.deletedAt,
            contentTitle.await()
        )
        notesDao.insert(model)
    }

    fun getNotes(attemptId: String) = notesDao.getNotes(attemptId)

    fun deleteNoteFromDb(noteId: String) = notesDao.deleteNoteByID(noteId)

    suspend fun getSectionTitle(sectionId: String, contentId: String): Pair<String, String> {
        val a = withContext(Dispatchers.IO) { sectionDao.getSectionTitle(sectionId) }
        val b = withContext(Dispatchers.IO) { contentDao.getContentTitle(contentId) }
        return Pair(a, b)
    }

    suspend fun updateNote(note: Note) = safeApiCall { Clients.onlineV2JsonApi.updateNoteById(note.id, note) }

    suspend fun getOtp(videoId: String, attemptId: String, sectionId: String) =
        safeApiCall { Clients.api.getOtp(videoId, sectionId, attemptId) }
}
