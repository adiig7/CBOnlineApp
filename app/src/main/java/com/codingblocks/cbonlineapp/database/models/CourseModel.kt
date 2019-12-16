package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codingblocks.onlineapi.models.Project

@Entity
data class CourseModel(
    @PrimaryKey
    var cid: String,
    var title: String,
    var subtitle: String,
    var logo: String,
    var summary: String,
    var promoVideo: String,
    var difficulty: String,
    var reviewCount: Int,
    var rating: Float,
    var slug: String?,
    var coverImage: String,
    var categoryId: Int,
    var faq: String? = "",
    var projectsId: ArrayList<Project?>? = null
) {
    constructor() : this("", "", "", "", "",
        "", "", 0, 0f, "",
        "", -1, "", null)
}
