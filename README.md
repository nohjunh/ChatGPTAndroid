# ChatGPTAndroid
Android app using OpenAI's ChatGPT</br>
It is possible to obtain the desired information through conversation or in-depth questions.

Conversation content is managed through a Room Database.</br>
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

Add the key to network/Apis.kt on the project.</br>
Put it in the value of authorization.

# Views
![gif1](https://user-images.githubusercontent.com/75293768/216814451-59ac513f-452f-4d3b-9418-21654452a1f0.gif)
![gif2](https://user-images.githubusercontent.com/75293768/216814452-a29c92bd-a782-42f1-a332-9d530678ae0d.gif)
![gif3](https://user-images.githubusercontent.com/75293768/216814453-c1ece64d-8f8f-44f2-b11e-5335f258896e.gif)
