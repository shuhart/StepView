StepView
======================

A simple animated step view for Android. Backward and forward animation is supported.

<img src="/images/sample.gif" alt="Sample" width="300px" />

Usage
-----

1. Add jcenter() to repositories block in your gradle file.
2. Add `compile 'com.shuhart.stepview:stepview:0.91'` to your dependencies.
2. Add `StepView` into your layouts or view hierarchy.
3. Look into the sample for additional details on how to use and configure the library.

Example:

```xml
<com.shuhart.stepview.StepView
	android:id="@+id/step_view"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:padding="16dp"
	app:selectedCircleColor="@color/colorAccent"
	app:selectedTextColor="@color/colorAccent"
	app:stepLineWidth="1dp"
	app:stepPadding="4dp"
        app:nextTextColor="@color/colorAccent"
	app:nextStepLineColor="@color/colorAccent"
	app:doneCircleColor="@color/colorAccent"
	app:doneStepLineColor="@color/colorAccent"
	app:doneCircleRadius="12dp"
	app:selectedCircleRadius="12dp"
	app:selectedStepNumberColor="@color/colorPrimary"
	app:stepViewStyle="@style/StepView"
	app:doneStepMarkColor="@color/colorPrimary"
	app:stepNumberTextSize="12sp"/>
```

License
=======

    Copyright 2017 Bogdan Kornev.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
