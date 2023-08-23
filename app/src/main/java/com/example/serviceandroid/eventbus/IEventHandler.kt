package com.example.serviceandroid.eventbus

import com.example.serviceandroid.eventbus.IEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

interface IEventHandler {
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: IEvent)
}
