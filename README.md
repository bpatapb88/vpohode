# Dresser
This application is creating to help you get rigth clother set according weather outside, style and colors. Also it will help to manage all your clothers.
* First you need add all clothers in "wardrobe"
* Configure setting according your needs
* Finaly get all possible clothers set by one click "Select an outfit"

<table>
    <tr>
        <td>
            <a href="url"><img src="https://github.com/bpatapb88/vpohode/blob/master/images/mainScreen.png" align="left" height="387" width="180" ></a>
        </td>
        <td>
            <a href="url"><img src="https://github.com/bpatapb88/vpohode/blob/master/images/Wardrobe.png" align="left" height="387" width="180" ></a>
        </td>
        <td>
            <a href="url"><img src="https://github.com/bpatapb88/vpohode/blob/master/images/ItemScreen.png" align="left" height="387" width="180" ></a>
        </td>
        <td>
            <a href="url"><img src="https://github.com/bpatapb88/vpohode/blob/master/images/Settings.png" align="left" height="387" width="180" ></a>
        </td>
        <td>
            <a href="url"><img src="https://github.com/bpatapb88/vpohode/blob/master/images/SelectScreen.png" align="left" height="387" width="180" ></a>
        </td>
    </tr>
</table>




# _
### Used API:
* Google Place API for image of the city on the main page(API key is hidden in build.gradle Module)
* Open Weather Map is used to get information regarding weather

Example of build.gradle Module:
```sh
apply plugin: 'com.android.application'
android {
    compileSdkVersion 30
    buildToolsVersion "30.0.3"
    defaultConfig {
        applicationId "com.simon.vpohode"
        minSdkVersion 23
        targetSdkVersion 30
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        def GOOGLE_API = "GOOGLE_API"
        debug {
            buildConfigField "String", "GOOGLE_API", "\"${API_Key}\""
        }
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    viewBinding {
        enabled = true
    }
}

configurations {
    cleanedAnnotations
    compile.exclude group: 'org.jetbrains' , module:'annotations'
}

dependencies {
    api 'com.google.android.material:material:1.4.0-beta01'
    implementation "androidx.fragment:fragment:1.3.3"
    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'androidx.preference:preference:1.1.1'
    implementation 'com.jaredrummler:colorpicker:1.1.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    testImplementation 'junit:junit:4.13.1'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
    implementation 'com.afollestad.material-dialogs:core:0.9.6.0'
    implementation 'com.github.CanHub:Android-Image-Cropper:3.1.0'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation "androidx.viewpager2:viewpager2:1.0.0"
}

```

Put your google key in ${API_Key}
