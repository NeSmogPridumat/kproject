package com.dteam.kproject

open class Event <out T> (private val content: T){

    var hasBeenHandler = false
        private set

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