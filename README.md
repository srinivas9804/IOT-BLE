# Iot-BLE
Android application to connect with a microchip RN4870 BLE chip, using the transparent UART channel

1. For BLE, you need to enable location services to discover nearby devices.
2. Uses a Room database to store data locally and uses the LiveData and ViewModel frameworks to asynchronously display data to the user.
3. Data can also be uploaded to a node.js server. Using android Volley to send HTTP requests.
4. Data retrieved from the server is displayed as a linechart using the MPChart library for android.
