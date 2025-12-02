package com.example.aikotlin.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.aikotlin.database.DateTypeConverter
import java.util.Date

@Entity(tableName = "news_articles")
@TypeConverters(DateTypeConverter::class)
data class NewsArticle(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val source: String,
    val author: String?,
    val publishedAt: Date,
    val url: String,
    val urlToImage: String?,
    val category: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        Date(parcel.readLong()),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(source)
        parcel.writeString(author)
        parcel.writeLong(publishedAt.time)
        parcel.writeString(url)
        parcel.writeString(urlToImage)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsArticle> {
        override fun createFromParcel(parcel: Parcel): NewsArticle {
            return NewsArticle(parcel)
        }

        override fun newArray(size: Int): Array<NewsArticle?> {
            return arrayOfNulls(size)
        }
    }
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

data class NewsCategory(
    val id: String,
    val name: String,
    val iconResId: Int
)