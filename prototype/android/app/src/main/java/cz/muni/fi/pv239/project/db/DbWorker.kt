package cz.muni.fi.pv239.project.db

import android.os.Handler
import android.os.HandlerThread

class DbWorker {

    private val handlerThread = HandlerThread("WorkerThread")
    private val handler: Handler

    init {
        handlerThread.start()
        val looper = handlerThread.looper
        handler = Handler(looper)
    }

    fun postTask(task: Runnable) {
        handler.post(task)
    }

    fun destroy() {
        handlerThread.quit()
    }

}