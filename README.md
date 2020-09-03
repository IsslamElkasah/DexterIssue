# DexterIssue
Reproducing an issue with Dexter library for Android permissions

- Open the app and grant some of the requested permissions and deny others
- Close the app and open it again and choose deny and don't ask again for whatever permissions you see
- You will see a dialog to open settings and grant the permanently denied permissions
- Go to settings and deny what you already granted
- Use the back arrows from settings to return to the app
- Observe the logcat when you return after denying already granted permissions, you will see the MainActivity re-creating itself by calling onCreate again

The expected behavior should be similar to going to settings and ALLOWING permissions. In that case, you will see the logcat goes: onStart -> onResume which is expected.
However, when you go to settings from inside the app, DENY what already was granted and return to the app using the back arrows, the logcat reads: onCreate -> onStart -> onResume

