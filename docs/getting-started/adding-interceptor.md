Once IrisMock dependency is configured, you can start creating your custom interceptors.

## Create interceptor
Create a new class implementing the [Interceptor](https://square.github.io/okhttp/3.x/okhttp/okhttp3/Interceptor.html){ target=_blank } 
interface.

Using `irisMock(chain)`, all DSL functions will be available:

```kotlin
class MyFirstInterceptor : Interceptor {
    override fun intercept(chain: Chain) = irisMock(chain) {
        onGet(endsWith = "/my/endpoint") mockResponse "myCoolJsonResponse"
    }
}
```

## Add interceptor to IrisMock
You need to add the interceptors to IrisMock `interceptors()` dsl on `Application.onCreate()`:
```kotlin
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startIrisMock {
            enableLogs()
            interceptors(::MyFirstInterceptor)
        }
    }
}
```


Run your app and you should see the following output on logcat:

```logcatfilter
IrisMock    I    Intercepting: [GET] https://myhost.com/my/endpoint
IrisMock    I    Mocking Response: [GET] https://myhost.com/my/endpoint

```

---

You can create as many interceptors as you like. It's a good idea to split them according to 
responsibilities/context, like creating `LoginInterceptor`, `ProfileInterceptor` and so on.

---

Need more examples? take a look at the  [:fontawesome-brands-github: app sample](https://github.com/arildojr7/iris-mock/tree/main/sample){ target=_blank }



!!! note ""

    If you configured IrisMock for a specific build variant, you need to create the classes according. 
    
    [See more](https://developer.android.com/build/build-variants#sourcesets){ target=_blank }