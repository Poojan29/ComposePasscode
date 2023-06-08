# Compose Passcode

The Passcode Android Jetpack Library is tool designed to simplify the implementation of passcode functionality in Android applications. It is built on top of the Android Jetpack components, which provide a set of libraries and guidelines to develop robust and efficient Android apps.

[![](https://jitpack.io/v/Poojan29/ComposePasscode.svg)](https://jitpack.io/#Poojan29/ComposePasscode)
<p>
<img src="https://github.com/Poojan29/ComposePasscode/blob/master/image/passcode.png" width="200" height="400">
<img src="https://github.com/Poojan29/ComposePasscode/blob/master/image/drag_passcode.png" width="200" height="400">
</p>

## Features

- [x] Number Passcode
- [x] Drag Passcode


#### Step 1

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


#### Step 2

Add the desired calendar library (view or compose) to your app `build.gradle`:

```groovy
dependencies {
  // The compose passcode library
  implementation 'implementation 'com.github.Poojan29:ComposePasscode:<latest-version>'
}
```

You can find the latest version of the library on the JitPack badge above.


## Usage

Implement `PasscodeListener` in your Activity
```kotlin
private var passcodeManager: PasscodeManager = PasscodeManager() // Available from the library
private var passcodeViewModel: PasscodeViewModel = PasscodeViewModel(passcodeManager) // Available from the library

@Composable
fun MainScreen() {
    // just add Passcode or DraggablePasscode composable function
    PasscodeScreen(
        viewModel = passcodeViewModel, 
        this, 
        passcodeManager
    )
    DraggablePasscode(
        dragViewModel = passcodeViewModel, 
        this, 
        passcodeManager
    )
}
```

Based on user input you will get passcode in `onPassCodeReceive` callback function.
<br>
Please not that you can check entered passcode with `PasscodeManager`

```kotlin
override fun onPassCodeReceive(passcode: String) {
    if (passcodeManager.getSavedDragPasscode() == passcode) {
        // Navigate to new screen
    } else {
        // Show error message
    }
}
```

## Do you like the project?

`Do support us by giving a ⭐ to the repository! :-)`

## License
[MIT License](LICENSE)