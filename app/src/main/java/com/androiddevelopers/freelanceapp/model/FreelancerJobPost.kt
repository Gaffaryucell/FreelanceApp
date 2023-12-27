package com.androiddevelopers.freelanceapp.model

import com.androiddevelopers.freelanceapp.util.JobStatus

class FreelancerJobPost {
    var postId: String? = null // İlanın benzersiz kimliği
    var freelancerId: String? = null // İlanı oluşturan freelancer'ın kimliği
    var title: String? = null // İlan başlığı
    var description: String? = null // İlan açıklaması
    var images : List<String>? = null // İlan göreslleri
    var skillsRequired: List<String>? = null // İstenen yeteneklerin listesi
    var budget: Double? = null // İş için ayrılan bütçe
    var deadline: String? = null // İlanın son başvuru tarihi
    var location: String? = null // İşin yapılacağı konum
    var datePosted: String? = null // İlanın yayımlandığı tarih
    var applicants: List<String>? = null // Başvuranların listesi
    var status: JobStatus? = null // İlanın durumu (Açık, Kapalı, Tamamlanan)
    var rating: Double? = null // İşveren tarafından işin başarı derecesini belirtmek için
    var additionalDetails: String? = null // İş ilanıyla ilgili ek detaylar
    var viewCount: Int? = null // İlanın kaç kez görüntülendiği
    var isUrgent: Boolean? = null // İşin acil olduğunu belirtmek için

    constructor()

    constructor(
        postId: String?,
        freelancerId: String?,
        title: String?,
        description: String?,
        images: List<String>?,
        skillsRequired: List<String>?,
        budget: Double?,
        deadline: String?,
        location: String?,
        datePosted: String?,
        applicants: List<String>?,
        status: JobStatus?,
        rating: Double?,
        additionalDetails: String?,
        viewCount: Int?,
        isUrgent: Boolean?
    ) {
        this.postId = postId
        this.freelancerId = freelancerId
        this.title = title
        this.description = description
        this.images = images
        this.skillsRequired = skillsRequired
        this.budget = budget
        this.deadline = deadline
        this.location = location
        this.datePosted = datePosted
        this.applicants = applicants
        this.status = status
        this.rating = rating
        this.additionalDetails = additionalDetails
        this.viewCount = viewCount
        this.isUrgent = isUrgent
    }
}
