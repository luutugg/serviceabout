package com.example.serviceandroid.eventbus.event

import com.example.serviceandroid.eventbus.IEvent
import com.example.serviceandroid.model.ACTION_SONG

class SendToService(val actionSong: ACTION_SONG?): IEvent
