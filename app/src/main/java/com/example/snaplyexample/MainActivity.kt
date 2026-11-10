package com.example.snaplyexample

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.snaply.agent.CaptureResult
import com.snaply.agent.LiveSession
import com.snaply.agent.Snaply
import com.snaply.agent.SnaplyCallbacks
import com.snaply.agent.SnaplyUser
import com.snaply.agent.snaplyRedact

/**
 * A complete Snaply integration in one file.
 *
 * The screen itself is deliberately plain — a fake order with a failed payment, which is the
 * situation someone actually contacts support about. What matters is the three calls below:
 * [Snaply.on], [Snaply.init], and [Snaply.showSupportCode].
 */
class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildLayout())

        // 1. Listen before you initialise. A consent request can arrive as soon as the device is
        //    registered, and callbacks attached afterwards would miss it.
        Snaply.on(object : SnaplyCallbacks {
            override fun onRequestShown() = show("Support asked to see your screen…")
            override fun onAllowed(capture: CaptureResult) = show("Screenshot shared ✓")
            override fun onDenied() = show("You declined the request.")
            override fun onExpired() = show("The request expired.")
            override fun onError(error: Exception) = show("Snaply error: ${error.message}")
            override fun onLiveEnded(session: LiveSession) = show("Live session ended.")
        })

        // 2. Initialise with YOUR key from the Snaply console (Products → your product → API key).
        //    `user` is optional: pass it when you know who this is, and call Snaply.identify(...)
        //    later if they sign in after launch. Without it the device stays anonymous and support
        //    finds it by the support code instead.
        Snaply.init(
            context = this,
            key = SnaplyKey,
            user = SnaplyUser(id = "usr_20481", name = "Maya Kowalski"),
        )
        show("Connected — this device is now visible in the console.")
    }

    private fun show(message: String) = runOnUiThread { status.text = message }

    private fun buildLayout(): LinearLayout {
        status = TextView(this).apply {
            text = "Connecting…"
            textSize = 13f
            gravity = Gravity.CENTER
        }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)

            addView(TextView(this@MainActivity).apply {
                text = "Acme Delivery"
                textSize = 22f
                gravity = Gravity.CENTER
            })
            addView(TextView(this@MainActivity).apply {
                text = "Order #8412 · Margherita pizza · 2"
                textSize = 13f
                gravity = Gravity.CENTER
            })
            // The one line that matters most in this file.
            //
            // The SDK auto-redacts PASSWORD fields and nothing else. A card number is not a
            // password, so without snaplyRedact() it would be captured in full. Mark anything you
            // would not want a support agent to read: card numbers, addresses, medical details,
            // another customer's data on screen.
            //
            // The box is painted on the device BEFORE the image is encoded, so the real pixels
            // never leave the phone — this is not a server-side blur that could be undone.
            addView(EditText(this@MainActivity).apply {
                setText("4242 4242 4242 4242")
                hint = "Card number"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
                snaplyRedact()
            })
            addView(TextView(this@MainActivity).apply {
                text = "Payment failed — card declined (402)"
                textSize = 13f
                gravity = Gravity.CENTER
            })
            addView(Button(this@MainActivity).apply {
                text = "Show support code"
                // 3. What the person reads out to the agent so support can find this device.
                setOnClickListener { Snaply.showSupportCode() }
            })
            addView(status)
        }
    }

    private companion object {
        /**
         * Replace with your own key from the Snaply console.
         *
         * It is not a secret — it identifies the workspace, is visible in any shipped app, and is
         * matched against your allowed package names on the server, so a copy of it is useless
         * from another app. Committing it is fine; this placeholder exists only so the example
         * fails with a clear message rather than silently talking to someone else's workspace.
         */
        const val SnaplyKey = "snap_live_REPLACE_WITH_YOUR_KEY"
    }
}
