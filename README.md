# ChatGPTAndroid
Android app using OpenAI's ChatGPT

It is possible to obtain the desired information through conversation or in-depth questions.
Conversation content is managed through a Room Databas.
You can delete the conversation by long-clicking the chatBox. (Based on LongClickEvent)

# Setting
- Android Dolphin l 2021.3.1
- Kotlin

# stack
- Timber
- Room
- Splash Screens
- Coil
- Coroutine
- Retrofit
- ViewPager2

# Main src
```bash
├── .idea
├── app 
│   └── src
│        └── main.java.com.nohjunh.test
│                                    ├── adapter
│                                    ├── database
│                                    ├── model
│                                    ├── network
│                                    ├── repository
│                                    ├── view
│                                    ├── viewModel
│                                    └── App.kt
└── gradle/wrapper

``` 

# API
OpenAI API Documentation
[https://platform.openai.com/docs/api-reference]

Add the key to network/Apis.kt on the project.
Put it in the value of authorization.


# Views
![1gif](https://user-images.githubusercontent.com/75293768/216813346-46de361d-8888-4281-aaf4-ed12c675dc4a.gif)
![2gif](https://user-images.githubusercontent.com/75293768/216813347-4fcaafe9-5bf0-43b6-a4a5-006768a6d07b.gif)
![3gif](https://user-images.githubusercontent.com/75293768/216813350-e7ee8fc0-4144-4596-bd62-a335462d49c8.gif)
