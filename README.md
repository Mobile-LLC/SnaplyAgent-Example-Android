# Snaply Agent — Android example

A complete, runnable integration of the Snaply Agent Android SDK. Clone it, drop in your key, run it.

The whole integration is in
[`MainActivity.kt`](app/src/main/java/com/example/snaplyexample/MainActivity.kt) — around a dozen
lines of it. Everything else on screen is a fake food-delivery order, there so the SDK has something
realistic to capture.

## Run it

1. Open the project in Android Studio (or use the Gradle wrapper).
2. In `MainActivity.kt`, replace the placeholder key:

   ```kotlin
   const val SnaplyKey = "snap_live_REPLACE_WITH_YOUR_KEY"
   ```

   Get yours from the Snaply console → **Products → your product → API key**.

3. Add your app's package name to that product's allowed package names, or registration is refused.
   This example uses `com.example.snaplyexample`.
4. Run on a device or emulator. The screen should say *"Connected — this device is now visible in
   the console."*
5. Open the console, find the device, and request a screenshot. The consent prompt appears on the
   phone; nothing is captured until it is allowed.

## Adding the SDK to your own app

One dependency, from Maven Central. No credentials, no extra repository:

```kotlin
dependencies {
    implementation("com.snaplyagent:agent:2.39.0")
}
```

Then, once at app start:

```kotlin
Snaply.on(object : SnaplyCallbacks { /* … */ })
Snaply.init(context = this, key = "snap_live_…")
```

`minSdk` 24 and Java 17 are the SDK's floors.

## Redaction — read this before you ship

**The SDK automatically redacts password fields and nothing else.** Everything else on screen is
captured as-is. Mark whatever you want hidden:

```kotlin
cardNumberField.snaplyRedact()          // or: view.isSnaplyRedacted = true
```

You can also set a view's `contentDescription` to contain `snaply-redact`, which is useful when the
view is created in XML.

The box is painted **on the device, before the image is encoded**, so the underlying pixels never
leave the phone. It is not a blur applied on the server, and there is no version of the frame that
contains the real content.

One current limit: redaction walks the Android **View** tree. Jetpack Compose draws into a single
view, so per-composable redaction is not covered yet.

## What the SDK does and does not capture

- Captures **this app's own window only** — never the launcher, never another app. That is why no
  screen-recording permission is requested and no system recording dialog appears.
- Captures **only after the user allows it**. Consent is the product, not a setting.
- While a live session runs, a persistent on-screen indicator stays up, and the SDK stops sending
  the moment it can no longer prove that indicator is attached.

## Permissions

None to declare. The SDK's own manifest contributes `INTERNET` and `ACCESS_NETWORK_STATE`, and the
manifest merger adds them to your app.

## Identifying your users

Optional, and it changes only what support sees:

```kotlin
// Known at launch:
Snaply.init(context = this, key = key, user = SnaplyUser(id = "usr_20481", name = "Maya Kowalski"))

// Or later, when they sign in:
Snaply.identify(id = "usr_20481", name = "Maya Kowalski", phone = "+14155550142")

// On sign-out:
Snaply.reset()
```

Without it the device stays anonymous, and support finds it by the support code the person reads
out — `Snaply.showSupportCode()`.

## Errors worth handling

Failures arrive on `SnaplyCallbacks.onError` as a `SnaplyException` carrying a `code`:

| `code` | Meaning |
|---|---|
| `invalid_key` | The key is wrong, or still the placeholder |
| `origin_mismatch` | This app's package name is not on the product's allowed list |
| `quota_exceeded` | The workspace is out of screenshots for the cycle |


## Vendor ID

`Snaply.init(..., collectVendorId = false)` opts out of sending `Settings.Secure.ANDROID_ID`. It is
on by default, and **you** own the Play Data-safety declaration for it: either declare *Device or
other IDs*, or pass `false`. It is never the identity key — an install-scoped UUID is.

## Docs

Full documentation: <https://snaplyagent.com/docs>
