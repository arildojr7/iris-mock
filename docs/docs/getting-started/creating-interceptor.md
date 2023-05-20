Once iris mock depencencies are configured, you can start creating your custom interceptors.

## Creating class
Create a new class implementing the [Interceptor](https://square.github.io/okhttp/3.x/okhttp/okhttp3/Interceptor.html){ target=_blank } 
interface and annotate it with `@IrisMockInterceptor`.

Using `irisMockScope(chain)`, all DSL functions will be available:

```kotlin
@IrisMockInterceptor
class MyFirstInterceptor : Interceptor {

    override fun intercept(chain: Chain) = irisMockScope(chain) {
        onGet(endsWith = "/my/endpoint") mockResponse "myCoolJsonResponse"
    }
    
}
```

That's all :sunglasses:

Run your app and you should see the following output on logcat:

```logcatfilter
IrisMock    I    Intercepting: [GET] https://myhost.com/my/endpoint
IrisMock    I    Mocking Response: [GET] https://myhost.com/my/endpoint

```

---

You can create as many interceptors as you like. It's a good idea to split them according to 
responsibilities/context, how to create a `LoginInterceptor`, `ProfileInterceptor` and so on.

---

Need more examples? take a look at the  [:fontawesome-brands-github: app sample](https://github.com/arildojr7/iris-mock/tree/main/sample){ target=_blank }



!!! note ""

    If you configured iris mock for a specific build variant, you need to create the classes according. 
    
    [See more](https://developer.android.com/build/build-variants#sourcesets){ target=_blank }