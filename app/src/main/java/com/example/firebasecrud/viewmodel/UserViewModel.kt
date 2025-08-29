package com.example.firebasecrud.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasecrud.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _users = MutableLiveData<List<User>>(emptyList())
    val users: LiveData<List<User>> = _users

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        getAll()
    }

    fun save(user: User) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskRef = db.collection("users").document(user.id)
                taskRef.set(user).await()
                _users.postValue(_users.value?.plus(user))
                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue("Error saving task: ${e.message}")
            }
        }
    }

    fun getAll() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.collection("users").get().await()
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                _users.postValue(users)
                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue("Error loading tasks: ${e.message}")
            }
        }
    }

}