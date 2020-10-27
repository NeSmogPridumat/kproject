package com.dteam.kproject.data

open class Event <out T> (private val content: T){

    private var hasBeenHandler = false

    fun getContentIfNotHandler(): T? {
        return if (hasBeenHandler){
            null
        } else {
            hasBeenHandler = true
            content
        }
    }

    fun peekContent(): T = content
}