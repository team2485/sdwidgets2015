Team 2485 Smart Dashboard Widgets 2015
=================================

These can be used to add functionality and customize the look and feel of [SmartDashboard](http://firstforge.wpi.edu/sf/projects/smartdashboard).

Read more about how to use SmartDashboard [here](http://wpilib.screenstepslive.com/s/3120/m/7932).

Widgets
-------

| Widget                                                | Class                      |
| ----------------------------------------------------- | -------------------------- |
| Air tank widget                                       | `AirTankWidget`            |
| Autonomous mode chooser                               | `AutoChooser`              |
| Axis camera image processor                           | `AxisCameraProcessor2`     |
| Background and logo connection indicator              | `Background`               |
| Battery voltage indicator                             | `BatteryWidget`            |
| Better booleans                                       | `CoolBool`                 |
| Debug region indicator (hidden behind DS)             | `DebugRegion`              |
| Large text                                            | `LargeText`                |
| Light mode switcher                                   | `LightSwitcher`            |
| Robot data logger (saves logs to file)                | `Logger`                   |
| Nyan cats (static and data-driven)                    | `NyanCat`, `StaticNyanCat` |
| Pandaboard connection indicator (happy panda)         | `PandaboardIndicator`      |
| Potentiometer value, reset, and ready state indicator | `PotWidget`                |
| Guitar Hero X-plorer strummer axis sender             | `StrummerSender`           |
| Toggle button (displays and sends a boolean)          | `ToggleButton`             |

Notes
-----

The Smart Dashboard widget JARs are generated in `extensions/` so that they can be easily tested by running `SmartDashboard.jar` using the default extension directory. Use `SmartDashboardDebug.bat` to quickly test the functionality of the built widgets.

The SRC folder must be exported as a JAR file to `extensions/SmartDashWidgets.jar` inorder for any changes to be applied.

The 64-bit OpenCV JNI and JavaCV libraries are included. **Make sure to run SmartDashboard with a 64-bit version of Java.**

JavaCV automatically copies the necessary javacpp dll's to the Windows `%TEMP%\javacpp[timestamp]` directory at runtime. This will slow down SmartDashboard and take up disk space. To avoid this, you can copy all the dll's inside `lib\ffmpeg-2.1.1-windows-x86_64.jar`, `lib\javacv-windows-x86_64.jar`, and `lib\opencv-2.4.8-windows-x86_64.jar` to the SmartDashboard installation directory alongside the SmartDashboard executable if SmartDashboard is installed, alongside this project's `SmartDashboard.jar` when developing, or somewhere in the path. To access the dll's inside the .`jar`s, rename the `.jar` files to `.zip` to open them or use a utility like [7-Zip](http://www.7-zip.org/) to open the `.jar`s directly. Then delete or rename those `.jar`s so that they aren't copied with the extension when building. Make sure you don't commit these changes!
