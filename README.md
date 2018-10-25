# Vela OBA

Vela OBA Offline SDK enables you cary out financial transactions and bill pyamnts offline.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

### Usage 
Foollow the instrcution below to configure Vela OBA Offline SDK once you have installed.

#### Initiliaze
For this step, you will need to set your `encrption key` and `Base UUSD code`
In your Application onCreate() method, initiliase the SDK as shown below:


```
//Create the velaOffline config
val velaOfflineConfig = VelaOfflineConfig.Builder()
                .encryptionKey(BuildConfig.ENCRYPTION_KEY)
                .baseServiceCode(BuildConfig.USSD_BASE_SERVICE)
                .build()
                
VelaOffline.initWithDefaultConfig(this, velaOfflineConfig)

```

#### Activity Usage
For you to be able to carry out `USSD` processing in an `Activity` either as a standalone Activity or an Activity that hosts a `Fragment`, you need to extent the `USSDActivity` as show below:

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
Like for Activity, to carry out `USSD` processing in a `Fragment ` or `DialogFragment` you need to extent the `USSDFragment` or `USSDDialogFrament` accordinly and overide the neccessary methods.

#### Fragment
```Java
public class MyFragment extends USSDFragment()

```
```Kotlin
class MyFragment : USSDFragment()

```

####DialogFragment
```Java
public class MyDialogFragment extends USSDDialogFragment()

```
```Kotlin
class MyDialogFragment : USSDDialogFragment()

```
 

End with an example of getting some data out of the system or using it for a little demo

## Invoking dialUSSD() function
From the USSDActivity(),Fragment() or DialogFragment() that was extended from above, you can invoke the `dialUSSD(String:ussdCode,String:key)`.

`ussdCode: The ussdCode that you wish to run.`

`key: Any random string that is used to retrive response from the USSDService.`

> Note: For the `key:String`, it can be any string, this same stringis mapped to the response when it is returned. So you have to persist the key as a variable of a constant where you can easily access it and check it against the response from the USSDService.


## Retrieving Result from USSDResponse
Once you invoke the dialUSSD() function, the VelaOfffline module is responsible for making the USSD request, retreiving, decryting and parsing the response and finally broadcasting the response object to all listeners.

To recieve a response from ussd request, you need the key you passed on to the dialUSSD() function. This is to ensure that you are only lsitening to responses from requests that you initilaised.

The `OverlayService` exposes a static variable of type `LiveDate<USSDEvent>` that you can observe from any LifecyleOwner and get notified accordinlgy.

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

Once you verify that the particlar USSD response is the response you are interested in by usinf `peekContent()` and then checking the unique key against the saved key, you can proceed and invoke the `processUSSDResponse(USSDEvent:ussdEvent)`. This function will decrypt, and pass the USSDRespone and invoke the followinf functions accordinlgy based on the outcome of the processing:

```Java

@Override
public void onUSSDSuccess(@NotNull String menuServiceCode, @NotNull USSDResponse response) {}

@Override
public void onUSSDError(@Nullable String errorMsg) {}

@Override
public void onUSSDInformation(@NotNull String menuServiceCode, @NotNull USSDResponse response) {}

@Override
public void toggleButtonState(boolean enable) {}

@Override
public void toggleMessageView(boolean show, @Nullable String message, boolean isError) {}

```
```Kotlin
override fun onUSSDSuccess(menuServiceCode: String, response: USSDResponse) {}

override fun onUSSDError(errorMsg: String?) {}

override fun onUSSDInformation(menuServiceCode: String, response: USSDResponse) {}

override fun toggleButtonState(enable: Boolean) {}

override fun toggleMessageView(show: Boolean, message: String?, isError: Boolean) {}
```

### Method BreakDown

Explain what each method does here.

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

<!--## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.-->

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
