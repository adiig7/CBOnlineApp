package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.LiveData
import com.codingblocks.cbonlineapp.database.CommentsDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.models.CommentModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.safeApiCall

class DashboardDoubtsRepository(
    private val doubtsDao: DoubtsDao,
    private val commentsDao: CommentsDao,
    private val runDao: CourseRunDao
) {

    suspend fun fetchDoubtsByCourseRun(id: String) = safeApiCall {
        Clients.onlineV2JsonApi.getDoubtByAttemptId(id)
    }

    suspend fun fetchCommentsByDoubtId(id: String) = safeApiCall {
        Clients.onlineV2JsonApi.getCommentsById(id)
    }

    suspend fun insertDoubts(doubts: List<Doubts>) {
        doubts.forEach {
            doubtsDao.insert(DoubtsModel(
                dbtUid = it.id,
                title = it.title,
                body = it.body,
                contentId = it.content?.id ?: "",
                status = it.status,
                runAttemptId = it.runAttempt?.id ?: "",
                discourseTopicId = it.discourseTopicId,
                conversationId = it.conversationId,
                createdAt = it.createdAt
            ))
        }
    }

    suspend fun resolveDoubt(doubt: DoubtsModel) =
        safeApiCall {
            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid,
                Doubts(
                    id = doubt.dbtUid,
                    title = doubt.title,
                    body = doubt.body,
                    discourseTopicId = doubt.discourseTopicId,
                    runAttempt = RunAttempts(doubt.runAttemptId),
                    conversationId = doubt.conversationId,
                    content = LectureContent(doubt.contentId),
                    status = doubt.status,
                    createdAt = doubt.createdAt
                ))
        }

    suspend fun insertComments(comments: List<Comment>) {
        comments.forEach {
            commentsDao.insert(CommentModel(
                it.id,
                it.body,
                it.doubt?.id ?: "",
                it.updatedAt,
                it.username
            ))
        }
    }

    fun getDoubtsByCourseRun(type: String?, courseId: String): LiveData<List<DoubtsModel>> {
        return when (type) {
            LIVE -> doubtsDao.getLiveDoubts(courseId)
            RESOLVED -> doubtsDao.getResolveDoubts(courseId)
            else -> doubtsDao.getDoubts(courseId)
        }
    }

    fun getDoubtById(id: String) = doubtsDao.getDoubtById(id)
    fun getCommentsById(id: String) = commentsDao.getComments(id)
    fun getRuns() = runDao.getAttemptIds()
}
