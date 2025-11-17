This guide provides ultra-detailed, "do this, then do that" instructions for setting up the `AdSkipRemote` project from start to finish, with zero assumptions.

### Part 1: What is the "SDK" and Why Do We Need It? (The Simple Explanation)

Think of it like this:
*   The code for `AdSkipRemote` is a **recipe** for a cake.
*   The **Android SDK** (Software Development Kit) is your **kitchen**. It has the oven (the compiler), the mixing bowls (build tools), and the delivery driver (`adb`) who can take the finished cake (the app) to your phone.

You absolutely need the kitchen (SDK) to make the cake (the app).

The reason we use **Android Studio** is because it's a fully automated, state-of-the-art kitchen. You just hand it the recipe, press one big green "Run" button, and it handles everything—mixing, baking, and delivery—all by itself. Trying to do this from the command line is like trying to build the oven yourself before you can even start baking. For our purposes, Android Studio is the only way to go.

---

### Part 2: The "Click This, Do That" Guide

Follow these steps in order. Do not skip any.

#### Step A: Install the Automated Kitchen (Android Studio)
1.  Go to the official download page: [https://developer.android.com/studio](https://developer.android.com/studio)
2.  Click the big, green button that says **"Download Android Studio"**.
3.  Once the download is finished, run the installer file.
4.  The installer will ask you a few questions. Just click **"Next"** on all of them to accept the default settings. You don't need to change a thing.
5.  When it's done, open Android Studio.

#### Step B: Open Our Project
1.  When Android Studio starts, it will show a welcome window. Click the **"Open"** button.
2.  A file browser will appear. Navigate to the folder where you saved our project.
3.  Click on the **`AdSkipRemote`** folder just once to highlight it.
4.  Click **"OK"**.
5.  **IMPORTANT:** The very first time you open the project, it needs to "sync" and download all the specific tools for our recipe. You will see a loading bar at the bottom of the window. Please wait for it to finish completely. This can take several minutes.

#### Step C: Put Your Samsung Phone in Developer Mode
This is a secret handshake to tell your phone it's okay to install apps from your computer.

1.  On your **Samsung Galaxy S25**, open the main **Settings** app (the gear icon).
2.  Scroll all the way to the bottom and tap on **"About phone"**.
3.  On the next screen, tap on **"Software information"**.
4.  You will see a line that says **"Build number"**. Tap this line **7 times in a row**. After a few taps, it will show a countdown. You might need to enter your phone's PIN.
5.  A small message will pop up: **"Developer mode has been turned on"**.
6.  Go back to the main Settings screen (press the back button twice). Scroll to the very bottom, and you will now see a new option: **"Developer options"**. Tap it.
7.  In the Developer options menu, scroll down until you see a toggle for **"USB debugging"**. Turn this **ON**.
8.  Now, connect your phone to your computer with a USB cable. A pop-up will appear on your phone that says "Allow USB debugging?". Check the box that says "Always allow from this computer" and then tap **"Allow"**.

#### Step D: Put Your Galaxy Watch 6 in Developer Mode
Same secret handshake, but on your watch.

1.  On your **Galaxy Watch 6**, open the **Settings** app (the gear icon).
2.  Scroll down and tap on **"About watch"**.
3.  Tap on **"Software information"**.
4.  Find the **"Build number"** line and tap it **7 times**.
5.  A message will confirm that developer mode is on. Go back to the main watch Settings menu.
6.  You will now see **"Developer options"**. Tap on it.
7.  Turn ON the toggle for **"ADB debugging"**. It will ask you to confirm; tap the checkmark (✓).
8.  Right below that, turn ON the toggle for **"Wireless debugging"**. This is how the computer will talk to your watch.

#### Step E: Install the App on Your Phone
1.  Look at the toolbar at the top of the Android Studio window.
2.  You should see a dropdown menu that says **`app`**. Next to it, you should see your device, listed as something like **`Samsung SM-GXXXXU`** (your Galaxy S25).
3.  If that's all correct, click the green **"Run"** button (it looks like a play icon ▶).
4.  Wait a minute. Android Studio will build the project and install it on your phone. The "Ad Skip Remote" app will now be in your phone's app list.

#### Step F: Install the App on Your Watch
1.  Back in the Android Studio toolbar, click the dropdown that currently says `app` and select **`wear`** from the list.
2.  Now, look at the device dropdown next to it. It might be empty or show your phone. We need to connect the watch.
3.  In Android Studio, go to the top menu and click **View > Tool Windows > Device Manager**.
4.  In the Device Manager panel, click the Wi-Fi icon and choose **"Pair new device over Wi-Fi"**.
5.  A window with a QR code and a 6-digit code will appear.
6.  On your watch, go to **Settings > Developer options > Wireless debugging** and tap **"Pair new device"**.
7.  Enter the 6-digit code from your computer screen onto your watch.
8.  Once paired, your **Galaxy Watch 6 (SM-R935U)** will appear in the device list in Android Studio. Select it.
9.  Now, click the green **"Run"** button (▶) again. This will install the app on your watch.

#### Step G: The Final Permission
This is the last step to make it all work.

1.  On your **phone**, find and open the "Ad Skip Remote" app.
2.  Tap the button that says **"Enable Accessibility Service"**.
3.  Your phone will take you to an "Accessibility" settings page. Tap on **"Installed apps"**.
4.  Find **"Ad Skip Remote"** in this list and tap it.
5.  Turn the service **ON**. Your phone will give you a standard warning. This is okay. The app only looks for the "Skip Ad" button when you tell it to. Tap **"Allow"**.

You are all done! The entire system is now set up and ready to go.
