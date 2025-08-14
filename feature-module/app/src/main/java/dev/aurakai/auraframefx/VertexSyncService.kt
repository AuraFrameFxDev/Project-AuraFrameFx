package dev.aurakai.auraframefx

import android.app.Service
import android.content.Intent
import android.os.IBinder

// @AndroidEntryPoint // Temporarily disabled for successful build
class VertexSyncService : Service() {
    // Example dependency injection (add real dependencies as needed)
    // @Inject private lateinit var syncManager: SyncManager
    override fun onBind(_intent: Intent?): IBinder? {
        // Not designed for binding; implement if needed
        return null
    }

    override fun onStartCommand(_intent: Intent?, _flags: Int, _startId: Int): Int {
        // Implement service logic here (e.g., start sync tasks)
        return START_NOT_STICKY
    }
}
