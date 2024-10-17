package com.example.filmeapp

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val title: String = "",
    val description: String = "",
    val release_date: String = "",
    val poster_path: String = "",
    val genres: List<String> = listOf(), // Gêneros como uma lista de Strings
    val videoUrl: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(release_date)
        parcel.writeString(poster_path)
        parcel.writeStringList(genres)
        parcel.writeString(videoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}
