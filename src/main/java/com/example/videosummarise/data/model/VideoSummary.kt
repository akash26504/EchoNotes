package com.example.videosummarise.data.model

import android.os.Parcel
import android.os.Parcelable

data class VideoSummary(
    val id: String,
    val videoUri: String,
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val captions: List<Caption>,
    val duration: Long,
    val createdAt: Long = System.currentTimeMillis()
)

data class Caption(
    val startTime: Long, // in milliseconds
    val endTime: Long,   // in milliseconds
    val text: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startTime)
        parcel.writeLong(endTime)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Caption> {
        override fun createFromParcel(parcel: Parcel): Caption {
            return Caption(parcel)
        }

        override fun newArray(size: Int): Array<Caption?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedStartTime(): String {
        val seconds = startTime / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun getFormattedTimeRange(): String {
        val startSeconds = startTime / 1000
        val endSeconds = endTime / 1000
        val startMinutes = startSeconds / 60
        val endMinutes = endSeconds / 60
        val startRemaining = startSeconds % 60
        val endRemaining = endSeconds % 60
        return String.format("%02d:%02d - %02d:%02d", startMinutes, startRemaining, endMinutes, endRemaining)
    }
}
