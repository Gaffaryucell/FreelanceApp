package com.androiddevelopers.freelanceapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevelopers.freelanceapp.dao.UserDao
import com.androiddevelopers.freelanceapp.model.UserProfileModel

@Database(entities = [UserProfileModel::class], version = 3)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}