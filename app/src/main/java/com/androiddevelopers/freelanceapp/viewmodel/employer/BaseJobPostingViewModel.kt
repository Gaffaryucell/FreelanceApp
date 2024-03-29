package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toEmployerJobPost
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

open class BaseJobPostingViewModel(
    val firebaseRepo: FirebaseRepoInterFace,
    val sharedPreferences: SharedPreferences,
    auth: FirebaseAuth
) : BaseNotificationViewModel(firebaseRepo, auth) {

    var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    var _firebaseLiveData = MutableLiveData<List<EmployerJobPost>>()
    val firebaseLiveData: LiveData<List<EmployerJobPost>>
        get() = _firebaseLiveData

    private var _firebaseLiveDataEmployerJobPost = MutableLiveData<EmployerJobPost>()
    val firebaseLiveDataEmployerJobPost: LiveData<EmployerJobPost>
        get() = _firebaseLiveDataEmployerJobPost

    private var _firebaseUserData = MutableLiveData<UserModel>()
    val firebaseUserData: LiveData<UserModel>
        get() = _firebaseUserData

    private var _firebaseUserListData = MutableLiveData<List<UserModel>>()
    val firebaseUserListData: LiveData<List<UserModel>>
        get() = _firebaseUserListData

    private var _firebaseListenerForChange = MutableLiveData<Boolean>()
    val firebaseListenerForChange: LiveData<Boolean>
        get() = _firebaseListenerForChange

    fun getAllEmployerJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllEmployerJobPostFromFirestore().addOnSuccessListener {

            _firebaseMessage.value = Resource.loading(false)

            it?.let { querySnapshot ->
                val list = mutableListOf<EmployerJobPost>()
                val userIdList = mutableSetOf<String>()

                for (document in querySnapshot) {
                    document.toEmployerJobPost()?.let { employerJobPost ->
                        if (employerJobPost.status == JobStatus.OPEN) {
                            list.add(employerJobPost)
                            employerJobPost.employerId?.let { id -> userIdList.add(id) }
                        }
                    }
                }

                if (userIdList.isNotEmpty()) {
                    getUserDataByDocumentIdList(userIdList.toList())
                }

                _firebaseLiveData.value = list

            }
            _firebaseMessage.value = Resource.success(true)

        }.addOnFailureListener {
            _firebaseMessage.value = Resource.loading(false)

            it.localizedMessage?.let { message ->
                _firebaseMessage.value = Resource.error(message, false)
            }
        }
    }

    fun getUserDataByDocumentId(documentId: String) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getUserDataByDocumentId(documentId).addOnSuccessListener { document ->
            document.toUserModel()?.let { userModel ->
                _firebaseUserData.value = userModel
            } ?: run {
                _firebaseMessage.value =
                    Resource.error("Bu hesapla eşleşen kullanıcı bulunamadı", null)
            }

            _firebaseMessage.value = Resource.loading(false)
            _firebaseMessage.value = Resource.success(true)

        }.addOnFailureListener {
            _firebaseMessage.value = Resource.loading(false)

            it.localizedMessage?.let { message ->
                Resource.error(message, false)
            }
        }
    }

    private fun getUserDataByDocumentIdList(list: List<String>) = viewModelScope.launch {
        firebaseRepo.getUsersFromFirestore(list).addOnSuccessListener { querySnapshot ->
            val users = mutableListOf<UserModel>()

            for (document in querySnapshot) {
                document.toUserModel()?.let { user ->
                    users.add(user)
                }
            }

            _firebaseUserListData.value = users
            _firebaseMessage.value = Resource.success(true)
        }.addOnFailureListener {
            _firebaseMessage.value = Resource.loading(false)

            it.localizedMessage?.let { message ->
                Resource.error(message, false)
            }
        }
    }

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toEmployerJobPost()?.let { employerJobPost ->
                        _firebaseLiveDataEmployerJobPost.value = employerJobPost
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", false)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
        }

    fun updateSavedUsersEmployerJobPostFromFirestore(
        userId: String, postId: String, isSavedPost: Boolean, savedUsers: List<String>
    ) = viewModelScope.launch {
        val list = mutableSetOf<String>()
        list.addAll(savedUsers)

        if (isSavedPost) {
            list.add(userId)
        } else {
            list.remove(userId)
        }

        firebaseRepo.updateSavedUsersEmployerJobPostFromFirestore(postId, list.toList())
            .addOnCompleteListener {
                _firebaseMessage.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _firebaseMessage.value = Resource.success(true)
                } else {
                    _firebaseMessage.value = Resource.loading(false)
                    it.exception?.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
            }
    }

    fun getListenerForChange() = viewModelScope.launch {
        _firebaseListenerForChange.value =
            sharedPreferences.getBoolean("employer_job_post_is_change", false)
    }

    fun setListenerForChange(isChangeSavedPost: Boolean) = viewModelScope.launch {
        sharedPreferences.edit().putBoolean("employer_job_post_is_change", isChangeSavedPost)
            .apply()
    }

    fun sendFirstMessage(
        chatId: String,
        messageData: String,
        messageReceiver: String,
    ) {
        val usersMessage = creatMessageModelForCurrentUser(
            messageData,
            currentUserId,
            messageReceiver
        )
        firebaseRepo.sendMessageToPreChatRoom(
            currentUserId,
            messageReceiver,
            chatId,
            usersMessage
        )
    }

    private fun creatMessageModelForCurrentUser(
        messageData: String,
        messageSender: String,
        messageReceiver: String
    ): MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId,
            messageData,
            messageSender,
            messageReceiver,
            getCurrentTime()
        )
    }
}