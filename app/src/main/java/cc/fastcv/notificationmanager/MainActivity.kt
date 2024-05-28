package cc.fastcv.notificationmanager

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"

        private const val IMPORTANCE_NONE_CHANNEL_ID = "noneChannel"
        private const val IMPORTANCE_MIN_CHANNEL_ID = "minChannel"
        private const val IMPORTANCE_LOW_CHANNEL_ID = "lowChannel"
        private const val IMPORTANCE_DEFAULT_CHANNEL_ID = "defaultChannel"
        private const val IMPORTANCE_HIGH_CHANNEL_ID = "highChannel"
    }

    private var notificationId = 0

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        findViewById<AppCompatButton>(R.id.btSend).setOnClickListener {
            createNotificationForNormal()
        }

        findViewById<AppCompatButton>(R.id.btActiveNotifications).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (activeNotification in notificationManager.activeNotifications) {
                    Log.d(TAG, "onCreate: $activeNotification")
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotificationChannel()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 200)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initNotificationChannel() {
        val noneChannel = NotificationChannel(
            IMPORTANCE_NONE_CHANNEL_ID,
            "无效的通知",
            NotificationManager.IMPORTANCE_NONE
        )
        val minChannel = NotificationChannel(
            IMPORTANCE_MIN_CHANNEL_ID,
            "重要性小的通知",
            NotificationManager.IMPORTANCE_MIN
        )
        val lowChannel = NotificationChannel(
            IMPORTANCE_LOW_CHANNEL_ID,
            "重要性低的通知",
            NotificationManager.IMPORTANCE_LOW
        )
        val defaultChannel = NotificationChannel(
            IMPORTANCE_DEFAULT_CHANNEL_ID,
            "重要性普通的通知",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val highChannel = NotificationChannel(
            IMPORTANCE_HIGH_CHANNEL_ID,
            "重要性高的通知",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(noneChannel)
        notificationManager.createNotificationChannel(minChannel)
        notificationManager.createNotificationChannel(lowChannel)
        notificationManager.createNotificationChannel(defaultChannel)
        notificationManager.createNotificationChannel(highChannel)
    }

    private fun createNotificationForNormal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 创建通知(标题、内容、图标)
            val notification: Notification = Notification.Builder(this)
                .setContentTitle(getInputTitle())
                .setContentText(getInputContent())
                .setSmallIcon(getSmallIcon())
                .setContentIntent(getContentIntent())
                .build()
            notificationManager.notify(notificationId++, notification)
        } else {
            val notification = Notification.Builder(applicationContext, getChannelId())
                .setContentTitle(getInputTitle()) // 标题
                .setContentText(getInputContent()) // 文本
                .setSmallIcon(getSmallIcon()) // 小图标
                .setLargeIcon(getLargeIcon()) // 大图标
                .setPriority(getPriority()) // 7.0 设置优先级
                .setContentIntent(getContentIntent()) // 跳转配置
                .setNumber(getNumber())
                .setBadgeIconType(getBadgeIconType())
//                .addAction(
//                    R.drawable.ic_baseline_perm_phone_msg_24,
//                    "去看看",
//                    pendingIntent
//                )// 通知上的操作
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
                .setVisibility(getVisibility()) // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏
                .setAutoCancel(getAutoCancel())  // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
            notificationManager.notify(notificationId++, notification.build())
        }
    }

    private fun getInputTitle(): String {
        return findViewById<AppCompatEditText>(R.id.etTitle).text.toString()
    }

    private fun getInputContent(): String {
        return findViewById<AppCompatEditText>(R.id.etContent).text.toString()
    }

    private fun getSmallIcon(): Int {
        return if (findViewById<RadioButton>(R.id.rbIc1).isChecked) {
            R.drawable.ic_small_icon1
        } else if (findViewById<RadioButton>(R.id.rbIc2).isChecked) {
            R.drawable.ic_small_icon2
        } else if (findViewById<RadioButton>(R.id.rbIc3).isChecked) {
            R.drawable.ic_small_icon3
        } else {
            R.drawable.ic_small_icon4
        }
    }

    private fun getLargeIcon(): Bitmap {
        val resId = if (findViewById<RadioButton>(R.id.rbImg1).isChecked) {
            R.drawable.img1
        } else if (findViewById<RadioButton>(R.id.rbImg2).isChecked) {
            R.drawable.img2
        } else {
            R.drawable.img3
        }
        return BitmapFactory.decodeResource(resources, resId)
    }

    private fun getPriority(): Int {
        return if (findViewById<RadioButton>(R.id.rbPriority1).isChecked) {
            0
        } else if (findViewById<RadioButton>(R.id.rbPriority2).isChecked) {
            -1
        } else if (findViewById<RadioButton>(R.id.rbPriority3).isChecked) {
            -2
        } else if (findViewById<RadioButton>(R.id.rbPriority4).isChecked) {
            1
        } else {
            2
        }
    }

    private fun getContentIntent(): PendingIntent {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this, 2000, Intent(this, TargetActivity::class.java), PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                2000,
                Intent(this, TargetActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return pendingIntent
    }

    private fun getNumber(): Int {
        return findViewById<AppCompatEditText>(R.id.etNumberOfDesktopNotifications).text.toString()
            .toInt()
    }

    private fun getAutoCancel(): Boolean {
        return findViewById<SwitchMaterial>(R.id.swAutoCancel).isChecked
    }

    private fun getChannelId(): String {
        return if (findViewById<RadioButton>(R.id.rbImportance1).isChecked) {
            IMPORTANCE_NONE_CHANNEL_ID
        } else if (findViewById<RadioButton>(R.id.rbImportance2).isChecked) {
            IMPORTANCE_MIN_CHANNEL_ID
        } else if (findViewById<RadioButton>(R.id.rbImportance3).isChecked) {
            IMPORTANCE_LOW_CHANNEL_ID
        } else if (findViewById<RadioButton>(R.id.rbImportance4).isChecked) {
            IMPORTANCE_DEFAULT_CHANNEL_ID
        } else {
            IMPORTANCE_HIGH_CHANNEL_ID
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBadgeIconType(): Int {
        return if (findViewById<RadioButton>(R.id.rbBadge1).isChecked) {
            Notification.BADGE_ICON_NONE
        } else if (findViewById<RadioButton>(R.id.rbBadge2).isChecked) {
            Notification.BADGE_ICON_SMALL
        } else {
            Notification.BADGE_ICON_LARGE
        }
    }

    private fun getVisibility() : Int {
        return if (findViewById<RadioButton>(R.id.rbVisible1).isChecked) {
            Notification.VISIBILITY_PUBLIC
        } else if (findViewById<RadioButton>(R.id.rbVisible2).isChecked) {
            Notification.VISIBILITY_PRIVATE
        } else {
            Notification.VISIBILITY_SECRET
        }
    }
}