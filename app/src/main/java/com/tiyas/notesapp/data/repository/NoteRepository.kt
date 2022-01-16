package com.tiyas.notesapp.data.repository

import androidx.lifecycle.LiveData
import com.tiyas.notesapp.data.NoteDao
import com.tiyas.notesapp.data.models.NoteData

class NoteRepository(private  val noteDao : NoteDao) {

    val getAllData : LiveData<List<NoteData>> = noteDao.getDataAll()
    val sortByHighPriority : LiveData<List<NoteData>> = noteDao.sortByHighPriority()
    val sortByLowPriority : LiveData<List<NoteData>> = noteDao.sortByLowPriority()

    fun  insertData(noteData: NoteData){
        noteDao.insertData(noteData)
    }

    fun  updateData(noteData: NoteData){
        noteDao.updateData(noteData)
    }

    fun deleteData(noteData: NoteData){
        noteDao.deleteData(noteData)
    }

    fun deleteAllData(){
        noteDao.deleteAllData()
    }

    fun searchData(searchQuery : String) : LiveData<List<NoteData>>{
       return noteDao.searchDatabase(searchQuery)
    }
}