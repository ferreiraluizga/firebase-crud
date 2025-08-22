package com.example.firebasecrud.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasecrud.data.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        getAll()
    }

    fun save(task: Task) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskRef = db.collection("tasks").document(task.id)
                taskRef.set(task).await()
                _tasks.postValue(_tasks.value?.plus(task))
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
                val result = db.collection("tasks").get().await()
                val tasks = result.documents.mapNotNull { it.toObject(Task::class.java) }
                _tasks.postValue(tasks)
                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue("Error loading tasks: ${e.message}")
            }
        }
    }

    fun update(task: Task) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tasks").document(task.id).set(task).await()
                _tasks.postValue(_tasks.value?.map { if (it.id == task.id) task else it })
                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue("Error updating task: ${e.message}")
            }
        }
    }

    fun delete(id: String) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tasks").document(id).delete().await() // Delete task
                _tasks.postValue(_tasks.value?.filter { it.id != id })
                _loading.postValue(false)
            } catch (e: Exception) {
                _loading.postValue(false)
                _error.postValue("Error deleting task: ${e.message}")
            }
        }
    }

}