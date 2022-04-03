package com.joeloewi.croissant.data.common

class NotSupportedGameException : Exception() {
    override val message: String = "Accessed to game which is not supported yet"
}