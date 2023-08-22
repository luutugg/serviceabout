package com.example.serviceandroid.model

enum class SONG(val value: Int) {
    SONG_1(1),
    SONG_2(2),
    SONG_3(3);

    companion object{
        fun findSong(value: Int): SONG{
            val item = values().find {
                it.value == value
            }
            return item?: throw Exception("loi song")
        }
    }
}
