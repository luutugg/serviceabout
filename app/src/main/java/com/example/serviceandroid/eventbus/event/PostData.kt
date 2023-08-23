package com.example.serviceandroid.eventbus.event

import com.example.serviceandroid.eventbus.IEvent

class PostData(var currentPosition: Int, val maxPosition: Int): IEvent

