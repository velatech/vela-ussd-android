# VelaOffline SDK

[![](https://jitpack.io/v/org.bitbucket.vela_financial_services/velaobasdk.svg)](https://jitpack.io/#org.bitbucket.vela_financial_services/velaobasdk)

Vela OBA Offline SDK enables you to carry out financial transactions and bill payments offline.

## Getting Started

Follow the instructions below to get started with `VelaOffline` SDK.

### Prerequisites

Velaoffline SDK is in the private domain, to get access you need to request for `accessToken` and once you have that, add it to your `$HOME/.gradle/gradle.properties` for global access or project level gradle.properties file.

```
accessToekn=jp_XXXXXXXXXXXXXXXXXXXXX

```
Alternatively, you can define it in the `build.gradle` file (app/build.gradle) before using it as follows:

```
def accessToken = "jp_XXXXXXXXXXXXXXXXXXXXXXXX"

...

buildscript {
 ...
```
> We would make use of the `accessToken ` below.

### Installing

Follow the steps below to add velaOffline SDK to your project.

1. Add the JitPack repository to your your poject level `build.gradel` file if it is not added already.

    ```Groove
    allprojects {
        repositories {
            ...
            maven { 
            url 'https://jitpack.io'
            credentials { username accessToken }
             }
            ...
        }
    }
    ```
2. Add VelaOffline dependency:

    Open your app level `build.gradel` and add the velaoffline sdk dependency.
    
    ```
    dependencies {
        ....
        implementation 'org.bitbucket.vela_financial_services:velaobasdk: 0.0.17'
        ...
    }
    ```
3. Sync and build your project.


### Usage 
Follow the instrcution below to configure Vela OBA Offline SDK once you have installed it.

#### Initialize
For this step, you will need to set your `encryption key` and `Base UUSD code`
In your Application onCreate() method, initialize the SDK as shown below:


```
//Create the velaOffline config
val velaOfflineConfig = VelaOfflineConfig.Builder()
                .encryptionKey(BuildConfig.ENCRYPTION_KEY)
                .baseServiceCode(BuildConfig.USSD_BASE_SERVICE)
                .build()
                
VelaOffline.initWithDefaultConfig(this, velaOfflineConfig)

```

#### Activity Usage
For you to be able to carry out `USSD` processing in an `Activity` either as a standalone Activity or an Activity that hosts a `Fragment`, you need to extend the `USSDActivity` as shown below:

##### Java

```java
public class BaseActivity extends USSDActivity{
.../
}
```
##### Kotlin

```Kolin
class BaseActivity : USSDActivity()

```

#### Fragment Usage
Like for Activity, to carry out `USSD` processing in a `Fragment ` or `DialogFragment` you need to extend the `USSDFragment` or `USSDDialogFrament` accordingly and override the necessary methods.

#### Fragment
Java

```Java
public class MyFragment extends USSDFragment()

```
Kotlin

```Kotlin
class MyFragment : USSDFragment()

```

####DialogFragment

Java

```Java
public class MyDialogFragment extends USSDDialogFragment()

```
Kotlin

```Kotlin
class MyDialogFragment : USSDDialogFragment()

```
 

End with an example of getting some data out of the system or using it for a little demo

## Invoking dialUSSD() function
From the USSDActivity(),Fragment() or DialogFragment() that was extended from above, you can invoke the `dialUSSD(String:ussdCode,String:key)`.

`ussdCode: The ussdCode that you wish to run.`

`key: Any random string that is used to retrieve the response from the USSDService.`

> Note: For the `key: String`, it can be any string, this same string is mapped to the response when it is returned. So you have to persist the key as a variable of a constant where you can easily access it and check it against the response from the USSDService.


## Retrieving Result from USSDResponse
Once you invoke the dialUSSD() function, the VelaOfffline module is responsible for making the USSD request, retrieving, decrypting and parsing the response and finally broadcasting the response object to all listeners.

To receive a response from USSD request, you need the key you passed on to the dialUSSD() function. This is to ensure that you are only listening to responses from requests that you initialised.

The `OverlayService` exposes a static variable of type `LiveDate<USSDEvent>` that you can observe from any LifecyleOwner and get notified accordingly.

The USSDEvent class is shown below: 

```Kotlin
data class USSDEvent(val content: String, val key: String)

```
```Java
OverlayService.getUSSDEvent().observe(this, new Observer<Event<USSDEvent>>() {
            @Override
            public void onChanged(Event<USSDEvent> event) {
                final String key = event.peekContent().getKey();
                switch (key) {
                    case `MY_UNIQUE_KEY`:
                    final USSDEvent myEvent = event.getContentIfNotHandled();
                    processUSSDResponse(myEvent);
                    break;
                }


            }
        });
``` 


```Kotlin
OverlayService.USSDEvent.observe(this, Observer { event ->
            Timber.d("OnUSSDEvent: $event")
            val key = event.peekContent().key
            when (key) {
                `MY_UNIQUE_KEY` -> event.getContentIfNotHandled()?.let {
                    processUSSDResponse(it)
                }
            }

        })
```


> Note:  You need to use `peekContent()` to ensure that your interaction with this event does not affect other observers.

Once you verify that the particular USSD response is the response you are interested in by using `peekContent()` and then checking the unique key against the saved key, you can proceed and invoke the `processUSSDResponse(USSDEvent:ussdEvent)`. This function will decrypt, and pass the USSDRespone and invoke the following functions accordingly based on the outcome of the processing:

```Java

@Override
public void onUSSDSuccess(@NotNull String key, @NotNull USSDResponse response) {}

@Override
public void onUSSDError(@Nullable String errorMsg) {}

@Override
public void onUSSDInformation(@NotNull String key, @NotNull USSDResponse response) {}

@Override
public void toggleButtonState(boolean enable) {}

@Override
public void toggleMessageView(boolean show, @Nullable String message, boolean isError) {}

```
```Kotlin
override fun onUSSDSuccess(key: String, response: USSDResponse) {}

override fun onUSSDError(errorMsg: String?) {}

override fun onUSSDInformation(key: String, response: USSDResponse) {}

override fun toggleButtonState(enable: Boolean) {}

override fun toggleMessageView(show: Boolean, message: String?, isError: Boolean) {}
```

### Method/Functions BreakDown

Here we describe what each function does.

1. #### onUssdSuccess()

    This is invoked when the `processUSSDResponse(it)` is finished and everything went fine as expected. It returns the unique `key:String` that was passed during the call to `dialUssd()` function and the `USSDREsponse` object. The `USSDRespone` object is most likely going to be different depending on the expected response from the client API service.
    
2. #### onUssdError(errorMessage:String?)

    This is invoked after the `processUSSDResponse(it)` is finished and an error occurred during the serialization or error returned from the server after deserialization so that you can display the appropriate error message to the user and invoke the next possible action due to the error. It returns a during which is nullable depending on whether the error from the server was successfully captured or not.

3. #### onUssdInformation(key:String, ussdResponse: UssdResonse)

    This is invoked after the `processUssdResposnse(it)` is finished and an information is required to be shown to the user for a successful transaction or for guidance to the next process. It returns the unique `key:String`that was passed during the invocation of `dialUssd(code: String, key: String)` and the `UssdResponse` object.

4. #### toggleButtonState(enable:Boolea)

    This is invoked when the USSD starts to dial and when it finishes, You can enable or disable UI interactions depending on the `enable: Boolean` state to avoid users hitting the dial USSD button twice.

5. #### toggleMessageView(show: Boolean, message: String?, isError: Boolean)

    This method is invoked specifically for UI interaction. It is used to show user `error `, `success ` or `process ` message.
    
## USSD String Builder

To build a USSD string, you can use the internal `UssdRequest.Builder()` `buildUpon()`  method as follows:

Java

```Java
final String fundTRransferUssdCode = new USSDRequest.Builder(Ussd.FUND_TRANSFER_NEW())
                .buildUpon("userId")
                .buildUpon("userPIN")
                .buildUpon("bankAccountId")
                .buildUpon("amount")
                .buildUpon("destAccountNum")
                .buildUpon("destBankCode")
                .buildUpon("saveAsBeneficiary")
                .build();
        Timber.d("UssdString: $fundTRransferUssdCode");
        dialUSSD(fundTRransferUssdCode, Ussd.FUND_TRANSFER_NEW());
```
Kotlin

```Kotlin
val fundTRransferUssdCode = USSDRequest.Builder(Ussd.FUND_TRANSFER_NEW)
                .buildUpon("userId")
                .buildUpon("userPIN")
                .buildUpon("bankAccountId")
                .buildUpon("amount")
                .buildUpon("destAccountNum")
                .buildUpon("destBankCode")
                .buildUpon("saveAsBeneficiary")
                .build()
        Timber.d("UssdString: $fundTRransferUssdCode")
        dialUSSD(fundTRransferUssdCode, Ussd.FUND_TRANSFER_NEW)
```

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

<!--## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.
-->
<!--## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
-->
<!--## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
-->