<snippet>

[ ![Download](https://api.bintray.com/packages/michaelprimez/maven/SearchableSpinner/images/download.svg) ](https://bintray.com/michaelprimez/maven/SearchableSpinner/_latestVersion)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SearchableSpinner-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5120)

# Searchable Spinner

![Demo SearchableSpinner](https://github.com/michaelprimez/searchablespinner/blob/master/searchablespinner.gif) 

## Usage

Add the dependency to your build.gradle.
```xml
implementation 'gr.escsoft.michaelprimez.searchablespinner:SearchableSpinner:1.0.9'
```

Usage on layout
```xml
<gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
        android:id="@+id/SearchableSpinner"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:layout_marginLeft="24dp"
        android:layout_marginRight="24dp"
        android:layout_marginStart="24dp"
        android:layout_marginEnd="24dp"
        android:gravity="center_horizontal"
        app:StartSearchTintColor="@android:color/white"
        app:DoneSearchTintColor="@android:color/holo_purple"
        app:RevealViewBackgroundColor="@android:color/holo_purple"
        app:SearchViewBackgroundColor="@android:color/secondary_text_dark"
        app:ShowBorders="false"
        app:RevealEmptyText="Touch to select"
        app:SpinnerExpandHeight="300dp"/>
```
## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D ## History

## History
#### Version 1.0.3
* added [get, set] selected item

#### Version 1.0.6
* added Touch outside to dismiss
    
#### Version 1.0.7
* added a status listener

#### Version 1.0.8
* added divider and divider height

# LICENSE 

```
Copyright (C) 2017 Michael Keskinidis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
</snippet>

