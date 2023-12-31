package com.androiddevelopers.freelanceapp.di

import android.content.Context
import androidx.room.Room
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.dao.UserDao
import com.androiddevelopers.freelanceapp.database.UserDatabase
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoImpl
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoImpl
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database_version_1" // Veritabanı adını buraya ekleyin
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(appDatabase: UserDatabase): UserDao {
        return appDatabase.userDao()
    }
    @Provides
    @Singleton
    fun provideGlide(@ApplicationContext context: Context) : RequestManager  {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.error)
                    .placeholder(circularProgressDrawable)
            )
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideFirebaseFireStore() = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirebaseRepo(auth: FirebaseAuth, firestore: FirebaseFirestore): FirebaseRepoInterFace {
        return FirebaseRepoImpl(auth,firestore)
    }
    @Singleton
    @Provides
    fun provideRoomUserDatabaseRepo(dao: UserDao): RoomUserDatabaseRepoInterface {
        return RoomUserDatabaseRepoImpl(dao)
    }
}
