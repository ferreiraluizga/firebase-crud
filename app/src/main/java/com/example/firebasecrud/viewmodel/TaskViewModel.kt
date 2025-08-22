package com.example.firebasecrud.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasecrud.data.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TaskViewModel: ViewModel() {

    private val db = Firebase.firestore

    private var _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    init {
        getAll()
    }

    fun save(task: Task) {
        task.id = UUID.randomUUID().toString()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tasks").document(task.id).set(task).await()
                _tasks.postValue(_tasks.value?.plus(task))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // select all tasks
    fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.collection("tasks").get().await()

                val tasks = result.documents.mapNotNull { it.toObject(Task::class.java) }
                _tasks.postValue(tasks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun update(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tasks").document(task.id).update(task.toMap()).await()
                _tasks.postValue(_tasks.value?.map { if (it.id == task.id) task else it })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tasks").document(id).delete().await()
                _tasks.postValue(_tasks.value?.filter { it.id != id })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}